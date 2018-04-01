package com.murshid.dynamo.repo;

import com.amazonaws.services.dynamodbv2.document.*;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.MasterConverter;
import com.murshid.dynamo.domain.Master;

import javax.inject.Named;

@Named
public class MasterRepository {

    public Master findOne(String hindiWord, int wordIndex){
        Table table = DynamoAccessor.dynamoDB.getTable("master");
        KeyAttribute keyAttribute = new KeyAttribute("title_latin", hindiWord);
        PrimaryKey primaryKey = new PrimaryKey().addComponent("hindi_word", hindiWord)
                .addComponent("word_index", wordIndex);
        Item item = table.getItem(primaryKey);
        if (item == null){
            return null;
        }else{
            return MasterConverter.convert(item);
        }
    }

    public void save(Master master){
        Table table = DynamoAccessor.dynamoDB.getTable("master");
        Item item = MasterConverter.convert(master);
        table.putItem(item);
    }

}
