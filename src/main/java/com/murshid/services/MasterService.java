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

    private  static Set<PartOfSpeech> verbDerivates = Sets.newHashSet(PartOfSpeech.VERB, PartOfSpeech.ABSOLUTIVE, PartOfSpeech.VERBAL_NOUN, PartOfSpeech.PARTICIPLE);

    /**
     * true if the POS indicated in Master can refer to the POS indicated in the respective disctionarr tables.
     * For example, a participle can refer to a dictionary verb.
     * @param root          the POS in the dictionary table
     * @param derivate      the POS in the Master's entry
     * @return              true if the link is valid, false if not
     */
    public boolean isDerivatePOS(PartOfSpeech root, PartOfSpeech derivate){
        switch (root){
            case VERB:
                return verbDerivates.contains(derivate);
            default:
                return derivate == root;
        }
    }

    public List<Master> getWords(@Nonnull String word) {

        Map<String, AttributeValue> expressionAttributeValues =  new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":hindiWord", new AttributeValue().withS(word));

        ScanRequest scanRequest = new ScanRequest()
        .withTableName("master").withFilterExpression( "hindi_word = :hindiWord")
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
        boolean hasGender = accidence.contains(Accidence.MASCULINE) || accidence.contains(Accidence.FEMININE);
        boolean hasNumber = accidence.contains(Accidence.SINGULAR) || accidence.contains(Accidence.PLURAL);
        boolean hasCase = accidence.contains(Accidence.DIRECT) || accidence.contains(Accidence.OBLIQUE) || accidence.contains(Accidence.VOCATIVE);
        boolean hasAspect = accidence.contains(Accidence.PERFECTIVE) || accidence.contains(Accidence.IMPERFECTIVE);
        boolean hasPerson = accidence.contains(Accidence._1ST) || accidence.contains(Accidence._2ND) || accidence.contains(Accidence._3RD);
        //"subjunctive" is a tense for the moment
        boolean hasTense = accidence.contains(Accidence.PRESENT) || accidence.contains(Accidence.FUTURE) || accidence.contains(Accidence.PAST) || accidence.contains(Accidence.PLUSQUAMPERFECT) || accidence.contains(Accidence.SUBJUNCTIVE);
        boolean hasFamiliarityDegree = accidence.contains(Accidence.FORMAL) || accidence.contains(Accidence.FAMILIAR) || accidence.contains(Accidence.INTIMATE);

        if (partOfSpeech == PartOfSpeech.NOUN || partOfSpeech == PartOfSpeech.ADJECTIVE){
            if (hasGender && hasNumber && hasCase && accidence.size() == 3) {
                return true;
            }else{
                LOGGER.info("accidence validation failure: parts of speech NOUN and ADJECTIVE have to have exactly gender, number and case");
                return false;
            }
        }

        if (partOfSpeech == PartOfSpeech.PARTICIPLE){
            if (hasGender && hasNumber && hasAspect && accidence.size() == 3){
                return true;
            }else{
                LOGGER.error("accidence validation failure: accidence validation error:part of speech PARTICIPLE has to have exactly gender, number and aspect");
                return false;
            }
        }

        if (partOfSpeech == PartOfSpeech.VERB){
            if (!hasPerson || !hasNumber || !hasTense){
                LOGGER.info("accidence validation failure: part of speech VERB has to have at least person, number and tense");
                return false;
            }
            if (accidence.contains(Accidence._2ND) && !hasFamiliarityDegree){
                LOGGER.info("accidence validation failure: second person verbs have to contain familiarity degree");
                return false;
            }
            return true;
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
            DictionaryKey dk = new DictionaryKey().setWord(ck.hindiWord).setWordIndex(ck.wordIndex);

            switch (ck.dictionarySource){
                case PRATTS:
                    Optional<PrattsEntry> prattsEntry = prattsService.findOne(dk);
                    if (!prattsEntry.isPresent()){
                        LOGGER.info("the PRATTS canonical entry hindiWord={} wordIndex={} indicated in Master does not exist", master.getHindiWord(), master.getWordIndex());
                        return false;
                    }else if (!isDerivatePOS(prattsEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in PRATTS ({})", master.getPartOfSpeech(), prattsEntry.get().getPartOfSpeech());
                        return false;
                    }

                case GONZALO:
                    Optional<GonzaloEntry> gonzaloEntry = gonzaloService.findOne(dk);
                    if (!gonzaloEntry.isPresent()){
                        LOGGER.info("the GONZALO canonical entry hindiWord={} wordIndex={} indicated in Master does not exist", master.getHindiWord(), master.getWordIndex());
                        return false;
                    }else if (!isDerivatePOS(gonzaloEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in GONZALO ({})", master.getPartOfSpeech(), gonzaloEntry.get().getPartOfSpeech());
                        return false;
                    }
                case REKHTA:
                    Optional<RekhtaEntry> rekhtaEntry = rekhtaService.findOne(dk);
                    if (!rekhtaEntry.isPresent()){
                        LOGGER.info("the REKHTA canonical entry hindiWord={} wordIndex={} indicated in Master does not exist", master.getHindiWord(), master.getWordIndex());
                        return false;
                    }else if (!isDerivatePOS(rekhtaEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in REKHTA ({})", master.getPartOfSpeech(), rekhtaEntry.get().getPartOfSpeech());
                        return false;
                    }
                case WIKITIONARY:
                    Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(dk);
                    if (!wikitionaryEntry.isPresent()){
                        LOGGER.info("the WIKITIONARY canonical entry hindiWord={} wordIndex={} indicated in Master does not exist", master.getHindiWord(), master.getWordIndex());
                        return false;
                    }else if (!isDerivatePOS(wikitionaryEntry.get().getPartOfSpeech(), master.getPartOfSpeech())){
                        LOGGER.info("the POS indicated in Master ({}) is not a derivate of the entry in WIKITIONARY ({})", master.getPartOfSpeech(), wikitionaryEntry.get().getPartOfSpeech());
                        return false;
                    }
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
