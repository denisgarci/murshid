package com.murshid.services;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.common.collect.Sets;
import com.murshid.dynamo.domain.Master;
import com.murshid.dynamo.repo.MasterRepository;
import com.murshid.models.CanonicalKey;
import com.murshid.models.DictionaryKey;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.MasterConverter;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.GonzaloEntry;
import com.murshid.persistence.domain.PrattsEntry;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.domain.WikitionaryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class MasterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterService.class);

    private  static Set<PartOfSpeech> verbDerivates = Sets.newHashSet(PartOfSpeech.VERB, PartOfSpeech.ABSOLUTIVE, PartOfSpeech.VERBAL_NOUN,
      PartOfSpeech.PARTICIPLE, PartOfSpeech.INFINITIVE);

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
            default:
                return derivate == root;
        }
    }

    public List<Master> getAllWords(){

        ScanRequest scanRequest = new ScanRequest().withTableName("master");

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        return scanResult.getItems()
                .stream().map(MasterConverter::fromAvMap)
                .collect(Collectors.toList());
    }

    /**
     * Returns some basic exploding, starting from a canonical form
     * @param origin        the canonical form
     * @return              a list of exploded forms, including the original
     */
    public List<Master> explode(Master origin){
        List<Master> result = new ArrayList<>();
        result.add(origin);
        String hindiWord = origin.getHindiWord();
        if ((origin.getPartOfSpeech() == PartOfSpeech.PARTICIPLE || origin.getPartOfSpeech() == PartOfSpeech.INFINITIVE) && hindiWord.endsWith("ा")){

            Master masculineObliqueSingular = (Master) origin.clone();
            masculineObliqueSingular.setWordIndex(1);
            masculineObliqueSingular.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
            masculineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
            result.add(masculineObliqueSingular);

            Master masculineVocativeSingular = (Master) origin.clone();
            masculineVocativeSingular.setWordIndex(2);
            masculineVocativeSingular.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
            masculineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
            result.add(masculineVocativeSingular);

            Master masculineDirectPlural = (Master) origin.clone();
            masculineDirectPlural.setWordIndex(3);
            masculineDirectPlural.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineDirectPlural.getAccidence().remove(Accidence.SINGULAR);
            masculineDirectPlural.getAccidence().add(Accidence.PLURAL);
            result.add(masculineDirectPlural);

            Master masculineObliquePlural = (Master) origin.clone();
            masculineObliquePlural.setWordIndex(4);
            masculineObliquePlural.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineObliquePlural.getAccidence().remove(Accidence.SINGULAR);
            masculineObliquePlural.getAccidence().add(Accidence.PLURAL);
            masculineObliquePlural.getAccidence().remove(Accidence.DIRECT);
            masculineObliquePlural.getAccidence().add(Accidence.OBLIQUE);
            result.add(masculineObliquePlural);

            Master masculineVocativePlural = (Master) origin.clone();
            masculineVocativePlural.setWordIndex(5);
            masculineVocativePlural.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("े"));
            masculineVocativePlural.getAccidence().remove(Accidence.SINGULAR);
            masculineVocativePlural.getAccidence().add(Accidence.PLURAL);
            masculineVocativePlural.getAccidence().remove(Accidence.DIRECT);
            masculineVocativePlural.getAccidence().add(Accidence.VOCATIVE);
            result.add(masculineVocativePlural);

            Master feminineDirectSingular = (Master) origin.clone();
            feminineDirectSingular.setWordIndex(6);
            feminineDirectSingular.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("ी"));
            feminineDirectSingular.getAccidence().remove(Accidence.MASCULINE);
            feminineDirectSingular.getAccidence().add(Accidence.FEMININE);
            result.add(feminineDirectSingular);

            Master feminineObliqueSingular = (Master) origin.clone();
            feminineObliqueSingular.setWordIndex(7);
            feminineObliqueSingular.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("ीं"));
            feminineObliqueSingular.getAccidence().remove(Accidence.MASCULINE);
            feminineObliqueSingular.getAccidence().add(Accidence.FEMININE);
            feminineObliqueSingular.getAccidence().remove(Accidence.DIRECT);
            feminineObliqueSingular.getAccidence().add(Accidence.OBLIQUE);
            result.add(feminineObliqueSingular);

            Master feminineVocativeSingular = (Master) origin.clone();
            feminineVocativeSingular.setWordIndex(8);
            feminineVocativeSingular.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("ीं"));
            feminineVocativeSingular.getAccidence().remove(Accidence.MASCULINE);
            feminineVocativeSingular.getAccidence().add(Accidence.FEMININE);
            feminineVocativeSingular.getAccidence().remove(Accidence.DIRECT);
            feminineVocativeSingular.getAccidence().add(Accidence.VOCATIVE);
            result.add(feminineVocativeSingular);

            Master feminineDirectPLural = (Master) origin.clone();
            feminineDirectPLural.setWordIndex(9);
            feminineDirectPLural.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("ीं"));
            feminineDirectPLural.getAccidence().remove(Accidence.MASCULINE);
            feminineDirectPLural.getAccidence().add(Accidence.FEMININE);
            feminineDirectPLural.getAccidence().remove(Accidence.SINGULAR);
            feminineDirectPLural.getAccidence().add(Accidence.PLURAL);
            result.add(feminineDirectPLural);

            Master feminineObliquePLural = (Master) origin.clone();
            feminineObliquePLural.setWordIndex(10);
            feminineObliquePLural.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("ीं"));
            feminineObliquePLural.getAccidence().remove(Accidence.MASCULINE);
            feminineObliquePLural.getAccidence().add(Accidence.FEMININE);
            feminineObliquePLural.getAccidence().remove(Accidence.SINGULAR);
            feminineObliquePLural.getAccidence().add(Accidence.PLURAL);
            feminineObliquePLural.getAccidence().remove(Accidence.DIRECT);
            feminineObliquePLural.getAccidence().add(Accidence.OBLIQUE);
            result.add(feminineObliquePLural);

            Master feminineVocativePLural = (Master) origin.clone();
            feminineVocativePLural.setWordIndex(11);
            feminineVocativePLural.setHindiWord(hindiWord.substring(0, hindiWord.length()-1).concat("ीं"));
            feminineVocativePLural.getAccidence().remove(Accidence.MASCULINE);
            feminineVocativePLural.getAccidence().add(Accidence.FEMININE);
            feminineVocativePLural.getAccidence().remove(Accidence.SINGULAR);
            feminineVocativePLural.getAccidence().add(Accidence.PLURAL);
            feminineVocativePLural.getAccidence().remove(Accidence.DIRECT);
            feminineVocativePLural.getAccidence().add(Accidence.VOCATIVE);
            result.add(feminineVocativePLural);
        }
        return result;
    }

    public List<Master> getWords(@Nonnull String word) {

        Map<String, AttributeValue> expressionAttributeValues =  new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":canonicalWord", new AttributeValue().withS(word));

        ScanRequest scanRequest = new ScanRequest()
        .withTableName("master").withFilterExpression( "hindi_word = :canonicalWord")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        return scanResult.getItems()
                .stream().map(MasterConverter::fromAvMap)
                .collect(Collectors.toList());
    }

    public boolean exists(String hindiWord, int index){
        return masterRepository.findOne(hindiWord, index).isPresent();
    }

    public boolean save(Master master){
        try{
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

    /**
     * checks if all canonical keys contained in Master (if any)
     * really exist in the respective entities
     * @param master    the Master record
     * @return          true if all canonical keys exist, false otherwise
     */
    public boolean validateCanonicalKeys(Master master){

        if (master.getCanonicalKeys() == null){
            return true;
        }
        for (int i=0; i< master.getCanonicalKeys().size(); i++){
            CanonicalKey ck = master.getCanonicalKeys().get(i);
            DictionaryKey dk = new DictionaryKey().setHindiWord(ck.canonicalWord).setWordIndex(ck.canonicalIndex);

            switch (ck.dictionarySource){
                case PRATTS:
                    Optional<PrattsEntry> prattsEntry = prattsService.findOne(dk);
                    if (!prattsEntry.isPresent()){
                        LOGGER.info("the PRATTS canonical entry canonicalWord={} canonicalIndex={} indicated in Master does not exist", master.getHindiWord(), master.getWordIndex());
                        return false;
                    }else if (!isDerivatePOS(prattsEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in PRATTS ({})", master.getPartOfSpeech(), prattsEntry.get().getPartOfSpeech());
                        return false;
                    }
                    break;
                case GONZALO:
                    Optional<GonzaloEntry> gonzaloEntry = gonzaloService.findOne(dk);
                    if (!gonzaloEntry.isPresent()){
                        LOGGER.info("the GONZALO canonical entry canonicalWord={} canonicalIndex={} indicated in Master does not exist", master.getHindiWord(), master.getWordIndex());
                        return false;
                    }else if (!isDerivatePOS(gonzaloEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in GONZALO ({})", master.getPartOfSpeech(), gonzaloEntry.get().getPartOfSpeech());
                        return false;
                    }
                    break;
                case REKHTA:
                    Optional<RekhtaEntry> rekhtaEntry = rekhtaService.findOne(dk);
                    if (!rekhtaEntry.isPresent()){
                        LOGGER.info("the REKHTA canonical entry canonicalWord={} canonicalIndex={} indicated in Master does not exist", master.getHindiWord(), master.getWordIndex());
                        return false;
                    }else if (!isDerivatePOS(rekhtaEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in REKHTA ({})", master.getPartOfSpeech(), rekhtaEntry.get().getPartOfSpeech());
                        return false;
                    }
                    break;
                case WIKITIONARY:
                    Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(dk);
                    if (!wikitionaryEntry.isPresent()){
                        LOGGER.info("the WIKITIONARY canonical entry canonicalWord={} canonicalIndex={} indicated in Master does not exist", master.getHindiWord(), master.getWordIndex());
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
    private MasterRepository masterRepository;

    @Inject
    private WikitionaryService wikitionaryService;

    @Inject
    private PrattsService prattsService;

    @Inject
    private GonzaloService gonzaloService;

    @Inject
    private RekhtaService rekhtaService;


}
