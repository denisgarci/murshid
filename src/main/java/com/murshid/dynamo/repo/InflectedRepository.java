package com.murshid.dynamo.repo;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.InflectedConverter;

import javax.inject.Named;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Named
public class InflectedRepository {

    public List<Map<String, AttributeValue>> scanAll(){
        ScanRequest scanRequest = new ScanRequest().withTableName("inflected");

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        return scanResult.getItems();
    }

    public Optional<Inflected> findOne(String hindiWord, int wordIndex){
        Table table = DynamoAccessor.dynamoDB.getTable("inflected");

        PrimaryKey primaryKey = new PrimaryKey().addComponent("inflected_hindi", hindiWord)
                .addComponent("inflected_hindi_index", wordIndex);
        Item item = table.getItem(primaryKey);
        if (item == null){
            return Optional.empty();
        }else{
            return Optional.of(InflectedConverter.convert(item));
        }
    }

    public Iterator<Item> findByInflectedWord(String inflectedHindi){
        Table table = DynamoAccessor.dynamoDB.getTable("inflected");

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("inflected_hindi = :v_param")
                .withValueMap(new ValueMap().withString(":v_param",inflectedHindi));

        ItemCollection<QueryOutcome> items = table.query(spec);
        Iterator<Item> iter = items.iterator();
        return iter;
    }

    public Iterator<Item> findByMasterDictionaryId(int masterDictionaryId){
        Table table = DynamoAccessor.dynamoDB.getTable("inflected");

        Index index = table.getIndex("idx-master_dictionary_id");

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("master_dictionary_id = :v_date")
                .withValueMap(new ValueMap().withInt(":v_date", masterDictionaryId));

        ItemCollection<QueryOutcome> items = index.query(spec);
        Iterator<Item> iter = items.iterator();
        return iter;
    }

    public Iterator<Item> findByCanonicalWord(String canonicalWord){
        Table table = DynamoAccessor.dynamoDB.getTable("inflected");

        Index index = table.getIndex("idx-canonical_hindi");

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("canonical_hindi = :v_date")
                .withValueMap(new ValueMap().withString(":v_date",canonicalWord));

        ItemCollection<QueryOutcome> items = index.query(spec);
        Iterator<Item> iter = items.iterator();
        return iter;
    }

    public void save(Inflected master){
        Table table = DynamoAccessor.dynamoDB.getTable("inflected");
        Item item = InflectedConverter.convert(master);
        table.putItem(item);
    }

    public void delete(Inflected master){
        Table table = DynamoAccessor.dynamoDB.getTable("inflected");
        PrimaryKey primaryKey = new PrimaryKey().addComponent("inflected_hindi", master.getInflectedHindi())
                .addComponent("inflected_hindi_index", master.getInflectedHindiIndex());
        table.deleteItem(primaryKey);
    }


}
