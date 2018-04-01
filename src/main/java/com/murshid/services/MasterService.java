package com.murshid.services;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.persistence.repo.HindiWordsRepository;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
public class MasterService {

    public List<Map<String, AttributeValue>> getWords(@Nonnull String word) {

        Map<String, AttributeValue> expressionAttributeValues =  new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":hindiWord", new AttributeValue().withS(word));

        ScanRequest scanRequest = new ScanRequest()
        .withTableName("master").withFilterExpression( "hindi_word = :hindiWord")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        List<Map<String, AttributeValue>> soResult = scanResult.getItems();
        return soResult;
    }

    @Inject
    private HindiWordsRepository hindiWordsRepository;


}
