package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.murshid.persistence.domain.views.MasterKey;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MasterKeyConverter {

    public static MasterKey convert(Item item){
        MasterKey masterKey = new MasterKey();
        if (item.isPresent("hindi_word")){
            masterKey.setHindiWord(item.getString("hindi_word"));
        }

        if (item.isPresent("word_index")){
            masterKey.setWordIndex(item.getInt("word_index"));
        }

        return masterKey;
    }

    public static Item convert(MasterKey masterKey){
        Item item = new Item();

        item = item.with("hindi_word", masterKey.getHindiWord());

        item = item.with("word_index", masterKey.getWordIndex());

        return item;
    }

    public static MasterKey fromMap(Map<String, Object> sAvs){
        MasterKey masterKey = new MasterKey();
        Object wordIndex =  sAvs.get("word_index");

        masterKey.setHindiWord((String)sAvs.get("hindi_word"))
                .setWordIndex(new BigDecimal(wordIndex.toString()).intValue());

        return masterKey;
    }

    public static Map<String, AttributeValue> toAvMap(MasterKey master){

        Map<String, AttributeValue> result = new HashMap<>();
        result.put("hindi_word", new AttributeValue(master.getHindiWord()));
        result.put("word_index", new AttributeValue(Integer.toString(master.getWordIndex())));

        return result;
    }


}
