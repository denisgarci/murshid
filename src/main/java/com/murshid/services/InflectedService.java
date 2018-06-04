package com.murshid.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.InflectedRepository;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.CanonicalKey;
import com.murshid.models.DictionaryKey;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.InflectedConverter;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.MurshidEntry;
import com.murshid.persistence.domain.PlattsEntry;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.domain.WikitionaryEntry;
import com.murshid.persistence.domain.views.InflectedKey;
import com.murshid.utils.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class InflectedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InflectedService.class);

    private static Gson gsonMapper = new Gson();

    private  static Set<PartOfSpeech> verbDerivates = Sets.newHashSet(PartOfSpeech.VERB, PartOfSpeech.ABSOLUTIVE, PartOfSpeech.VERBAL_NOUN,
      PartOfSpeech.PARTICIPLE, PartOfSpeech.INFINITIVE, PartOfSpeech.ABSOLUTIVE);

    private  static Set<PartOfSpeech> pronounDerivates = Sets.newHashSet(PartOfSpeech.PRONOUN, PartOfSpeech.POSSESSIVE_PRONOUN, PartOfSpeech.PERSONAL_PRONOUN,  PartOfSpeech.DEMONSTRATIVE_PRONOUN);


    public boolean writeSeveralWithSuggestedIndexes(List<Inflected> inflectedList){
        //then write
        for (Inflected inflectedEntry: inflectedList) {
            inflectedEntry.setInflectedHindiIndex(suggestNewIndex(inflectedEntry.getInflectedHindi()));
            boolean success = save(inflectedEntry);
            if (!success) {
                return false;
            }
        }
        return true;
    }

    /**
     * true if the POS indicated in Master can refer to the POS indicated in the respective disctionarr tables.
     * For example, a participle can refer to a dictionary verb.
     * @param root          the POS in the dictionary table
     * @param derivate      the POS in the Master's entry
     * @return              true if the link is valid, false if not
     */
    public boolean isDerivatePOS(PartOfSpeech root, PartOfSpeech derivate){
        if (root == null){
            LOGGER.info("the POS does not exist in the dictionary table");
            return false;
        }
        switch (root){
            case VERB:
                return verbDerivates.contains(derivate);
            case PRONOUN:
                return pronounDerivates.contains(derivate);
            default:
                return derivate == root;
        }
    }

    public List<Inflected> getAll(){

        ScanRequest scanRequest = new ScanRequest().withTableName("inflected");

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        return scanResult.getItems()
                .stream().map(InflectedConverter::fromAvMap)
                .collect(Collectors.toList());
    }

    /**
     * retrieves all Inflected entries relevant for a song, but instead of in List form, in a Map<String, Object> form
     * that is suitable to be transformed into a Javascript object.
     * Then, writes them in String form into the Song's DynamoDB record.
     *
     * Also returns the Inflected entries retrieved
     *
     * @param song      a Song model
     * @return          a Map<String, Object> similar easily transformable into a JS object
     */
    public Map<String, Object> generateInflectedEntries(Song song){
        List<Inflected> inflectedList = allEntriesForSong(song);
        Map<String, Object> result = new HashMap<>();
        inflectedList.forEach(inflected -> {
            Map<String, Object> value = new HashMap<>();
            value.put("inflected_hindi", inflected.getInflectedHindi());
            value.put("inflected_urdu", inflected.getInflectedUrdu());
            value.put("accidence", inflected.getAccidence());
            value.put("part_of_speech", inflected.getPartOfSpeech());
            value.put("canonical_hindi", inflected.getCanonicalHindi());
            value.put("canonical_urdu", inflected.getCanonicalUrdu());
            List<String> dictionaryKeys = inflected.getCanonicalKeys().stream()
                    .map( CanonicalKey::toKey)
                    .collect(Collectors.toList());
            value.put("canonical_keys", dictionaryKeys);

            result.put(inflected.getKey(), value);
        });
        song.setInflectedEntries(gsonMapper.toJson(result).toString());
        songRepository.save(song);

        return result;
    }

    /**
     * Retrieves all DynamoDB.Inflected entries relevant for a Song.
     * The song is assumed to have the word_list_master member populated
     * @param song         a song model
     * @return             a list (not necessarily ordered) of said master keys
     */
    public List<Inflected> allEntriesForSong(Song song){

        //collect all master keys, without repetition
        Set<InflectedKey> mks = song.getWordListMaster()
                .stream().map(wlm -> wlm.getInflectedKey())
                .collect(Collectors.toSet());

        return mks.stream().map(mk ->
            inflectedRepository.findOne(mk.getInflectedHindi(), mk.getInflectedHindiIndex()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void validateAll(){

        ScanRequest scanRequest = new ScanRequest().withTableName("master");

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        List<Inflected> masters = scanResult.getItems()
                .stream().map(InflectedConverter::fromAvMap)
                .collect(Collectors.toList());



        for(Inflected master: masters){
            LOGGER.info("master=" + master);
            if (!isValid(master)){
                throw new RuntimeException(String.format("master entry invalid hindiWord=%s wordIndex=%s", master.getInflectedHindi(), master.getInflectedHindiIndex()));
            }
            save(master);
        }


    }

    /**
     * Utility method for cloning an inflected entry into another similar one
     * @param original                  the original Inflected entry
     * @param removeAccidences          what Accidence entries to remove
     * @param addAccidences             what Accidence entrues to add
     * @param inflectedHindi            the new Inflected hindi word (the Urud one is retrieved from the database)
     * @return                          the new Inflected instance
     */
    Inflected clone(Inflected original, List<Accidence> removeAccidences, List<Accidence> addAccidences, String inflectedHindi){
        Inflected target = (Inflected)original.clone();
        target.getAccidence().removeAll(removeAccidences);
        target.getAccidence().addAll(addAccidences);
        target.setInflectedHindi(inflectedHindi);
        target.setInflectedHindiIndex(suggestNewIndex(inflectedHindi));
        target.setCanonicalUrdu(spellCheckService.getUrduSpelling(inflectedHindi));
        return target;
    }

    Inflected clone(Inflected original, List<Accidence> remove, List<Accidence> add, int removeLetters, String addLetters){
        String originalInflected = original.getInflectedHindi();
        String newInflected = originalInflected.substring(0, originalInflected.length()-removeLetters ).concat(addLetters);
        Inflected target = clone(original, remove, add, newInflected);
        return target;
    }


    private List<Inflected> explodeSubjunctive(Inflected origin){

        List<Inflected> result = new ArrayList<>();

        if (origin.getInflectedHindi().endsWith("ऊँ")){
            result.add(clone(origin, Lists.newArrayList(Accidence._1ST), Lists.newArrayList(Accidence._2ND), 2, "ए"));
            result.add(clone(origin, Lists.newArrayList(Accidence._1ST), Lists.newArrayList(Accidence._3RD), 2, "ए"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 2, "एँ"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 2, "एँ"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 2, "ऒ"));
        }else {
            result.add(clone(origin, Lists.newArrayList(Accidence._1ST), Lists.newArrayList(Accidence._2ND), 2, "े"));
            result.add(clone(origin, Lists.newArrayList(Accidence._1ST), Lists.newArrayList(Accidence._3RD), 2, "े"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 2, "ें"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 2, "ें"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 2, "ो"));
        }

        return result;
    }


    public boolean isInfinitiveMasculineSingularDirect(Inflected inflected){
        Set<Accidence> expected = Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
        if (!Sets.difference(inflected.getAccidence(), expected).isEmpty()){
            LOGGER.info("the accidence has to be MASCULINE, DIRECT, SINGULAR");
            return false;
        }else if (!inflected.getPartOfSpeech().equals(PartOfSpeech.INFINITIVE)){
            LOGGER.info("the PartOfSpeech is not an INFINITIVE");
            return false;
        }
        return true;
    }

    /**
     * Takes an infinitive and creates inflected verb verbal forms for:
     * - infinitives
     * - verb root
     * - absolutives
     * - perfect and imperfect participles
     * - verbal nouns
     * - subjunctive
     * - participle
     *
     * @param infinitive        an infinitive form in masculine, singular, direct
     * @return                  A list of all those derivate forms, including the original infinitive
     */
    public List<Inflected> explodeAllVerbs(Inflected infinitive){
        List<Inflected> result = new ArrayList<>();

        //infinitives
        List<Inflected> infinitives = infinitivesInAA(infinitive);
        infinitives.add(infinitive);
        result.addAll(infinitives);

        //verbal root
        {
            Inflected verbRoot = clone(infinitive, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR), Lists.newArrayList(Accidence.VERB_ROOT), 2, "");
            verbRoot.setPartOfSpeech(PartOfSpeech.VERB);
            result.add(verbRoot);
        }


        //absolutives
        {
            Inflected absolutiveRoot = clone(infinitive, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR), Lists.newArrayList(), 2, "");
            absolutiveRoot.setPartOfSpeech(PartOfSpeech.ABSOLUTIVE);
            result.add(clone(absolutiveRoot, Lists.newArrayList(), Lists.newArrayList(), 0, ""));
            result.add(clone(absolutiveRoot, Lists.newArrayList(), Lists.newArrayList(), 0, "कर"));
            result.add(clone(absolutiveRoot, Lists.newArrayList(), Lists.newArrayList(), 0, "के"));
        }

        //imperfect participle
        {
            Inflected impPart = clone(infinitive, Lists.newArrayList(), Lists.newArrayList(Accidence.IMPERFECTIVE), 2, "ता");
            impPart.setPartOfSpeech(PartOfSpeech.PARTICIPLE);
            result.add(impPart);
            result.addAll(participlesInAA(impPart));
        }

        //perfect participle
        {
            Inflected perfPartRoot = clone(infinitive, Lists.newArrayList(), Lists.newArrayList(Accidence.PERFECTIVE), 2, "");
            perfPartRoot.setPartOfSpeech(PartOfSpeech.PARTICIPLE);
            if (!WordUtils.endsWithVowel(perfPartRoot.getInflectedHindi())) {
                perfPartRoot = clone(perfPartRoot, Lists.newArrayList(), Lists.newArrayList(Accidence.PERFECTIVE), 0, "ा");
            }else {
                perfPartRoot = clone(perfPartRoot, Lists.newArrayList(), Lists.newArrayList(Accidence.PERFECTIVE), 0, "या");
            }
            result.add(perfPartRoot);
            result.addAll(participlesInAA(perfPartRoot));
        }

        //verbal nouns
        {
            Inflected verbalNoun = clone(infinitive, Lists.newArrayList(), Lists.newArrayList(), 2, "नेवाला");
            verbalNoun.setPartOfSpeech(PartOfSpeech.VERBAL_NOUN);
            result.add(verbalNoun);
            result.addAll(participlesInAA(verbalNoun));
        }

        //subjunctive
        {
            Inflected subjunctiveRoot = clone(infinitive, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT), Lists.newArrayList(Accidence.SUBJUNCTIVE), 2, "");
            subjunctiveRoot.setPartOfSpeech(PartOfSpeech.VERB);
            if (!WordUtils.endsWithVowel(subjunctiveRoot.getInflectedHindi())) {
                result.add(clone(subjunctiveRoot, Lists.newArrayList(), Lists.newArrayList(Accidence._1ST), 0, "ूँ"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(), Lists.newArrayList(Accidence._2ND), 0, "े"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(), Lists.newArrayList(Accidence._3RD), 0, "े"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "ें"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "ें"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ो"));
            }else{
                result.add(clone(subjunctiveRoot, Lists.newArrayList(), Lists.newArrayList(Accidence._1ST), 0, "ऊँ"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(), Lists.newArrayList(Accidence._2ND), 0, "ए"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(), Lists.newArrayList(Accidence._3RD), 0, "ए"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "एँ"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "एँ"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ओ"));
            }
        }

        //future
        {
            Inflected futureRootMasc = clone(infinitive, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR), Lists.newArrayList(Accidence.FUTURE), 2, "");
            futureRootMasc.setPartOfSpeech(PartOfSpeech.VERB);

            if (!WordUtils.endsWithVowel(futureRootMasc.getInflectedHindi())) {
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), 0, "ूँगा"));
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, "ेगा"));
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._3RD), 0, "ेगा"));
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "ेंगे"));
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ोगे"));
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "ेंगे"));
            }else{
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), 0, "ऊँगा"));
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, "एगा"));
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._3RD), 0, "एगा"));
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "एँगे"));
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ओगे"));
                result.add(clone(futureRootMasc, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "एँगे"));
            }
        }

        {
            Inflected futureRootFem = clone(infinitive, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE), Lists.newArrayList(Accidence.FUTURE, Accidence.FEMININE), 2, "");
            futureRootFem.setPartOfSpeech(PartOfSpeech.VERB);
            if (!WordUtils.endsWithVowel(futureRootFem.getInflectedHindi())) {
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), 0, "ूँगी"));
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, "ेगी"));
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._3RD), 0, "ेगी"));
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "ेंगी"));
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ोगी"));
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "ेंगी"));
            }else{
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), 0, "ऊँगी"));
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, "एगी"));
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._3RD), 0, "एगी"));
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "एँगी"));
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ओगी"));
                result.add(clone(futureRootFem, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "एँगी"));
            }
        }

        {
            Inflected imperativeRoot = clone(infinitive, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE), Lists.newArrayList(Accidence.IMPERATIVE), 2, "");
            imperativeRoot.setPartOfSpeech(PartOfSpeech.VERB);
            if (!WordUtils.endsWithVowel(imperativeRoot.getInflectedHindi())) {
                result.add(clone(imperativeRoot, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, ""));
                result.add(clone(imperativeRoot, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ो"));
                result.add(clone(imperativeRoot, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "िये"));
                result.add(clone(imperativeRoot, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "िए"));
            }else {
                result.add(clone(imperativeRoot, Lists.newArrayList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, ""));
                result.add(clone(imperativeRoot, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ओ"));
                result.add(clone(imperativeRoot, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "इये"));
                result.add(clone(imperativeRoot, Lists.newArrayList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "इए"));
            }
        }

        return result;

    }

    /**
     * Explodes a masc. sing. direct form into the 11 remaining inflected forms for a participle or infinitive
     * @param origin        the original form
     * @return              a list of all exploded forms, included the nasalized and non-nasalized options in feminine.
     *                      And excluding the original form
     */
    private List<Inflected> infinitivesInAA(Inflected origin){
        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "े"));

        Inflected feminine = clone(origin, Lists.newArrayList(Accidence.MASCULINE), Lists.newArrayList(Accidence.FEMININE), hindiWord);

        result.add(clone( feminine, Lists.newArrayList(), Lists.newArrayList(), 1, "ी"));
        result.add(clone( feminine, Lists.newArrayList( Accidence.DIRECT), Lists.newArrayList( Accidence.OBLIQUE), 1, "ीं"));
        result.add(clone( feminine, Lists.newArrayList( Accidence.DIRECT), Lists.newArrayList( Accidence.OBLIQUE), 1, "ी"));
        result.add(clone( feminine, Lists.newArrayList( Accidence.DIRECT), Lists.newArrayList( Accidence.VOCATIVE), 1, "ीं"));
        result.add(clone( feminine, Lists.newArrayList( Accidence.DIRECT), Lists.newArrayList( Accidence.VOCATIVE), 1, "ी"));
        result.add(clone( feminine, Lists.newArrayList( Accidence.SINGULAR), Lists.newArrayList( Accidence.PLURAL), 1, "ीं"));
        result.add(clone( feminine, Lists.newArrayList( Accidence.SINGULAR), Lists.newArrayList( Accidence.PLURAL), 1, "ी"));
        result.add(clone( feminine, Lists.newArrayList( Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList( Accidence.PLURAL, Accidence.OBLIQUE), 1, "ीं"));
        result.add(clone( feminine, Lists.newArrayList( Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList( Accidence.PLURAL, Accidence.OBLIQUE), 1, "ी"));
        result.add(clone( feminine, Lists.newArrayList( Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList( Accidence.PLURAL, Accidence.VOCATIVE), 1, "ीं"));
        result.add(clone( feminine, Lists.newArrayList( Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList( Accidence.PLURAL, Accidence.VOCATIVE), 1, "ी"));

        return result;

    }

    private List<Inflected> participlesInAA(Inflected origin){
        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        if (origin.getAccidence().contains(Accidence.PERFECTIVE) && hindiWord.endsWith("या")) {
            result.add(clone(origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), 2, "ए"));
            result.add(clone(origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), 2, "ए"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 2, "ए"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 2, "ए"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 2, "ए"));

            Inflected feminine = clone(origin, Lists.newArrayList(Accidence.MASCULINE), Lists.newArrayList(Accidence.FEMININE), 2, "");

            result.add(clone(feminine, Lists.newArrayList(), Lists.newArrayList(), 0, "ई"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), 0, "ईं"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), 0, "ई"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), 0, "ईं"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), 0, "ई"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 0, "ईं"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 0, "ई"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 0, "ईं"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 0, "ई"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 0, "ईं"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 0, "ई"));

        }else {

            result.add(clone(origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), 1, "े"));
            result.add(clone(origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), 1, "े"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, "े"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "े"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "े"));

            Inflected feminine = clone(origin, Lists.newArrayList(Accidence.MASCULINE), Lists.newArrayList(Accidence.FEMININE), hindiWord);

            result.add(clone(feminine, Lists.newArrayList(), Lists.newArrayList(), 1, "ी"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), 1, "ीं"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), 1, "ी"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), 1, "ीं"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), 1, "ी"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, "ीं"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, "ी"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "ीं"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "ी"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "ीं"));
            result.add(clone(feminine, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "ी"));
        }

        return result;

    }


    /**
     * Returns some basic exploding, starting from a canonical form
     * @param origin        the canonical form
     * @return              a list of exploded forms, including the original
     */
    public List<Inflected> explode(Inflected origin){
        origin.setInflectedHindiIndex(suggestNewIndex(origin.getInflectedHindi()));
        List<Inflected> result = new ArrayList<>();
        result.add(origin);
        String hindiWord = origin.getInflectedHindi();

        if (origin.getPartOfSpeech() == PartOfSpeech.NOUN && isMasculineSingularDirect(origin) &&  hindiWord.endsWith("ा")){

            result.addAll(explodeMasculinesInAA(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.PARTICIPLE  && hindiWord.endsWith("ा") && isMasculineSingularDirect(origin)){
            result.addAll(participlesInAA(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.INFINITIVE  && hindiWord.endsWith("ा")&& isMasculineSingularDirect(origin)){
            result.addAll(infinitivesInAA(origin));

        }else if (
                (origin.getPartOfSpeech() == PartOfSpeech.ADJECTIVE || origin.getPartOfSpeech() == PartOfSpeech.POSSESSIVE_PRONOUN || origin.getPartOfSpeech() == PartOfSpeech.ADVERB) && hindiWord.endsWith("ा")
                && isMasculineSingularDirect(origin)){

            result.addAll(genericExplode(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && isMasculineSingularDirect(origin) && !hindiWord.endsWith("ा") && !hindiWord.endsWith("ू") && !hindiWord.endsWith("ी")
                && isMasculineSingularDirect(origin)){

                    result.addAll(explodeMasculinesNotInAAorUUorII(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && isMasculineSingularDirect(origin) && hindiWord.endsWith("ू") ){

            result.addAll(explodeMasculinesInUU(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && isMasculineSingularDirect(origin) && hindiWord.endsWith("ी") ){

            result.addAll(explodeMasculinesInII(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && isFeminineSingularDirect(origin)  && hindiWord.endsWith("ई") ){

            result.addAll(explodeFemininesInIIIsolated(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && isFeminineSingularDirect(origin) && (!hindiWord.endsWith("ी") ) ){

            result.addAll(explodeFemininesNotInII(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && isFeminineSingularDirect(origin) && (hindiWord.endsWith("ी")) ){

            result.addAll(explodeFemininesInII(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.VERB  && origin.getAccidence().containsAll(Lists.newArrayList(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)) && (hindiWord.endsWith("ूँ")) ){
            result.addAll(explodeSubjunctive(origin));
        } else if (origin.getPartOfSpeech() == PartOfSpeech.ADJECTIVE && isMasculineSingularDirect(origin) && !origin.getInflectedHindi().endsWith("ा")){
            result.addAll(explodeAsInvariableMascFem(origin));
        }else if (origin.getPartOfSpeech() == PartOfSpeech.POSTPOSITION && isMasculineSingularDirect(origin)){
            result.addAll(explodeAsInvariableMascFem(origin));
        }


        return result;
    }

    private boolean isMasculineSingularDirect(Inflected inflected){
        if (inflected.getAccidence() == null){
            return false;
        }else {
          return inflected.getAccidence().containsAll(Lists.newArrayList(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT));
        }
    }

    private boolean isFeminineSingularDirect(Inflected inflected){
        if (inflected.getAccidence() == null){
            return false;
        }else {
            return inflected.getAccidence().containsAll(Lists.newArrayList(Accidence.FEMININE, Accidence.SINGULAR, Accidence.DIRECT));
        }
    }


    private List<Inflected> genericExplode(Inflected origin){
        List<Inflected> result = new ArrayList<>();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "े"));

        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE), Lists.newArrayList(Accidence.FEMININE), 1, "ी"));
        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT), Lists.newArrayList(Accidence.FEMININE, Accidence.OBLIQUE), 1, "ी"));
        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT), Lists.newArrayList(Accidence.FEMININE, Accidence.VOCATIVE), 1, "ी"));
        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE, Accidence.SINGULAR), Lists.newArrayList(Accidence.FEMININE, Accidence.PLURAL), 1, "ी"));
        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.FEMININE, Accidence.PLURAL, Accidence.OBLIQUE), 1, "ी"));
        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.FEMININE, Accidence.PLURAL, Accidence.VOCATIVE), 1, "ी"));

        return result;
    }


    /**
     * Parses DynameDB.Inflected in search of the minumum possible next index for that inflected word
     * @param inflectedIndex
     * @return
     */
    public int suggestNewIndex(String inflectedIndex){
        int index = -1;
        Iterator<Item> it = inflectedRepository.findByInflectedWord(inflectedIndex);
        while(it.hasNext()){
            Item item = it.next();
            index = Math.max(index, item.getInt("inflected_hindi_index"));
        }
        return index + 1;
    }



    private List<Inflected> explodeMasculinesNotInAAorUUorII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), hindiWord.concat("ों")));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), hindiWord.concat("ो")));

        return result;

    }

    /**
     * Explodes a masc. sing. direct form into the 11 remaining invariable forms for an adjective
     * @param origin
     * @return
     */
    private List<Inflected> explodeAsInvariableMascFem(Inflected origin){
        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();
        int index = origin.getInflectedHindiIndex();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), hindiWord));

        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE), Lists.newArrayList(Accidence.FEMININE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT), Lists.newArrayList(Accidence.FEMININE, Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT), Lists.newArrayList(Accidence.FEMININE, Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE, Accidence.SINGULAR), Lists.newArrayList(Accidence.FEMININE, Accidence.PLURAL), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.FEMININE, Accidence.PLURAL, Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.FEMININE, Accidence.PLURAL, Accidence.VOCATIVE), hindiWord));

        return result;

    }


    private List<Inflected> explodeMasculinesInAA(Inflected origin){
        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();
        int index = origin.getInflectedHindiIndex();


        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, "े"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "ों"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "ो"));

        return result;

    }

    private List<Inflected> explodeMasculinesInII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "ियों"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "ियो"));

        return result;

    }

    private List<Inflected> explodeMasculinesInUU(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "ुओं"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "ुओ"));

        return result;

    }

    private List<Inflected> explodeFemininesNotInII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), hindiWord.concat("ें")));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), hindiWord.concat("ों")));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), hindiWord.concat("ो")));

        return result;

    }

    private List<Inflected> explodeFemininesInII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, "ियाँ"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "ियों"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "ियो"));

        return result;

    }

    private List<Inflected> explodeFemininesInIIIsolated(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, "इयाँ"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "इयों"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "इयो"));

        return result;

    }




    public List<Inflected> getByInflectedWord(@Nonnull String inflectedWord) {

        Map<String, AttributeValue> expressionAttributeValues =  new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":inflectedWord", new AttributeValue().withS(inflectedWord));

        ScanRequest scanRequest = new ScanRequest()
        .withTableName("inflected").withFilterExpression( "inflected_hindi = :inflectedWord")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        return scanResult.getItems()
                .stream().map(InflectedConverter::fromAvMap)
                .collect(Collectors.toList());
    }

    /**
     * True if some of the proposed inflected, that I want to write in Dynamos'inflected,
     * are already in the database
     */
    public boolean duplicatesInflected(String canonicalWord, List<Inflected> exploded){
        Set<Inflected> explodedSet = Sets.newHashSet(exploded);
        Set<Inflected> inflectedSetInRepo = Sets.newHashSet(findByCanonicalWord(canonicalWord));

        Set<Inflected> intersection = Sets.intersection(explodedSet, inflectedSetInRepo);
        if (!intersection.isEmpty()){
            LOGGER.info("some of the proposed, exploded Inflected are already in the database ");
            intersection.forEach(in -> LOGGER.info(in.toString()));
            return true;
        }
        return false;
    }

    public List<Inflected> findByCanonicalWord(@Nonnull String canonicalWord) {

        Iterator<Item> items = inflectedRepository.findByCanonicalWord(canonicalWord);

        List<Inflected> result = new ArrayList<>();
        items.forEachRemaining(it ->{
            Optional<Inflected> master = inflectedRepository.findOne(it.getString("inflected_hindi"), it.getInt("inflected_hindi_index"));
            if(master.isPresent()){
                result.add(master.get());
            }
        });
        return result;
    }

    public boolean exists(String hindiWord, int index){
        return inflectedRepository.findOne(hindiWord, index).isPresent();
    }

    public boolean save(Inflected master){
        try {
            master.setInflectedUrdu(spellCheckService.getUrduSpelling(master.getInflectedHindi()));
            master.setCanonicalUrdu(spellCheckService.getUrduSpelling(master.getCanonicalHindi()));

            inflectedRepository.save(master);
        }catch (RuntimeException ex){
            LOGGER.error("error saving Master entry {}", ex.getMessage());
            return false;
        }
        return true;
    }


    public boolean validateAccidence(Inflected inflected){

        Set<Accidence> accidence = inflected.getAccidence();
        PartOfSpeech partOfSpeech = inflected.getPartOfSpeech();

        if (accidence == null){
            accidence = Collections.EMPTY_SET;
        }
        boolean hasGender = accidence.contains(Accidence.MASCULINE) || accidence.contains(Accidence.FEMININE);
        boolean hasNumber = accidence.contains(Accidence.SINGULAR) || accidence.contains(Accidence.PLURAL);
        boolean hasCase = accidence.contains(Accidence.DIRECT) || accidence.contains(Accidence.OBLIQUE) || accidence.contains(Accidence.VOCATIVE);
        boolean hasAspect = accidence.contains(Accidence.PERFECTIVE) || accidence.contains(Accidence.IMPERFECTIVE);
        boolean hasPerson = accidence.contains(Accidence._1ST) || accidence.contains(Accidence._2ND) || accidence.contains(Accidence._3RD);
        //"subjunctive" and "imperative" are tenses to this effect
        boolean hasTense = accidence.contains(Accidence.PRESENT) || accidence.contains(Accidence.FUTURE) || accidence.contains(Accidence.PAST) || accidence.contains(Accidence.PLUSQUAMPERFECT) || accidence.contains(Accidence.SUBJUNCTIVE) || accidence.contains(Accidence.IMPERATIVE);

        if (partOfSpeech == PartOfSpeech.NOUN || partOfSpeech == PartOfSpeech.ADJECTIVE){
            if (hasGender && hasNumber && hasCase && accidence.size() == 3) {
                return true;
            }else{
                LOGGER.info("accidence validation failure: parts of speech NOUN and ADJECTIVE have to have exactly gender, number and case");
                return false;
            }
        }

        if (partOfSpeech == PartOfSpeech.PARTICIPLE){
            if (hasGender && hasNumber && hasAspect && hasCase && accidence.size()  == 4){
                return true;
            }else{
                LOGGER.error("accidence validation failure: accidence validation error:part of speech PARTICIPLE in {} has to have exactly gender, number, aspect, and case", inflected);
                return false;
            }
        }


        if (partOfSpeech == PartOfSpeech.VERB){
            if (accidence.equals(Sets.newHashSet(Accidence.VERB_ROOT))) {
                return true;
            }else if (!hasPerson || !hasNumber || !hasTense){
                LOGGER.info("accidence validation failure: part of speech VERB in {} has to have at least person, number and tense", inflected);
                return false;
            }
            return true;
        }

        if (partOfSpeech == PartOfSpeech.INFINITIVE){
            if ( hasGender &&  hasNumber && hasCase && accidence.size() == 3) {
                return true;
            }else{
                LOGGER.info("accidence validation failure: part of speech INFINITIVE in {} has to have number, gender and case", inflected);
                return false;
            }
        }

        if (partOfSpeech == PartOfSpeech.VERBAL_NOUN){
            if ( hasGender &&  hasNumber && hasCase && accidence.size() == 3) {
                return true;
            }else{
                LOGGER.info("accidence validation failure: part of speech VERBAL_NOUN in {} has to have number, gender and case", inflected);
                return false;
            }
        }

        return true;

    }

    public boolean isValid(Inflected inflected) {
        if (inflected.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (inflected.getInflectedHindi() == null) {
            LOGGER.info("inflected hindi cannot be null");
            return false;
        }

        if (!spellCheckService.exists(inflected.getInflectedHindi())){
            LOGGER.info("the inflected hindi word {} does not exists in spell_check ", inflected.getInflectedHindi());
            return false;
        }

        if (inflected.getPartOfSpeech() == null) {
            LOGGER.info("part of speech cannot be null");
            return false;
        }

        if (!validateCanonicalKeys(inflected)){
            LOGGER.info("some of the canonical keys are not present or incorrect");
            return false;
        }

        if (!validateAccidence(inflected)){
            LOGGER.info("inadequate accidence for the POS");
            return false;
        }

        return true;
    }

    /**
     * checks if all canonical keys contained in Master (if any)
     * really exist in the respective entities
     * @param master    the Master record
     * @return          true if all canonical keys exist, false otherwise
     */
    public boolean validateCanonicalKeys(Inflected master){

        if (master.getCanonicalKeys() == null){
            return true;
        }
        for (CanonicalKey ck: master.getCanonicalKeys()){
            DictionaryKey dk = new DictionaryKey().setHindiWord(ck.canonicalWord).setWordIndex(ck.canonicalIndex);

            switch (ck.dictionarySource){
                case PLATTS:
                    Optional<PlattsEntry> plattsEntry = plattsService.findOne(dk);
                    if (!plattsEntry.isPresent()){
                        LOGGER.info("the PRATTS canonical entry hindiWordIndex={} hindiWordIndex={} indicated in Master does not exist", master.getInflectedHindi(), master.getInflectedHindiIndex());
                        return false;
                    }else if (!isDerivatePOS(plattsEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in PRATTS ({})", master.getPartOfSpeech(), plattsEntry.get().getPartOfSpeech());
                        return false;
                    }
                    break;
                case MURSHID:
                    Optional<MurshidEntry> gonzaloEntry = murshidService.findOne(dk);
                    if (!gonzaloEntry.isPresent()){
                        LOGGER.info("the MURSHID canonical entry hindiWordIndex={} hindiWordIndex={} indicated in Master does not exist", master.getInflectedHindi(), master.getInflectedHindiIndex());
                        return false;
                    }else if (!isDerivatePOS(gonzaloEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in MURSHID ({})", master.getPartOfSpeech(), gonzaloEntry.get().getPartOfSpeech());
                        return false;
                    }
                    break;
                case REKHTA:
                    Optional<RekhtaEntry> rekhtaEntry = rekhtaService.findOne(dk);
                    if (!rekhtaEntry.isPresent()){
                        LOGGER.info("the REKHTA canonical entry hindiWordIndex={} hindiWordIndex={} indicated in Master does not exist", master.getInflectedHindi(), master.getInflectedHindiIndex());
                        return false;
                    }else if (!isDerivatePOS(rekhtaEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in REKHTA ({})", master.getPartOfSpeech(), rekhtaEntry.get().getPartOfSpeech());
                        return false;
                    }
                    break;
                case WIKITIONARY:
                    Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(dk);
                    if (!wikitionaryEntry.isPresent()){
                        LOGGER.info("the WIKITIONARY canonical entry hindiWordIndex={} hindiWordIndex={} indicated in Master does not exist", master.getInflectedHindi(), master.getInflectedHindiIndex());
                        return false;
                    }else if (!isDerivatePOS(wikitionaryEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in WIKITIONARY ({})", master.getPartOfSpeech(), wikitionaryEntry.get().getPartOfSpeech());
                        return false;
                    }
                    break;
            }

        };
        return true;
    }

    @Inject
    private InflectedRepository inflectedRepository;

    @Inject
    private SongRepository songRepository;


    @Inject
    private WikitionaryService wikitionaryService;

    @Inject
    private PlattsService plattsService;

    @Inject
    private MurshidService murshidService;

    @Inject
    private RekhtaService rekhtaService;

    @Inject
    private SpellCheckService spellCheckService;



}
