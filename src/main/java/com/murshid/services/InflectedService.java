package com.murshid.services;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.HasInflectedHindi;
import com.murshid.persistence.domain.Inflected;
import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.persistence.domain.views.InflectedKey;
import com.murshid.persistence.domain.views.InflectedView;
import com.murshid.persistence.domain.views.SongWordsToInflectedTable;
import com.murshid.persistence.repo.InflectedRepositoryDB;
import com.murshid.utils.WordUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Named
public class InflectedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InflectedService.class);

    private static Gson gsonMapper = new Gson();

    private InflectedRepositoryDB inflectedRepository;
    private SongRepository songRepository;
    private MasterDictionaryService masterDictionaryService;
    private SpellCheckService spellCheckService;

    public boolean writeSeveralWithSuggestedIndexes(List<Inflected> inflectedList){
        //then write
        for (Inflected inflectedEntry: inflectedList) {
            inflectedEntry.getInflectedKey().setInflectedHindiIndex(suggestNewIndex(inflectedEntry.getInflectedKey().getInflectedHindi()));
            boolean success = save(inflectedEntry);
            if (!success) {
                return false;
            }
        }
        return true;
    }

    /**
     * retrieves all InflectedRepositoryDB entries relevant for a song, but instead of in List form, in a Map<String, Object> form
     * that is suitable to be transformed into a Javascript object.
     * Then, writes them in String form into the Song's DynamoDB record.
     *
     * Also returns the InflectedRepositoryDB entries retrieved
     *
     * @param song      a Song model
     * @return          a Map<String, Object> similar easily transformable into a JS object
     */
    public Map<String, Object> generateInflectedEntries(Song song){
        List<Inflected> inflectedList = allEntriesForSong(song);
        Map<String, Object> result = new HashMap<>();
        inflectedList.forEach(inflected -> {
            Map<String, Object> value = new HashMap<>();
            value.put("inflected_hindi", inflected.getInflectedKey().getInflectedHindi());
            value.put("inflected_urdu", inflected.getInflectedUrdu());
            value.put("accidence", inflected.getAccidence());
            value.put("part_of_speech", inflected.getPartOfSpeech());
            value.put("master_dictionary_key", ImmutableMap.of(
                    "hindi_word", inflected.getInflectedKey().getInflectedHindi(),
                    "word_index", inflected.getInflectedKey().inflectedHindiIndex));

            if (!inflected.isOwnMeaning()) {
                value.put("canonical_hindi", inflected.getMasterDictionary().getHindiWord());
            }else{
                value.put("canonical_hindi", inflected.getCanonicalHindi());
            }

            result.put(inflected.getKey(), value);
        });
        song.setInflectedEntries(gsonMapper.toJson(result));
        songRepository.save(song);

        return result;
    }

    /**
     * Retrieves all DynamoDB.InflectedRepositoryDB entries relevant for a Song.
     * The song is assumed to have the word_list_master member populated
     * @param song         a song model
     * @return             a list (not necessarily ordered) of said master keys
     */
    private List<Inflected> allEntriesForSong(Song song){

        //collect all master keys, without repetition
        Set<InflectedKey> mks = song.getWordListMaster()
                .stream().map(SongWordsToInflectedTable::getInflectedKey)
                //we filter the null/-1, because they are just placeholders for the sake of structure of keeping inflected entries sequential
                //for example, when only a non-inflected entry accounts for the item that span
                .filter(ik -> ik.getInflectedHindiIndex() != -1)
                .collect(toSet());

        return mks.stream()
                .map(mk -> inflectedRepository.findByInflectedKey_InflectedHindiAndInflectedKey_InflectedHindiIndex(mk.getInflectedHindi(), mk.getInflectedHindiIndex())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        String.format("the inflected entry %s-%s in the song, is not in the inflected repository ", mk.getInflectedHindi(), mk.getInflectedHindiIndex())))
                ).collect(toList());

    }


    /**
     * Utility method for cloning an inflected entry into another similar one
     * @param original                  the original InflectedRepositoryDB entry
     * @param removeAccidences          what Accidence entries to remove
     * @param addAccidences             what Accidence entrues to add
     * @param inflectedHindi            the new InflectedRepositoryDB hindi word (the Urud one is retrieved from the database)
     * @return                          the new InflectedRepositoryDB instance
     */
    private Inflected clone(Inflected original, List<Accidence> removeAccidences, List<Accidence> addAccidences, String inflectedHindi){
        Inflected target = (Inflected)original.clone();
        Set<Accidence> inflectedAccidences = Sets.newHashSet(original.getAccidence());
        inflectedAccidences.removeAll(removeAccidences);
        inflectedAccidences.addAll(addAccidences);
        target.setAccidence(Lists.newArrayList(inflectedAccidences));
        target.getInflectedKey().setInflectedHindi(inflectedHindi);
        target.getInflectedKey().setInflectedHindiIndex(suggestNewIndex(inflectedHindi));
        target.setMasterDictionary(original.getMasterDictionary());
        return target;
    }

    private Inflected clone(Inflected original, List<Accidence> remove, List<Accidence> add, int removeLetters, String addLetters){
        String originalInflected = original.getInflectedKey().getInflectedHindi();
        String newInflected = originalInflected.substring(0, originalInflected.length()-removeLetters ).concat(addLetters);
        return clone(original, remove, add, newInflected);
    }


    private List<Inflected> explodeSubjunctive(Inflected origin){

        List<Inflected> result = new ArrayList<>();

        if (origin.getInflectedKey().getInflectedHindi().endsWith("ऊँ")){
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


    public boolean isInfinitiveMasculineSingularDirect(InflectedView inflected){
        Set<Accidence> expected = Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
        if (!Sets.difference(Sets.newHashSet(inflected.getAccidence()), expected).isEmpty()){
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
            if (!WordUtils.endsWithVowel(perfPartRoot.getInflectedKey().getInflectedHindi())) {
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
        explodeSubjunctives(infinitive, result);


        //future
        {
            Inflected futureRootMasc = clone(infinitive, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR), Lists.newArrayList(Accidence.FUTURE), 2, "");
            futureRootMasc.setPartOfSpeech(PartOfSpeech.VERB);

            if (!WordUtils.endsWithVowel(futureRootMasc.getInflectedKey().getInflectedHindi())) {
                inflectMasculineFutureRootNotVocalic(result, futureRootMasc);
            }else{
                inflectMasculineFutureRootVocalic(result, futureRootMasc);
            }
        }

        {
            Inflected futureRootFem = clone(infinitive, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE), Lists.newArrayList(Accidence.FUTURE, Accidence.FEMININE), 2, "");
            futureRootFem.setPartOfSpeech(PartOfSpeech.VERB);
            if (!WordUtils.endsWithVowel(futureRootFem.getInflectedKey().getInflectedHindi())) {
                inflectFeminineFutureRootNotVocalic(result, futureRootFem);
            }else{
                inflectFeminineFutureRootVocalic(result, futureRootFem);
            }
        }
        explodeImperatives(infinitive, result);


        return result;

    }

    @VisibleForTesting
    protected void explodeImperatives(Inflected infinitive, List<Inflected> result) {
        //imperative
        {
            Inflected imperativeRoot = clone(infinitive, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE), Lists.newArrayList(Accidence.IMPERATIVE), 2, "");
            imperativeRoot.setPartOfSpeech(PartOfSpeech.VERB);
            if (!WordUtils.endsWithVowel(imperativeRoot.getInflectedKey().getInflectedHindi())) {
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, ""));
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ो"));
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "िये"));
                result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "िए"));
            }else {

                String root = imperativeRoot.getInflectedKey().getInflectedHindi();
                //roots in -ii and -uu shorten before some imperative endings
                if (root.endsWith("ी")) {
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, ""));
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 1, "िओ"));
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 1, "िये"));
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 1, "िए"));
                }else if (root.endsWith("ू")) {
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, ""));
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 1, "ुओ"));
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 1, "ुये"));
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 1, "ुए"));
                }else{
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.SINGULAR, Accidence._2ND), 0, ""));
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ओ"));
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "इये"));
                    result.add(clone(imperativeRoot, Collections.emptyList(), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "इए"));
                }
            }
        }
    }

    /**
     * Explodes the infinitive into subjunctive forms
     * @param infinitive        the InflectedRepositoryDB object containing the verb infinitive
     * @param result            the list where we put the explosion results
     */
    protected void explodeSubjunctives(Inflected infinitive, List<Inflected> result) {
        //subjunctive
        {
            Inflected subjunctiveRoot = clone(infinitive, Lists.newArrayList(Accidence.MASCULINE, Accidence.DIRECT), Lists.newArrayList(Accidence.SUBJUNCTIVE), 2, "");
            subjunctiveRoot.setPartOfSpeech(PartOfSpeech.VERB);
            if (!WordUtils.endsWithVowel(subjunctiveRoot.getInflectedKey().getInflectedHindi())) {
                result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._1ST), 0, "ूँ"));
                result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._2ND), 0, "े"));
                result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._3RD), 0, "े"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "ें"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "ें"));
                result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ो"));
            }else{
                if (subjunctiveRoot.getInflectedKey().getInflectedHindi().endsWith("ी")){
                    result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._1ST), 1, "िऊँ"));
                    result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._2ND), 1, "िए"));
                    result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._3RD), 1, "िए"));
                    result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 1, "िएँ"));
                    result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 1, "िएँ"));
                    result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 1, "िओ"));
                }else if (subjunctiveRoot.getInflectedKey().getInflectedHindi().endsWith("ू")){
                    result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._1ST), 1, "ुऊँ"));
                    result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._2ND), 1, "ुए"));
                    result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._3RD), 1, "ुए"));
                    result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 1, "ुएँ"));
                    result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 1, "ुएँ"));
                    result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 1, "ुओ"));
                }else {
                    result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._1ST), 0, "ऊँ"));
                    result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._2ND), 0, "ए"));
                    result.add(clone(subjunctiveRoot, Collections.emptyList(), Lists.newArrayList(Accidence._3RD), 0, "ए"));
                    result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._1ST), 0, "एँ"));
                    result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._3RD), 0, "एँ"));
                    result.add(clone(subjunctiveRoot, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL, Accidence._2ND), 0, "ओ"));
                }
            }
        }
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
        String inflectedHindi = origin.getInflectedKey().getInflectedHindi();
        Preconditions.checkArgument(inflectedHindi.endsWith("ूँगा") || inflectedHindi.endsWith("ऊँगा"));
        List<Inflected> result = Lists.newArrayList();

        boolean rootInVowel = !origin.getInflectedKey().getInflectedHindi().endsWith("ूँगा");
        Inflected futureRootMasc = clone(origin, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR, Accidence._1ST), Collections.emptyList(), 4, "");
        if (!rootInVowel) {
            inflectMasculineFutureRootNotVocalic(result, futureRootMasc);
        }else{
            inflectMasculineFutureRootVocalic(result, futureRootMasc);
        }

        Inflected futureRootFem = clone(origin, Lists.newArrayList(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE, Accidence._1ST), Lists.newArrayList(Accidence.FEMININE), 4, "");
        futureRootFem.setPartOfSpeech(PartOfSpeech.VERB);
        if (!WordUtils.endsWithVowel(futureRootFem.getInflectedKey().getInflectedHindi())) {
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
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

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
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

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
     * @param inflectedList     the list of InflectedRepositoryDB to validate
     * @return                  true if all have Urdu counterparts (or they have been added), false otherwise
     */
    public boolean validateSpellCheckIngroupWithSupplement(List<Inflected> inflectedList, int masterDictionaryId){
        List<Inflected> notInSpellChecker = validateSpellCheckIngroup(inflectedList);

        if (notInSpellChecker.isEmpty()){
            return true;
        }


        List<Inflected> allExisting = findByMasterDictionaryId(masterDictionaryId);
        List<Pair<Inflected, Optional<String>>> processed = notInSpellChecker.stream().map(notFound -> spellCheckService.propose(notFound, inflectedList))
                .collect(toList());

        //for those processed that still have no suggestion, make a second pass, but know using not the exploded forms as
        //auxiliars, but the forms already existing in the database.
        processed = processed.stream().map(pr ->{
            if (pr.getRight().isPresent()){
                return pr;
            }else{
                return  spellCheckService.propose(pr.getLeft(), allExisting);
            }
        }).collect(Collectors.toList());

        //we create a map because the proper transliteration for the same hindi word could have been found for some accidences, but not for others
        Map<String, String> hindiToUrdu = processed.stream().filter(pair -> pair.getRight().isPresent())
                .collect(toMap(this::extractInflectedHindi, this::extractString, (v1, v2) -> v1));

        //now, re-process, removing the values whose hindi has counterpart, even if not for the same exact accidence
        List<Pair<Inflected, Optional<String>>> stillEmpty = processed.stream().filter(pr -> !pr.getRight().isPresent()
                && !hindiToUrdu.containsKey(pr.getLeft().getInflectedKey().getInflectedHindi()))
                .collect(toList());

        if ( stillEmpty.isEmpty()){
            //write all that have been suggested
            List<Pair<Inflected, Optional<String>>> writable = processed.stream().filter(pr -> pr.getRight().isPresent())
                    .collect(toList());
            writable.forEach(pr -> spellCheckService.upsert(pr.getLeft().getInflectedKey().getInflectedHindi(), pr.getRight().get()));

            return true;
        }else{
            stillEmpty.forEach(pr -> {
                LOGGER.error("even after proposing, the hindi word {} is not in spell_check", pr.getLeft().getInflectedKey().getInflectedHindi());
            });
            return false;
        }
    }

    private String extractInflectedHindi (Pair<Inflected, Optional<String>> pair){
        return pair.getLeft().getInflectedKey().getInflectedHindi();
    }

    private String extractString(Pair<Inflected, Optional<String>> pair){
        return pair.getRight().get();
    }

    public <T extends HasInflectedHindi> List<T> validateSpellCheckIngroup(List<T> inflectedList){
        List<T> notInSpellCheck = inflectedList.stream().filter(inf -> spellCheckService.wordsDontExist(inf.getInflectedHindi())).collect(toList());
        notInSpellCheck.forEach(nisch -> LOGGER.info("the Hindi word {} does not have Urdu counterpart in spell_check", nisch.getInflectedHindi()));
        return notInSpellCheck;
    }


    /**
     * Returns some basic exploding, starting from a canonical form
     * @param origin        the canonical form
     * @return              a list of exploded forms, including the original
     */
    public List<Inflected> explode(Inflected origin){
        origin.getInflectedKey().setInflectedHindiIndex(suggestNewIndex(origin.getInflectedKey().getInflectedHindi()));
        List<Inflected> result = new ArrayList<>();
        result.add(origin);
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

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
        } else if (origin.getPartOfSpeech() == PartOfSpeech.ADJECTIVE && isMasculineSingularDirect(origin) && !origin.getInflectedKey().getInflectedHindi().endsWith("ा")){
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
        Iterator<Inflected> it = inflectedRepository.findByInflectedKey_InflectedHindi(inflectedIndex).iterator();
        while(it.hasNext()){
            Inflected item = it.next();
            index = Math.max(index, item.getInflectedKey().inflectedHindiIndex);
        }
        return index + 1;
    }



    private List<Inflected> explodeMasculinesNotInAAorUUorII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), hindiWord.concat("ों")));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), hindiWord.concat("ो")));

        return result;

    }


    private List<Inflected> explodeAsInvariableMascFem(Inflected origin){
        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

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
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "ियों"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "ियो"));

        return result;

    }

    private List<Inflected> explodeMasculinesInUU(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "ुओं"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "ुओ"));

        return result;

    }

    private List<Inflected> explodeFemininesNotInII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), hindiWord.concat("ें")));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), hindiWord.concat("ों")));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), hindiWord.concat("ो")));

        return result;

    }

    private List<Inflected> explodeFemininesInUU(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, hindiWord.concat("ें")));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE),  hindiWord.concat("ों")));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), hindiWord.concat("ो")));

        return result;

    }


    private List<Inflected> explodeFemininesInII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, "ियाँ"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "ियों"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "ियो"));

        return result;

    }

    private List<Inflected> explodeFemininesInIIIsolated(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedKey().getInflectedHindi();

        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.OBLIQUE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.DIRECT), Lists.newArrayList(Accidence.VOCATIVE), hindiWord));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR), Lists.newArrayList(Accidence.PLURAL), 1, "इयाँ"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.OBLIQUE), 1, "इयों"));
        result.add(clone( origin, Lists.newArrayList(Accidence.SINGULAR, Accidence.DIRECT), Lists.newArrayList(Accidence.PLURAL, Accidence.VOCATIVE), 1, "इयो"));

        return result;

    }




    public List<Inflected> getByInflectedWord(@Nonnull String inflectedWord) {

        return inflectedRepository.findByInflectedKey_InflectedHindi(inflectedWord);
    }


    public List<Inflected> findByMasterDictionaryId(@Nonnull int masterDicionaryId) {
        return inflectedRepository.findByMasterDictionaryId(masterDicionaryId);
    }


    /**
     * From an original list of "proposed', substract those forms whose accidence is contained in the "existing" list.
     * This is used for the "exploding" generators not to overwrite existing, previously inserted forms
     * @param proposed      an initial list with forms we want to insert in InflectedRepositoryDB
     * @param existing      a list with the form that already exists in the InflectedRepositoryDB database
     * @return              a the original "proposed" list, minus those forms whose accidence is already contained in
     *                      "existing"
     */
    public static List<Inflected> subtractByAccidence(List<Inflected> proposed, List<Inflected> existing){
        return proposed.stream().filter(inflected -> !anyWithAccidence(existing, Sets.newHashSet(inflected.getAccidence())))
                .collect(toList());
    }

    @VisibleForTesting
    protected static boolean anyWithAccidence(List<Inflected> inflectedList, Set<Accidence> accidences){
       Preconditions.checkArgument(accidences != null && !accidences.isEmpty(), "non-empty accidences parameter expected");
       return inflectedList.stream().anyMatch( inflected -> accidences.equals(Sets.newHashSet(inflected.getAccidence())));
    }



    public List<Inflected> findByCanonicalWord(@Nonnull String canonicalWord) {

        List<MasterDictionary> masterDictionaries = masterDictionaryService.findByHindiWord(canonicalWord);
        List<Inflected> result = new ArrayList<>();
        masterDictionaries.forEach(md -> {
            List<Inflected> itInf = inflectedRepository.findByMasterDictionaryId (md.getId());
            result.addAll(itInf);
        });

        return result;
    }

    public boolean exists(String hindiWord, int index){
        return inflectedRepository.findByInflectedKey_InflectedHindiAndInflectedKey_InflectedHindiIndex(hindiWord, index).isPresent();
    }

    public boolean save(Inflected master){
        try {
            master.setInflectedUrdu(spellCheckService.passMultipleWordsToUrdu(master.getInflectedKey().inflectedHindi));

            inflectedRepository.save(master);
        }catch (RuntimeException ex){
            LOGGER.error("error saving Master entry {}", ex.getMessage());
            return false;
        }
        return true;
    }


    private boolean validateAccidence(InflectedView inflected){

        Set<Accidence> accidence = Sets.newHashSet(inflected.getAccidence());
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
            }else if (accidence.equals(Sets.newHashSet(Accidence.ABSOLUTIVE))) {
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

    public Inflected fromView(InflectedView inflectedView, MasterDictionary masterDictionary){
        Inflected inflected = new Inflected();
        inflected.setMasterDictionary(masterDictionary);
        Inflected.InflectedKey inflectedKey = new Inflected.InflectedKey();
        inflectedKey.setInflectedHindi(inflectedView.getInflectedHindi());
        inflectedKey.setInflectedHindiIndex(suggestNewIndex(inflectedView.getInflectedHindi()));
        inflected.setInflectedKey(inflectedKey);
        inflected.setAccidence(inflectedView.getAccidence());
        if (inflectedView.isOwnMeaning()) {
            inflected.setOwnMeaning(true);
            inflected.setCanonicalHindi(inflectedView.getCanonicalHindi());
        }
        inflected.setInflectedUrdu(inflectedView.getInflectedUrdu());
        inflected.setPartOfSpeech(inflectedView.getPartOfSpeech());
        return inflected;
    }

    public boolean isValid(InflectedView inflected) {
        if (inflected.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (inflected.getInflectedHindi() == null) {
            LOGGER.info("inflected hindi cannot be null");
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
        return masterDictionaryService.findByHindiWordAndWordIndex(master.getMasterDictionary().getHindiWord(), master.getMasterDictionary().getWordIndex()).isPresent();
    }


    @Inject
    public void setInflectedRepository(InflectedRepositoryDB inflectedRepository) {
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
