package com.murshid.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.InflectedRepository;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.DictionaryKey;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.InflectedConverter;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.persistence.domain.views.InflectedKey;
import com.murshid.persistence.domain.views.SongWordsToInflectedTable;
import com.murshid.utils.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private InflectedRepository inflectedRepository;
    private SongRepository songRepository;
    private MasterDictionaryService masterDictionaryService;
    private SpellCheckService spellCheckService;

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

    public List<Inflected> getAll(){
        return inflectedRepository.scanAll()
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
            value.put("canonical_hindi", inflected.getMasterDictionaryKey().getHindiWord());
            if (!inflected.isOwnMeaning()) {
                value.put("master_dictionary_key", inflected.getMasterDictionaryKey().toMap());
            }else{
                final DictionaryKey dk = new DictionaryKey().setHindiWord(inflected.getInflectedHindi()).setWordIndex(inflected.getInflectedHindiIndex());
                value.put("master_dictionary_key", dk.toMap());
            }

            result.put(inflected.getKey(), value);
        });
        song.setInflectedEntries(gsonMapper.toJson(result));
        songRepository.save(song);

        return result;
    }

    public Inflected complementMasterDictionaryId(Inflected inflected){
        Optional<MasterDictionary> masterDictionary = masterDictionaryService.findByHindiWordAndWordIndex(inflected.getMasterDictionaryKey().hindiWord, inflected.getMasterDictionaryKey().wordIndex);
        if (masterDictionary.isPresent()){
            inflected.setMasterDictionaryId(masterDictionary.get().getId());
        }else{
            throw new IllegalArgumentException(String.format("master dictionary entry not found for inflected %sलेना-%s", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex()));
        }
        return inflected;
    }

    /**
     * Retrieves all DynamoDB.Inflected entries relevant for a Song.
     * The song is assumed to have the word_list_master member populated
     * @param song         a song model
     * @return             a list (not necessarily ordered) of said master keys
     */
    private List<Inflected> allEntriesForSong(Song song){

        //collect all master keys, without repetition
        Set<InflectedKey> mks = song.getWordListMaster()
                .stream().map(SongWordsToInflectedTable::getInflectedKey)
                .collect(Collectors.toSet());

        return mks.stream()
                .map(mk -> inflectedRepository.findOne(mk.getInflectedHindi(), mk.getInflectedHindiIndex())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        String.format("the not-inflected entry %s-%s in the song, is not in the not_inflected repository ", mk.getInflectedHindi(), mk.getInflectedHindiIndex())))
                ).collect(Collectors.toList());

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
    private Inflected clone(Inflected original, List<Accidence> removeAccidences, List<Accidence> addAccidences, String inflectedHindi){
        Inflected target = (Inflected)original.clone();
        target.getAccidence().removeAll(removeAccidences);
        target.getAccidence().addAll(addAccidences);
        target.setInflectedHindi(inflectedHindi);
        target.setInflectedHindiIndex(suggestNewIndex(inflectedHindi));
        target.setMasterDictionaryId(original.getMasterDictionaryId());
        return target;
    }

    private Inflected clone(Inflected original, List<Accidence> remove, List<Accidence> add, int removeLetters, String addLetters){
        String originalInflected = original.getInflectedHindi();
        String newInflected = originalInflected.substring(0, originalInflected.length()-removeLetters ).concat(addLetters);
        return clone(original, remove, add, newInflected);
    }


    private List<Inflected> explodeSubjunctive(Inflected origin){

        List<Inflected> result = new ArrayList<>();

        if (origin.getInflectedHindi().endsWith("ऊँ")){
            result.add(clone(origin, Lists.newArrayList(Accidence._1ST), Lists.newArrayList(Accidence._2ND), 2, "ए"));
            result.add(clone(origin, Lists.newArrayList(Accidence._1ST), Lists.newArrayList(Accidence._3RD), 2, "ए"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 2, "एँ"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 2, "एँ"));
            result.add(clone(origin, Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 2, "ओ"));
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
        {
            List<Inflected> infinitives = infinitivesInAA(infinitive);
            infinitives.add(infinitive);
            result.addAll(infinitives);
        }

        //verbal root
        {
            Inflected verbRoot = clone(infinitive, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR), Lists.newArrayList(Accidence.VERB_ROOT), 2, "");
            verbRoot.setPartOfSpeech(PartOfSpeech.VERB);
            result.add(verbRoot);
        }


        //absolutives
        {
            Inflected absolutiveRoot = clone(infinitive, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR), Lists.newArrayList(Accidence.ABSOLUTIVE), 2, "");
            absolutiveRoot.setPartOfSpeech(PartOfSpeech.VERB);
            result.add(clone(absolutiveRoot, Collections.emptyList(), Collections.emptyList(), 0, ""));
            result.add(clone(absolutiveRoot, Collections.emptyList(), Collections.emptyList(), 0, "कर"));
            result.add(clone(absolutiveRoot, Collections.emptyList(), Collections.emptyList(), 0, "के"));
        }

        //imperfect participle
        {
            Inflected impPart = clone(infinitive, Collections.emptyList(), Lists.newArrayList(Accidence.IMPERFECTIVE), 2, "ता");
            impPart.setPartOfSpeech(PartOfSpeech.PARTICIPLE);
            result.add(impPart);
            result.addAll(participlesInAA(impPart));
        }

        //perfect participle
        {
            Inflected perfPartRoot = clone(infinitive, Collections.emptyList(), Lists.newArrayList(Accidence.PERFECTIVE), 2, "");
            perfPartRoot.setPartOfSpeech(PartOfSpeech.PARTICIPLE);
            if (!WordUtils.endsWithVowel(perfPartRoot.getInflectedHindi())) {
                perfPartRoot = clone(perfPartRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PERFECTIVE), 0, "ा");
            }else {
                perfPartRoot = clone(perfPartRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PERFECTIVE), 0, "या");
            }
            result.add(perfPartRoot);
            result.addAll(participlesInAA(perfPartRoot));
        }

        //verbal nouns
        {
            Inflected verbalNoun = clone(infinitive, Collections.emptyList(), Collections.emptyList(), 2, "ने वाला");
            verbalNoun.setPartOfSpeech(PartOfSpeech.VERBAL_NOUN);
            result.add(verbalNoun);
            result.addAll(participlesInAA(verbalNoun));
        }

        //subjunctive
        {
            Inflected subjunctiveRoot = clone(infinitive, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT), Lists.newArrayList(Accidence.SUBJUNCTIVE), 2, "");
            subjunctiveRoot.setPartOfSpeech(PartOfSpeech.VERB);
            if (!WordUtils.endsWithVowel(subjunctiveRoot.getInflectedHindi())) {
                result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._1ST), 0, "ूँ"));
                result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._2ND), 0, "े"));
                result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._3RD), 0, "े"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "ें"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "ें"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ो"));
            }else{
                result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._1ST), 0, "ऊँ"));
                result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._2ND), 0, "ए"));
                result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._3RD), 0, "ए"));
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
                inflectMasculineFutureRootNotVocalic(result, futureRootMasc);
            }else{
                inflectMasculineFutureRootVocalic(result, futureRootMasc);
            }
        }

        {
            Inflected futureRootFem = clone(infinitive, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE), Lists.newArrayList(Accidence.FUTURE, Accidence.FEMININE), 2, "");
            futureRootFem.setPartOfSpeech(PartOfSpeech.VERB);
            if (!WordUtils.endsWithVowel(futureRootFem.getInflectedHindi())) {
                inflectFeminineFutureRootNotVocalic(result, futureRootFem);
            }else{
                inflectFeminineFutureRootVocalic(result, futureRootFem);
            }
        }

        {
            Inflected imperativeRoot = clone(infinitive, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE), Lists.newArrayList(Accidence.IMPERATIVE), 2, "");
            imperativeRoot.setPartOfSpeech(PartOfSpeech.VERB);
            if (!WordUtils.endsWithVowel(imperativeRoot.getInflectedHindi())) {
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, ""));
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ो"));
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "िये"));
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "िए"));
            }else {
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, ""));
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ओ"));
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "इये"));
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "इए"));
            }
        }

        return result;

    }

    private void inflectFeminineFutureRootVocalic(List<Inflected> result, Inflected futureRootFem) {
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), 0, "ऊँगी"));
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, "एगी"));
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._3RD), 0, "एगी"));
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "एँगी"));
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ओगी"));
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "एँगी"));
    }

    private void inflectMasculineFutureRootVocalic(List<Inflected> result, Inflected futureRootMasc) {
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), 0, "ऊँगा"));
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, "एगा"));
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._3RD), 0, "एगा"));
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "एँगे"));
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ओगे"));
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "एँगे"));
    }

    private List<Inflected> futures(Inflected origin){
        String inflectedHindi = origin.getInflectedHindi();
        Preconditions.checkArgument(inflectedHindi.endsWith("ूँगा") || inflectedHindi.endsWith("ऊँगा"));
        List<Inflected> result = Lists.newArrayList();

        boolean rootInVowel = !origin.getInflectedHindi().endsWith("ूँगा");
        Inflected futureRootMasc = clone(origin, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR, Accidence._1ST), Collections.emptyList(), 4, "");
        if (!rootInVowel) {
            inflectMasculineFutureRootNotVocalic(result, futureRootMasc);
        }else{
            inflectMasculineFutureRootVocalic(result, futureRootMasc);
        }

        Inflected futureRootFem = clone(origin, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE, Accidence._1ST), Lists.newArrayList(Accidence.FEMININE), 4, "");
        futureRootFem.setPartOfSpeech(PartOfSpeech.VERB);
        if (!WordUtils.endsWithVowel(futureRootFem.getInflectedHindi())) {
            inflectFeminineFutureRootNotVocalic(result, futureRootFem);
        }else{
            inflectFeminineFutureRootVocalic(result, futureRootFem);
        }

        return result;

    }

    private void inflectFeminineFutureRootNotVocalic(List<Inflected> result, Inflected futureRootFem) {
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), 0, "ूँगी"));
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, "ेगी"));
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._3RD), 0, "ेगी"));
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "ेंगी"));
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ोगी"));
        result.add(clone(futureRootFem, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "ेंगी"));
    }

    private void inflectMasculineFutureRootNotVocalic(List<Inflected> result, Inflected futureRootMasc) {
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._1ST), 0, "ूँगा"));
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, "ेगा"));
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._3RD), 0, "ेगा"));
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "ेंगे"));
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ोगे"));
        result.add(clone(futureRootMasc, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "ेंगे"));
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

        result.add(clone( feminine, Collections.emptyList(), Collections.emptyList(), 1, "ी"));
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

            result.add(clone(feminine, Collections.emptyList(), Collections.emptyList(), 0, "ई"));
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

            result.add(clone(feminine, Collections.emptyList(), Collections.emptyList(), 1, "ी"));
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
     * Validates a group of "exploded inflected" against spell_check, to verify if they have Urdu counterpart.
     * Some like vocative plural can be added on the fly, based on the oblique plural.
     * @param inflectedList     the list of Inflected to validate
     * @return                  true if all have Urdu counterparts (or they have been added), false otherwise
     */
    public boolean validateSpellCheckIngroupWithSupplement(List<Inflected> inflectedList){
        List<Inflected> notInSpellChecker = validateSpellCheckIngroup(inflectedList);
        if (notInSpellChecker.isEmpty()){
            return true;
        }else{
            List<Inflected> vocativePlurals = notInSpellChecker.stream().filter(inf -> inf.getAccidence().containsAll(Lists.newArrayList(Accidence.VOCATIVE, Accidence.PLURAL))).collect(Collectors.toList());
            if (vocativePlurals.size() >1){
                return false;
            }
            Inflected vocativePlural = vocativePlurals.get(0);

            List<Inflected> obliquePlurals = inflectedList.stream().filter(inf -> inf.getAccidence().containsAll(Lists.newArrayList(Accidence.OBLIQUE, Accidence.PLURAL))).collect(Collectors.toList());
            if (obliquePlurals.size() >1){
                return false;
            }
            Inflected obliquePlural = obliquePlurals.get(0);

            String vocativePluralString = vocativePlural.getInflectedHindi();
            String obliquePluralString = obliquePlural.getInflectedHindi();
            String obliquePluralStringUrdu = obliquePlural.getInflectedUrdu();

            if (obliquePluralString.substring(0, obliquePluralString.length()-1).equals(vocativePluralString)){
                String vocativePluralStringUrdu =  obliquePluralStringUrdu.substring(0, obliquePluralStringUrdu.length()-1);
                vocativePlural.setInflectedUrdu(vocativePluralStringUrdu);
                LOGGER.info("the vocative plural {} has been supplemented in spell_check", vocativePluralStringUrdu);
                spellCheckService.upsert(vocativePlural.getInflectedHindi(), vocativePluralStringUrdu);
            }
            return true;
        }
    }

    public List<Inflected> validateSpellCheckIngroup(List<Inflected> inflectedList){
        List<Inflected> notInSpellCheck = inflectedList.stream().filter(inf -> !spellCheckService.exists(inf.getInflectedHindi())).collect(Collectors.toList());
        notInSpellCheck.forEach(nisch -> LOGGER.info("the word {} does not have urdu counterpar in spell_check", nisch.getInflectedHindi()));
        return notInSpellCheck;
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

        } else if (origin.getPartOfSpeech() == PartOfSpeech.VERB  && origin.getAccidence().containsAll(Lists.newArrayList(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)) && (hindiWord.endsWith("ूँ") || hindiWord.endsWith("ऊँ")) ){
            result.addAll(  explodeSubjunctive(origin));
        } else if (origin.getPartOfSpeech() == PartOfSpeech.VERB  && origin.getAccidence().containsAll(Lists.newArrayList(Accidence._1ST, Accidence.SINGULAR, Accidence.FUTURE, Accidence.MASCULINE)) && (hindiWord.endsWith("ूँगा") || hindiWord.endsWith("ऊँगा")) ){
            result.addAll(  futures(origin));
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


    private List<Inflected> explodeAsInvariableMascFem(Inflected origin){
        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

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

        Map<String, AttributeValue> expressionAttributeValues =  new HashMap<>();
        expressionAttributeValues.put(":inflectedWord", new AttributeValue().withS(inflectedWord));

        ScanRequest scanRequest = new ScanRequest()
        .withTableName("inflected").withFilterExpression( "inflected_hindi = :inflectedWord")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        return scanResult.getItems()
                .stream().map(InflectedConverter::fromAvMap)
                .collect(Collectors.toList());
    }

    public List<Inflected> findByCanonicalWord(@Nonnull String canonicalWord) {

        List<MasterDictionary> masterDictionaries = masterDictionaryService.findByHindiWord(canonicalWord);
        List<Inflected> result = new ArrayList<>();
        masterDictionaries.forEach(md -> {
            Iterator<Item> itInf = inflectedRepository.findByMasterDictionaryId (md.getId());
            while(itInf.hasNext()){
                Item findRes = itInf.next();
                String hindiWordInflected = findRes.getString("inflected_hindi");
                int wordIndexInflected = findRes.getInt("inflected_hindi_index");
                Optional<Inflected> inflectedOpt = inflectedRepository.findOne(hindiWordInflected, wordIndexInflected);
                if (inflectedOpt.isPresent()) {
                    result.add(inflectedOpt.get());
                }else{
                    LOGGER.error("there is an inflected {}-{} whose masterDictionary {}-{} doesn't exist ", hindiWordInflected, wordIndexInflected, md.getHindiWord(), md.getWordIndex());
                }
            }
        });

        return result;
    }

    public boolean exists(String hindiWord, int index){
        return inflectedRepository.findOne(hindiWord, index).isPresent();
    }

    public boolean save(Inflected master){
        try {
            master.setInflectedUrdu(spellCheckService.passMultipleWordsToUrdu(master.getInflectedHindi()));

            inflectedRepository.save(master);
        }catch (RuntimeException ex){
            LOGGER.error("error saving Master entry {}", ex.getMessage());
            return false;
        }
        return true;
    }


    private boolean validateAccidence(Inflected inflected){

        Set<Accidence> accidence = inflected.getAccidence();
        PartOfSpeech partOfSpeech = inflected.getPartOfSpeech();

        if (accidence == null){
            accidence = Collections.emptySet();
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

        if (!keyExistsInMasterDictionary(inflected)){
            LOGGER.info("no master_dictionary key {}-{} exists, as suggested by {}", inflected.getMasterDictionaryKey().getHindiWord(), inflected.getMasterDictionaryKey().getWordIndex(), inflected.getInflectedHindi());
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
    private boolean keyExistsInMasterDictionary(Inflected master) {
        return masterDictionaryService.findByHindiWordAndWordIndex(master.getMasterDictionaryKey().hindiWord, master.getMasterDictionaryKey().wordIndex).isPresent();
    }


    @Inject
    public void setInflectedRepository(InflectedRepository inflectedRepository) {
        this.inflectedRepository = inflectedRepository;
    }

    @Inject
    public void setSongRepository(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Inject
    public void setMasterDictionaryService(MasterDictionaryService masterDictionaryService) {
        this.masterDictionaryService = masterDictionaryService;
    }

    @Inject
    public void setSpellCheckService(SpellCheckService spellCheckService) {
        this.spellCheckService = spellCheckService;
    }


}
