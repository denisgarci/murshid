package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.murshid.persistence.domain.views.InflectedKey;
import com.murshid.persistence.domain.views.NotInflectedKey;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class NotInflectedKeyConverter {

    public static NotInflectedKey convert(Item item){
        NotInflectedKey inflectedKey = new NotInflectedKey();
        if (item.isPresent("hindi")){
            inflectedKey.setHindi(item.getString("hindi"));
        }

        if (item.isPresent("hindi_index")){
            inflectedKey.setHindiIndex(item.getInt("hindi_index"));
        }

        return inflectedKey;
    }

    public static Item convert(NotInflectedKey inflectedKey){
        Item item = new Item();

        item = item.with("hindi", inflectedKey.getHindi());

        item = item.with("hindi_index", inflectedKey.getHindiIndex());

        return item;
    }

    public static NotInflectedKey fromMap(Map<String, Object> sAvs){
        NotInflectedKey inflectedKey = new NotInflectedKey();
        Object wordIndex =  sAvs.get("hindi_index");

        inflectedKey.setHindi((String)sAvs.get("hindi"))
                .setHindiIndex(new BigDecimal(wordIndex.toString()).intValue());

        return inflectedKey;
    }

    public static Map<String, AttributeValue> toAvMap(NotInflectedKey master){

        Map<String, AttributeValue> result = new HashMap<>();
        result.put("hindi", new AttributeValue(master.getHindi()));
        result.put("hindi_index", new AttributeValue(Integer.toString(master.getHindiIndex())));

        return result;
    }


}
