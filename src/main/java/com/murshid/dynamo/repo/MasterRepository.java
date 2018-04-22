package com.murshid.dynamo.repo;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.murshid.dynamo.domain.Master;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.MasterConverter;

import javax.inject.Named;
import java.util.Iterator;
import java.util.Optional;

@Named
public class MasterRepository {

    public Optional<Master> findOne(String hindiWord, int wordIndex){
        Table table = DynamoAccessor.dynamoDB.getTable("master");
        KeyAttribute keyAttribute = new KeyAttribute("title_latin", hindiWord);
        PrimaryKey primaryKey = new PrimaryKey().addComponent("hindi_word", hindiWord)
                .addComponent("word_index", wordIndex);
        Item item = table.getItem(primaryKey);
        if (item == null){
            return Optional.empty();
        }else{
            return Optional.of(MasterConverter.convert(item));
        }
    }

    public Iterator<Item> findByCanonicalWord(String canonicalWord){
        Table table = DynamoAccessor.dynamoDB.getTable("master");

        Index index = table.getIndex("idx-canonical_word");

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("canonical_word = :v_date")
                .withValueMap(new ValueMap().withString(":v_date",canonicalWord));


        ItemCollection<QueryOutcome> items = index.query(spec);
        Iterator<Item> iter = items.iterator();
        return iter;
    }

    public void save(Master master){
        Table table = DynamoAccessor.dynamoDB.getTable("master");
        Item item = MasterConverter.convert(master);
        table.putItem(item);
    }

}
