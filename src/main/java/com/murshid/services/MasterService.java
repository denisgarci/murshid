package com.murshid.services;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.murshid.dynamo.domain.Master;
import com.murshid.dynamo.repo.MasterRepository;
import com.murshid.models.CanonicalKey;
import com.murshid.models.DictionaryKey;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.MasterConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
public class MasterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterService.class);

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

    /**
     * checks if all canonical keys contained in Master (if any)
     * really exist in the respective entities
     * @param master    the Master recprd
     * @return          true if all canonical keys exist, false otherwise
     */
    public boolean validateCanonicalKeys(Master master){

        boolean anyDoesntExist = false;
        if (master.getCanonicalKeys() == null){
            return true;
        }
        for (int i=0; i< master.getCanonicalKeys().size(); i++){
            CanonicalKey ck = master.getCanonicalKeys().get(i);
            DictionaryKey dk = new DictionaryKey().setWord(ck.word).setWordIndex(ck.wordIndex);
            switch (ck.dictionarySource){
                case PRATTS:
                    if (!prattsService.exists(dk)){
                        anyDoesntExist = true;
                        break;
                    }
                    break;
                case GONZALO:
                    if (!gonzaloService.exists(dk)){
                        anyDoesntExist = true;
                        break;
                    }
                    break;
                case REKHTA:
                    if (!rekhtaService.exists(dk)){
                        anyDoesntExist = true;
                        break;
                    }
                    break;
                case WIKITIONARY:
                    if (!wikitionaryService.exists(dk)){
                        anyDoesntExist = true;
                        break;
                    }
                    break;
            }

        };
        return !anyDoesntExist;
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
