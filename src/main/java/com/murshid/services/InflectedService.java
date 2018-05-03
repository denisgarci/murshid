package com.murshid.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class InflectedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InflectedService.class);

    private static Gson gsonMapper = new Gson();

    private  static Set<PartOfSpeech> verbDerivates = Sets.newHashSet(PartOfSpeech.VERB, PartOfSpeech.ABSOLUTIVE, PartOfSpeech.VERBAL_NOUN,
      PartOfSpeech.PARTICIPLE, PartOfSpeech.INFINITIVE);

    private  static Set<PartOfSpeech> pronounDerivates = Sets.newHashSet(PartOfSpeech.PRONOUN, PartOfSpeech.POSSESSIVE_PRONOUN, PartOfSpeech.PERSONAL_PRONOUN,  PartOfSpeech.DEMONSTRATIVE_PRONOUN);

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

    public List<Inflected> getAllWords(){

        ScanRequest scanRequest = new ScanRequest().withTableName("inflected");

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        return scanResult.getItems()
                .stream().map(InflectedConverter::fromAvMap)
                .collect(Collectors.toList());
    }

    /**
     * retrieves all Master entries relevant for a song, but instead of in List form, in a Map<String, Object> form
     * that is suitable to be transformed into a Javascript object.
     * Then, writes them in String form into the Song's DynamoDB record.
     *
     * Also returns the Master entries retrieved
     *
     * @param song      a Song model
     * @return          a Map<String, Object> similar easily transformable into a JS object
     */
    public Map<String, Object> allEntriesForSongJS(Song song){
        List<Inflected> masterList = allEntriesForSong(song);
        Map<String, Object> result = new HashMap<>();
        masterList.forEach(master -> {
            Map<String, Object> value = new HashMap<>();
            value.put("inflected_hindi", master.getInflectedHindi());
            value.put("inflected_urdu", master.getInflectedUrdu());
            value.put("accidence", master.getAccidence());
            value.put("part_of_speech", master.getPartOfSpeech());
            value.put("canonical_hindi", master.getCanonicalHindi());
            value.put("canonical_urdu", master.getCanonicalUrdu());
            List<String> dictionaryKeys = master.getCanonicalKeys().stream()
                    .map( CanonicalKey::toKey)
                    .collect(Collectors.toList());
            value.put("canonical_keys", dictionaryKeys);

            result.put(master.getKey(), value);
        });
        song.setInflectedEntries(gsonMapper.toJson(result).toString());
        songRepository.save(song);

        return result;
    }

    /**
     * Retrieves all Master entries relevant for a Song.
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
            masterRepository.findOne(mk.getInflectedHindi(), mk.getInflectedHindiIndex()))
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
     * Returns some basic exploding, starting from a canonical form
     * @param origin        the canonical form
     * @return              a list of exploded forms, including the original
     */
    public List<Inflected> explode(Inflected origin){
        List<Inflected> result = new ArrayList<>();
        result.add(origin);
        String hindiWord = origin.getInflectedHindi();

        if (origin.getPartOfSpeech() == PartOfSpeech.NOUN && origin.getAccidence().contains(Accidence.MASCULINE) && origin.getAccidence().contains(Accidence.SINGULAR) &&  hindiWord.endsWith("ा")){

            result.addAll(explodeMasculinesInAA(origin));

        } else if (
                (origin.getPartOfSpeech() == PartOfSpeech.PARTICIPLE || origin.getPartOfSpeech() == PartOfSpeech.INFINITIVE )

                && hindiWord.endsWith("ा")){

            Inflected masculineObliqueSingular = (Inflected) origin.clone();
            masculineObliqueSingular.setInflectedHindiIndex(1);
            masculineObliqueSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
            masculineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
            result.add(masculineObliqueSingular);

            Inflected masculineVocativeSingular = (Inflected) origin.clone();
            masculineVocativeSingular.setInflectedHindiIndex(2);
            masculineVocativeSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
            masculineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
            result.add(masculineVocativeSingular);

            Inflected masculineDirectPlural = (Inflected) origin.clone();
            masculineDirectPlural.setInflectedHindiIndex(3);
            masculineDirectPlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineDirectPlural.getAccidence().remove(Accidence.SINGULAR);
            masculineDirectPlural.getAccidence().add(Accidence.PLURAL);
            result.add(masculineDirectPlural);

            Inflected masculineObliquePlural = (Inflected) origin.clone();
            masculineObliquePlural.setInflectedHindiIndex(4);
            masculineObliquePlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineObliquePlural.getAccidence().remove(Accidence.SINGULAR);
            masculineObliquePlural.getAccidence().add(Accidence.PLURAL);
            masculineObliquePlural.getAccidence().remove(Accidence.DIRECT);
            masculineObliquePlural.getAccidence().add(Accidence.OBLIQUE);
            result.add(masculineObliquePlural);

            Inflected masculineVocativePlural = (Inflected) origin.clone();
            masculineVocativePlural.setInflectedHindiIndex(5);
            masculineVocativePlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineVocativePlural.getAccidence().remove(Accidence.SINGULAR);
            masculineVocativePlural.getAccidence().add(Accidence.PLURAL);
            masculineVocativePlural.getAccidence().remove(Accidence.DIRECT);
            masculineVocativePlural.getAccidence().add(Accidence.VOCATIVE);
            result.add(masculineVocativePlural);

            Inflected feminineDirectSingular = (Inflected) origin.clone();
            feminineDirectSingular.setInflectedHindiIndex(6);
            feminineDirectSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ी"));
            feminineDirectSingular.getAccidence().remove(Accidence.MASCULINE);
            feminineDirectSingular.getAccidence().add(Accidence.FEMININE);
            result.add(feminineDirectSingular);

            Inflected feminineObliqueSingular = (Inflected) origin.clone();
            feminineObliqueSingular.setInflectedHindiIndex(7);
            feminineObliqueSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ीं"));
            feminineObliqueSingular.getAccidence().remove(Accidence.MASCULINE);
            feminineObliqueSingular.getAccidence().add(Accidence.FEMININE);
            feminineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
            feminineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
            result.add(feminineObliqueSingular);

            Inflected feminineVocativeSingular = (Inflected) origin.clone();
            feminineVocativeSingular.setInflectedHindiIndex(8);
            feminineVocativeSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ीं"));
            feminineVocativeSingular.getAccidence().remove(Accidence.MASCULINE);
            feminineVocativeSingular.getAccidence().add(Accidence.FEMININE);
            feminineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
            feminineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
            result.add(feminineVocativeSingular);

            Inflected feminineDirectPLural = (Inflected) origin.clone();
            feminineDirectPLural.setInflectedHindiIndex(9);
            feminineDirectPLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ीं"));
            feminineDirectPLural.getAccidence().remove(Accidence.MASCULINE);
            feminineDirectPLural.getAccidence().add(Accidence.FEMININE);
            feminineDirectPLural.getAccidence().remove(Accidence.SINGULAR);
            feminineDirectPLural.getAccidence().add(Accidence.PLURAL);
            result.add(feminineDirectPLural);

            Inflected feminineObliquePLural = (Inflected) origin.clone();
            feminineObliquePLural.setInflectedHindiIndex(10);
            feminineObliquePLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ीं"));
            feminineObliquePLural.getAccidence().remove(Accidence.MASCULINE);
            feminineObliquePLural.getAccidence().add(Accidence.FEMININE);
            feminineObliquePLural.getAccidence().remove(Accidence.SINGULAR);
            feminineObliquePLural.getAccidence().add(Accidence.PLURAL);
            feminineObliquePLural.getAccidence().remove(Accidence.DIRECT);
            feminineObliquePLural.getAccidence().add(Accidence.OBLIQUE);
            result.add(feminineObliquePLural);

            Inflected feminineVocativePLural = (Inflected) origin.clone();
            feminineVocativePLural.setInflectedHindiIndex(11);
            feminineVocativePLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ीं"));
            feminineVocativePLural.getAccidence().remove(Accidence.MASCULINE);
            feminineVocativePLural.getAccidence().add(Accidence.FEMININE);
            feminineVocativePLural.getAccidence().remove(Accidence.SINGULAR);
            feminineVocativePLural.getAccidence().add(Accidence.PLURAL);
            feminineVocativePLural.getAccidence().remove(Accidence.DIRECT);
            feminineVocativePLural.getAccidence().add(Accidence.VOCATIVE);
            result.add(feminineVocativePLural);
        }else if (
                (origin.getPartOfSpeech() == PartOfSpeech.ADJECTIVE || origin.getPartOfSpeech() == PartOfSpeech.POSSESSIVE_PRONOUN || origin.getPartOfSpeech() == PartOfSpeech.ADVERB)

                && hindiWord.endsWith("ा")){

            Inflected masculineObliqueSingular = (Inflected) origin.clone();
            masculineObliqueSingular.setInflectedHindiIndex(1);
            masculineObliqueSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
            masculineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
            result.add(masculineObliqueSingular);

            Inflected masculineVocativeSingular = (Inflected) origin.clone();
            masculineVocativeSingular.setInflectedHindiIndex(2);
            masculineVocativeSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
            masculineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
            result.add(masculineVocativeSingular);

            Inflected masculineDirectPlural = (Inflected) origin.clone();
            masculineDirectPlural.setInflectedHindiIndex(3);
            masculineDirectPlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineDirectPlural.getAccidence().remove(Accidence.SINGULAR);
            masculineDirectPlural.getAccidence().add(Accidence.PLURAL);
            result.add(masculineDirectPlural);

            Inflected masculineObliquePlural = (Inflected) origin.clone();
            masculineObliquePlural.setInflectedHindiIndex(4);
            masculineObliquePlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineObliquePlural.getAccidence().remove(Accidence.SINGULAR);
            masculineObliquePlural.getAccidence().add(Accidence.PLURAL);
            masculineObliquePlural.getAccidence().remove(Accidence.DIRECT);
            masculineObliquePlural.getAccidence().add(Accidence.OBLIQUE);
            result.add(masculineObliquePlural);

            Inflected masculineVocativePlural = (Inflected) origin.clone();
            masculineVocativePlural.setInflectedHindiIndex(5);
            masculineVocativePlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineVocativePlural.getAccidence().remove(Accidence.SINGULAR);
            masculineVocativePlural.getAccidence().add(Accidence.PLURAL);
            masculineVocativePlural.getAccidence().remove(Accidence.DIRECT);
            masculineVocativePlural.getAccidence().add(Accidence.VOCATIVE);
            result.add(masculineVocativePlural);

            Inflected feminineDirectSingular = (Inflected) origin.clone();
            feminineDirectSingular.setInflectedHindiIndex(6);
            feminineDirectSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ी"));
            feminineDirectSingular.getAccidence().remove(Accidence.MASCULINE);
            feminineDirectSingular.getAccidence().add(Accidence.FEMININE);
            result.add(feminineDirectSingular);

            Inflected feminineObliqueSingular = (Inflected) origin.clone();
            feminineObliqueSingular.setInflectedHindiIndex(7);
            feminineObliqueSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ी"));
            feminineObliqueSingular.getAccidence().remove(Accidence.MASCULINE);
            feminineObliqueSingular.getAccidence().add(Accidence.FEMININE);
            feminineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
            feminineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
            result.add(feminineObliqueSingular);

            Inflected feminineVocativeSingular = (Inflected) origin.clone();
            feminineVocativeSingular.setInflectedHindiIndex(8);
            feminineVocativeSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ी"));
            feminineVocativeSingular.getAccidence().remove(Accidence.MASCULINE);
            feminineVocativeSingular.getAccidence().add(Accidence.FEMININE);
            feminineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
            feminineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
            result.add(feminineVocativeSingular);

            Inflected feminineDirectPLural = (Inflected) origin.clone();
            feminineDirectPLural.setInflectedHindiIndex(9);
            feminineDirectPLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ी"));
            feminineDirectPLural.getAccidence().remove(Accidence.MASCULINE);
            feminineDirectPLural.getAccidence().add(Accidence.FEMININE);
            feminineDirectPLural.getAccidence().remove(Accidence.SINGULAR);
            feminineDirectPLural.getAccidence().add(Accidence.PLURAL);
            result.add(feminineDirectPLural);

            Inflected feminineObliquePLural = (Inflected) origin.clone();
            feminineObliquePLural.setInflectedHindiIndex(10);
            feminineObliquePLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ी"));
            feminineObliquePLural.getAccidence().remove(Accidence.MASCULINE);
            feminineObliquePLural.getAccidence().add(Accidence.FEMININE);
            feminineObliquePLural.getAccidence().remove(Accidence.SINGULAR);
            feminineObliquePLural.getAccidence().add(Accidence.PLURAL);
            feminineObliquePLural.getAccidence().remove(Accidence.DIRECT);
            feminineObliquePLural.getAccidence().add(Accidence.OBLIQUE);
            result.add(feminineObliquePLural);

            Inflected feminineVocativePLural = (Inflected) origin.clone();
            feminineVocativePLural.setInflectedHindiIndex(11);
            feminineVocativePLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ी"));
            feminineVocativePLural.getAccidence().remove(Accidence.MASCULINE);
            feminineVocativePLural.getAccidence().add(Accidence.FEMININE);
            feminineVocativePLural.getAccidence().remove(Accidence.SINGULAR);
            feminineVocativePLural.getAccidence().add(Accidence.PLURAL);
            feminineVocativePLural.getAccidence().remove(Accidence.DIRECT);
            feminineVocativePLural.getAccidence().add(Accidence.VOCATIVE);
            result.add(feminineVocativePLural);
        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && origin.getAccidence().contains(Accidence.MASCULINE) && !hindiWord.endsWith("ा") && !hindiWord.endsWith("ू") && !hindiWord.endsWith("ी") ){

                    result.addAll(explodeMasculinesNotInAAorUUorII(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && origin.getAccidence().contains(Accidence.MASCULINE) && hindiWord.endsWith("ू") ){

            result.addAll(explodeMasculinesInUU(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && origin.getAccidence().contains(Accidence.MASCULINE) && hindiWord.endsWith("ी") ){

            result.addAll(explodeMasculinesInII(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && origin.getAccidence().contains(Accidence.FEMININE) && hindiWord.endsWith("ई") ){

            result.addAll(explodeFemininesInIIIsolated(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && origin.getAccidence().contains(Accidence.FEMININE) && (!hindiWord.endsWith("ी") ) ){

            result.addAll(explodeFemininesNotInII(origin));

        } else if (origin.getPartOfSpeech() == PartOfSpeech.NOUN  && origin.getAccidence().contains(Accidence.FEMININE) && (hindiWord.endsWith("ी")) ){

            result.addAll(explodeFemininesInII(origin));

        }

        return result;
    }

    private List<Inflected> explodeMasculinesNotInAAorUUorII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        Inflected masculineObliqueSingular = (Inflected) origin.clone();
        masculineObliqueSingular.setInflectedHindiIndex(1);
        masculineObliqueSingular.setInflectedHindi(hindiWord);
        masculineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
        masculineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
        result.add(masculineObliqueSingular);

        Inflected masculineVocativeSingular = (Inflected) origin.clone();
        masculineVocativeSingular.setInflectedHindiIndex(2);
        masculineVocativeSingular.setInflectedHindi(hindiWord);
        masculineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
        masculineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
        result.add(masculineVocativeSingular);

        Inflected masculineDirectPlural = (Inflected) origin.clone();
        masculineDirectPlural.setInflectedHindiIndex(3);
        masculineDirectPlural.setInflectedHindi(hindiWord);
        masculineDirectPlural.getAccidence().remove(Accidence.SINGULAR);
        masculineDirectPlural.getAccidence().add(Accidence.PLURAL);
        result.add(masculineDirectPlural);

        Inflected masculineObliquePlural = (Inflected) origin.clone();
        masculineObliquePlural.setInflectedHindiIndex(4);
        masculineObliquePlural.setInflectedHindi(hindiWord.concat("ों"));
        masculineObliquePlural.getAccidence().remove(Accidence.SINGULAR);
        masculineObliquePlural.getAccidence().add(Accidence.PLURAL);
        masculineObliquePlural.getAccidence().remove(Accidence.DIRECT);
        masculineObliquePlural.getAccidence().add(Accidence.OBLIQUE);
        result.add(masculineObliquePlural);

        Inflected masculineVocativePlural = (Inflected) origin.clone();
        masculineVocativePlural.setInflectedHindiIndex(5);
        masculineVocativePlural.setInflectedHindi(hindiWord.concat("ो"));
        masculineVocativePlural.getAccidence().remove(Accidence.SINGULAR);
        masculineVocativePlural.getAccidence().add(Accidence.PLURAL);
        masculineVocativePlural.getAccidence().remove(Accidence.DIRECT);
        masculineVocativePlural.getAccidence().add(Accidence.VOCATIVE);
        result.add(masculineVocativePlural);

        return result;

    }

    private List<Inflected> explodeMasculinesInAA(Inflected origin){
        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        Inflected masculineObliqueSingular = (Inflected) origin.clone();
        masculineObliqueSingular.setInflectedHindiIndex(1);
        masculineObliqueSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
        masculineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
        masculineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
        result.add(masculineObliqueSingular);

        Inflected masculineVocativeSingular = (Inflected) origin.clone();
        masculineVocativeSingular.setInflectedHindiIndex(2);
        masculineVocativeSingular.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
        masculineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
        masculineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
        result.add(masculineVocativeSingular);

        Inflected masculineDirectPlural = (Inflected) origin.clone();
        masculineDirectPlural.setInflectedHindiIndex(3);
        masculineDirectPlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
        masculineDirectPlural.getAccidence().remove(Accidence.SINGULAR);
        masculineDirectPlural.getAccidence().add(Accidence.PLURAL);
        result.add(masculineDirectPlural);

        Inflected masculineObliquePlural = (Inflected) origin.clone();
        masculineObliquePlural.setInflectedHindiIndex(4);
        masculineObliquePlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ों"));
        masculineObliquePlural.getAccidence().remove(Accidence.SINGULAR);
        masculineObliquePlural.getAccidence().add(Accidence.PLURAL);
        masculineObliquePlural.getAccidence().remove(Accidence.DIRECT);
        masculineObliquePlural.getAccidence().add(Accidence.OBLIQUE);
        result.add(masculineObliquePlural);

        Inflected masculineVocativePlural = (Inflected) origin.clone();
        masculineVocativePlural.setInflectedHindiIndex(5);
        masculineVocativePlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ो"));
        masculineVocativePlural.getAccidence().remove(Accidence.SINGULAR);
        masculineVocativePlural.getAccidence().add(Accidence.PLURAL);
        masculineVocativePlural.getAccidence().remove(Accidence.DIRECT);
        masculineVocativePlural.getAccidence().add(Accidence.VOCATIVE);
        result.add(masculineVocativePlural);

        return result;

    }

    private List<Inflected> explodeMasculinesInII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        Inflected masculineObliqueSingular = (Inflected) origin.clone();
        masculineObliqueSingular.setInflectedHindiIndex(1);
        masculineObliqueSingular.setInflectedHindi(hindiWord);
        masculineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
        masculineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
        result.add(masculineObliqueSingular);

        Inflected masculineVocativeSingular = (Inflected) origin.clone();
        masculineVocativeSingular.setInflectedHindiIndex(2);
        masculineVocativeSingular.setInflectedHindi(hindiWord);
        masculineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
        masculineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
        result.add(masculineVocativeSingular);

        Inflected masculineDirectPlural = (Inflected) origin.clone();
        masculineDirectPlural.setInflectedHindiIndex(3);
        masculineDirectPlural.setInflectedHindi(hindiWord);
        masculineDirectPlural.getAccidence().remove(Accidence.SINGULAR);
        masculineDirectPlural.getAccidence().add(Accidence.PLURAL);
        result.add(masculineDirectPlural);

        Inflected masculineObliquePlural = (Inflected) origin.clone();
        masculineObliquePlural.setInflectedHindiIndex(4);
        masculineObliquePlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ियों"));
        masculineObliquePlural.getAccidence().remove(Accidence.SINGULAR);
        masculineObliquePlural.getAccidence().add(Accidence.PLURAL);
        masculineObliquePlural.getAccidence().remove(Accidence.DIRECT);
        masculineObliquePlural.getAccidence().add(Accidence.OBLIQUE);
        result.add(masculineObliquePlural);

        Inflected masculineVocativePlural = (Inflected) origin.clone();
        masculineVocativePlural.setInflectedHindiIndex(5);
        masculineVocativePlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ियो"));
        masculineVocativePlural.getAccidence().remove(Accidence.SINGULAR);
        masculineVocativePlural.getAccidence().add(Accidence.PLURAL);
        masculineVocativePlural.getAccidence().remove(Accidence.DIRECT);
        masculineVocativePlural.getAccidence().add(Accidence.VOCATIVE);
        result.add(masculineVocativePlural);

        return result;

    }

    private List<Inflected> explodeMasculinesInUU(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        Inflected masculineObliqueSingular = (Inflected) origin.clone();
        masculineObliqueSingular.setInflectedHindiIndex(1);
        masculineObliqueSingular.setInflectedHindi(hindiWord);
        masculineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
        masculineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
        result.add(masculineObliqueSingular);

        Inflected masculineVocativeSingular = (Inflected) origin.clone();
        masculineVocativeSingular.setInflectedHindiIndex(2);
        masculineVocativeSingular.setInflectedHindi(hindiWord);
        masculineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
        masculineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
        result.add(masculineVocativeSingular);

        Inflected masculineDirectPlural = (Inflected) origin.clone();
        masculineDirectPlural.setInflectedHindiIndex(3);
        masculineDirectPlural.setInflectedHindi(hindiWord);
        masculineDirectPlural.getAccidence().remove(Accidence.SINGULAR);
        masculineDirectPlural.getAccidence().add(Accidence.PLURAL);
        result.add(masculineDirectPlural);

        Inflected masculineObliquePlural = (Inflected) origin.clone();
        masculineObliquePlural.setInflectedHindiIndex(4);
        masculineObliquePlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ुओं"));
        masculineObliquePlural.getAccidence().remove(Accidence.SINGULAR);
        masculineObliquePlural.getAccidence().add(Accidence.PLURAL);
        masculineObliquePlural.getAccidence().remove(Accidence.DIRECT);
        masculineObliquePlural.getAccidence().add(Accidence.OBLIQUE);
        result.add(masculineObliquePlural);

        Inflected masculineVocativePlural = (Inflected) origin.clone();
        masculineVocativePlural.setInflectedHindiIndex(5);
        masculineVocativePlural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ुओ"));
        masculineVocativePlural.getAccidence().remove(Accidence.SINGULAR);
        masculineVocativePlural.getAccidence().add(Accidence.PLURAL);
        masculineVocativePlural.getAccidence().remove(Accidence.DIRECT);
        masculineVocativePlural.getAccidence().add(Accidence.VOCATIVE);
        result.add(masculineVocativePlural);

        return result;

    }

    private List<Inflected> explodeFemininesNotInII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        Inflected feminineObliqueSingular = (Inflected) origin.clone();
        feminineObliqueSingular.setInflectedHindiIndex(7);
        feminineObliqueSingular.setInflectedHindi(hindiWord);
        feminineObliqueSingular.getAccidence().remove(Accidence.MASCULINE);
        feminineObliqueSingular.getAccidence().add(Accidence.FEMININE);
        feminineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
        feminineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
        result.add(feminineObliqueSingular);

        Inflected feminineVocativeSingular = (Inflected) origin.clone();
        feminineVocativeSingular.setInflectedHindiIndex(8);
        feminineVocativeSingular.setInflectedHindi(hindiWord);
        feminineVocativeSingular.getAccidence().remove(Accidence.MASCULINE);
        feminineVocativeSingular.getAccidence().add(Accidence.FEMININE);
        feminineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
        feminineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
        result.add(feminineVocativeSingular);

        Inflected feminineDirectPLural = (Inflected) origin.clone();
        feminineDirectPLural.setInflectedHindiIndex(9);
        feminineDirectPLural.setInflectedHindi(hindiWord.concat("ें"));
        feminineDirectPLural.getAccidence().remove(Accidence.MASCULINE);
        feminineDirectPLural.getAccidence().add(Accidence.FEMININE);
        feminineDirectPLural.getAccidence().remove(Accidence.SINGULAR);
        feminineDirectPLural.getAccidence().add(Accidence.PLURAL);
        result.add(feminineDirectPLural);

        Inflected feminineObliquePLural = (Inflected) origin.clone();
        feminineObliquePLural.setInflectedHindiIndex(10);
        feminineObliquePLural.setInflectedHindi(hindiWord.concat("ों"));
        feminineObliquePLural.getAccidence().remove(Accidence.MASCULINE);
        feminineObliquePLural.getAccidence().add(Accidence.FEMININE);
        feminineObliquePLural.getAccidence().remove(Accidence.SINGULAR);
        feminineObliquePLural.getAccidence().add(Accidence.PLURAL);
        feminineObliquePLural.getAccidence().remove(Accidence.DIRECT);
        feminineObliquePLural.getAccidence().add(Accidence.OBLIQUE);
        result.add(feminineObliquePLural);

        Inflected feminineVocativePLural = (Inflected) origin.clone();
        feminineVocativePLural.setInflectedHindiIndex(11);
        feminineVocativePLural.setInflectedHindi(hindiWord.concat("ो"));
        feminineVocativePLural.getAccidence().remove(Accidence.MASCULINE);
        feminineVocativePLural.getAccidence().add(Accidence.FEMININE);
        feminineVocativePLural.getAccidence().remove(Accidence.SINGULAR);
        feminineVocativePLural.getAccidence().add(Accidence.PLURAL);
        feminineVocativePLural.getAccidence().remove(Accidence.DIRECT);
        feminineVocativePLural.getAccidence().add(Accidence.VOCATIVE);
        result.add(feminineVocativePLural);

        return result;

    }

    private List<Inflected> explodeFemininesInII(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        Inflected feminineObliqueSingular = (Inflected) origin.clone();
        feminineObliqueSingular.setInflectedHindiIndex(7);
        feminineObliqueSingular.setInflectedHindi(hindiWord);
        feminineObliqueSingular.getAccidence().remove(Accidence.MASCULINE);
        feminineObliqueSingular.getAccidence().add(Accidence.FEMININE);
        feminineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
        feminineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
        result.add(feminineObliqueSingular);

        Inflected feminineVocativeSingular = (Inflected) origin.clone();
        feminineVocativeSingular.setInflectedHindiIndex(8);
        feminineVocativeSingular.setInflectedHindi(hindiWord);
        feminineVocativeSingular.getAccidence().remove(Accidence.MASCULINE);
        feminineVocativeSingular.getAccidence().add(Accidence.FEMININE);
        feminineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
        feminineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
        result.add(feminineVocativeSingular);

        Inflected feminineDirectPLural = (Inflected) origin.clone();
        feminineDirectPLural.setInflectedHindiIndex(9);
        feminineDirectPLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ियाँ"));
        feminineDirectPLural.getAccidence().remove(Accidence.MASCULINE);
        feminineDirectPLural.getAccidence().add(Accidence.FEMININE);
        feminineDirectPLural.getAccidence().remove(Accidence.SINGULAR);
        feminineDirectPLural.getAccidence().add(Accidence.PLURAL);
        result.add(feminineDirectPLural);

        Inflected feminineObliquePLural = (Inflected) origin.clone();
        feminineObliquePLural.setInflectedHindiIndex(10);
        feminineObliquePLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ियों"));
        feminineObliquePLural.getAccidence().remove(Accidence.MASCULINE);
        feminineObliquePLural.getAccidence().add(Accidence.FEMININE);
        feminineObliquePLural.getAccidence().remove(Accidence.SINGULAR);
        feminineObliquePLural.getAccidence().add(Accidence.PLURAL);
        feminineObliquePLural.getAccidence().remove(Accidence.DIRECT);
        feminineObliquePLural.getAccidence().add(Accidence.OBLIQUE);
        result.add(feminineObliquePLural);

        Inflected feminineVocativePLural = (Inflected) origin.clone();
        feminineVocativePLural.setInflectedHindiIndex(11);
        feminineVocativePLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("ियो"));
        feminineVocativePLural.getAccidence().remove(Accidence.MASCULINE);
        feminineVocativePLural.getAccidence().add(Accidence.FEMININE);
        feminineVocativePLural.getAccidence().remove(Accidence.SINGULAR);
        feminineVocativePLural.getAccidence().add(Accidence.PLURAL);
        feminineVocativePLural.getAccidence().remove(Accidence.DIRECT);
        feminineVocativePLural.getAccidence().add(Accidence.VOCATIVE);
        result.add(feminineVocativePLural);

        return result;

    }

    private List<Inflected> explodeFemininesInIIIsolated(Inflected origin){

        List<Inflected> result = new ArrayList<>();
        String hindiWord = origin.getInflectedHindi();

        Inflected feminineObliqueSingular = (Inflected) origin.clone();
        feminineObliqueSingular.setInflectedHindiIndex(7);
        feminineObliqueSingular.setInflectedHindi(hindiWord);
        feminineObliqueSingular.getAccidence().remove(Accidence.MASCULINE);
        feminineObliqueSingular.getAccidence().add(Accidence.FEMININE);
        feminineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
        feminineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
        result.add(feminineObliqueSingular);

        Inflected feminineVocativeSingular = (Inflected) origin.clone();
        feminineVocativeSingular.setInflectedHindiIndex(8);
        feminineVocativeSingular.setInflectedHindi(hindiWord);
        feminineVocativeSingular.getAccidence().remove(Accidence.MASCULINE);
        feminineVocativeSingular.getAccidence().add(Accidence.FEMININE);
        feminineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
        feminineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
        result.add(feminineVocativeSingular);

        Inflected feminineDirectPLural = (Inflected) origin.clone();
        feminineDirectPLural.setInflectedHindiIndex(9);
        feminineDirectPLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("इयाँ"));
        feminineDirectPLural.getAccidence().remove(Accidence.MASCULINE);
        feminineDirectPLural.getAccidence().add(Accidence.FEMININE);
        feminineDirectPLural.getAccidence().remove(Accidence.SINGULAR);
        feminineDirectPLural.getAccidence().add(Accidence.PLURAL);
        result.add(feminineDirectPLural);

        Inflected feminineObliquePLural = (Inflected) origin.clone();
        feminineObliquePLural.setInflectedHindiIndex(10);
        feminineObliquePLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("इयों"));
        feminineObliquePLural.getAccidence().remove(Accidence.MASCULINE);
        feminineObliquePLural.getAccidence().add(Accidence.FEMININE);
        feminineObliquePLural.getAccidence().remove(Accidence.SINGULAR);
        feminineObliquePLural.getAccidence().add(Accidence.PLURAL);
        feminineObliquePLural.getAccidence().remove(Accidence.DIRECT);
        feminineObliquePLural.getAccidence().add(Accidence.OBLIQUE);
        result.add(feminineObliquePLural);

        Inflected feminineVocativePLural = (Inflected) origin.clone();
        feminineVocativePLural.setInflectedHindiIndex(11);
        feminineVocativePLural.setInflectedHindi(hindiWord.substring(0, hindiWord.length()-1).concat("इयो"));
        feminineVocativePLural.getAccidence().remove(Accidence.MASCULINE);
        feminineVocativePLural.getAccidence().add(Accidence.FEMININE);
        feminineVocativePLural.getAccidence().remove(Accidence.SINGULAR);
        feminineVocativePLural.getAccidence().add(Accidence.PLURAL);
        feminineVocativePLural.getAccidence().remove(Accidence.DIRECT);
        feminineVocativePLural.getAccidence().add(Accidence.VOCATIVE);
        result.add(feminineVocativePLural);

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

    public List<Inflected> findByCanonicalWord(@Nonnull String canonicalWord) {

        Iterator<Item> items = masterRepository.findByCanonicalWord(canonicalWord);

        List<Inflected> result = new ArrayList<>();
        items.forEachRemaining(it ->{
            Optional<Inflected> master = masterRepository.findOne(it.getString("inflected_hindi"), it.getInt("inflected_hindi_index"));
            if(master.isPresent()){
                result.add(master.get());
            }
        });
        return result;
    }

    public boolean exists(String hindiWord, int index){
        return masterRepository.findOne(hindiWord, index).isPresent();
    }

    public boolean save(Inflected master){
        try {
            master.setInflectedUrdu(spellCheckService.getUrduSpelling(master.getInflectedHindi()));
            master.setCanonicalUrdu(spellCheckService.getUrduSpelling(master.getCanonicalHindi()));

            masterRepository.save(master);
        }catch (RuntimeException ex){
            LOGGER.error("error saving Master entry {}", ex.getMessage());
            return false;
        }
        return true;
    }


    public boolean validateAccidence(PartOfSpeech partOfSpeech, Set<Accidence> accidence){
        if (accidence == null){
            accidence = Collections.EMPTY_SET;
        }
        boolean hasGender = accidence.contains(Accidence.MASCULINE) || accidence.contains(Accidence.FEMININE);
        boolean hasNumber = accidence.contains(Accidence.SINGULAR) || accidence.contains(Accidence.PLURAL);
        boolean hasCase = accidence.contains(Accidence.DIRECT) || accidence.contains(Accidence.OBLIQUE) || accidence.contains(Accidence.VOCATIVE);
        boolean hasAspect = accidence.contains(Accidence.PERFECTIVE) || accidence.contains(Accidence.IMPERFECTIVE);
        boolean hasPerson = accidence.contains(Accidence._1ST) || accidence.contains(Accidence._2ND) || accidence.contains(Accidence._3RD);
        //"subjunctive" is a tense for the moment
        boolean hasTense = accidence.contains(Accidence.PRESENT) || accidence.contains(Accidence.FUTURE) || accidence.contains(Accidence.PAST) || accidence.contains(Accidence.PLUSQUAMPERFECT) || accidence.contains(Accidence.SUBJUNCTIVE);

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
                LOGGER.error("accidence validation failure: accidence validation error:part of speech PARTICIPLE has to have exactly gender, number, aspect, and case");
                return false;
            }
        }

        if (partOfSpeech == PartOfSpeech.ABSOLUTIVE){
            if (accidence.isEmpty()){
                return true;
            }else{
                LOGGER.info("accidence validation failure: absolutive cannot have accidences");
                return false;
            }
        }

        if (partOfSpeech == PartOfSpeech.VERB){
            if (!hasPerson || !hasNumber || !hasTense){
                LOGGER.info("accidence validation failure: part of speech VERB has to have at least person, number and tense");
                return false;
            }
            return true;
        }

        if (partOfSpeech == PartOfSpeech.INFINITIVE){
            if ( hasGender &&  hasNumber && hasCase && accidence.size() == 3) {
                return true;
            }else{
                LOGGER.info("accidence validation failure: part of speech INFINITIVE has to have number, gender and case");
                return false;
            }
        }

        if (partOfSpeech == PartOfSpeech.VERBAL_NOUN){
            if ( hasGender &&  hasNumber && hasCase && accidence.size() == 3) {
                return true;
            }else{
                LOGGER.info("accidence validation failure: part of speech VERBAL_NPUN has to have number, gender and case");
                return false;
            }
        }

        return true;

    }

    public boolean isValid(Inflected master) {
        if (master.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (master.getInflectedHindi() == null) {
            LOGGER.info("canonicalWord cannot be null");
            return false;
        }

        if (!spellCheckService.exists(master.getInflectedHindi())){
            LOGGER.info("the inflected hindi word {} does not exists in hindi_words ", master.getInflectedHindi());
            return false;
        }

        if (master.getPartOfSpeech() == null) {
            LOGGER.info("part of speech cannot be null");
            return false;
        }

        if (!validateCanonicalKeys(master)){
            LOGGER.info("some of the canonical keys are not present");
            return false;
        }

        if (!validateAccidence(master.getPartOfSpeech(), master.getAccidence())){
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
                        LOGGER.info("the PRATTS canonical entry canonicalWord={} canonicalIndex={} indicated in Master does not exist", master.getInflectedHindi(), master.getInflectedHindiIndex());
                        return false;
                    }else if (!isDerivatePOS(plattsEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in PRATTS ({})", master.getPartOfSpeech(), plattsEntry.get().getPartOfSpeech());
                        return false;
                    }
                    break;
                case MURSHID:
                    Optional<MurshidEntry> gonzaloEntry = murshidService.findOne(dk);
                    if (!gonzaloEntry.isPresent()){
                        LOGGER.info("the GONZALO canonical entry canonicalWord={} canonicalIndex={} indicated in Master does not exist", master.getInflectedHindi(), master.getInflectedHindiIndex());
                        return false;
                    }else if (!isDerivatePOS(gonzaloEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in GONZALO ({})", master.getPartOfSpeech(), gonzaloEntry.get().getPartOfSpeech());
                        return false;
                    }
                    break;
                case REKHTA:
                    Optional<RekhtaEntry> rekhtaEntry = rekhtaService.findOne(dk);
                    if (!rekhtaEntry.isPresent()){
                        LOGGER.info("the REKHTA canonical entry canonicalWord={} canonicalIndex={} indicated in Master does not exist", master.getInflectedHindi(), master.getInflectedHindiIndex());
                        return false;
                    }else if (!isDerivatePOS(rekhtaEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in REKHTA ({})", master.getPartOfSpeech(), rekhtaEntry.get().getPartOfSpeech());
                        return false;
                    }
                    break;
                case WIKITIONARY:
                    Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(dk);
                    if (!wikitionaryEntry.isPresent()){
                        LOGGER.info("the WIKITIONARY canonical entry canonicalWord={} canonicalIndex={} indicated in Master does not exist", master.getInflectedHindi(), master.getInflectedHindiIndex());
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
    private InflectedRepository masterRepository;

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
