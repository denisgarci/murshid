package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.murshid.persistence.domain.views.InflectedKey;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class InflectedKeyConverter {

    public static InflectedKey convert(Item item){
        InflectedKey inflectedKey = new InflectedKey();
        if (item.isPresent("inflected_hindi")){
            inflectedKey.setInflectedHindi(item.getString("inflected_hindi"));
        }

        if (item.isPresent("inflected_hindi_index")){
            inflectedKey.setInflectedHindiIndex(item.getInt("inflected_hindi_index"));
        }

        return inflectedKey;
    }

    public static Item convert(InflectedKey inflectedKey){
        Item item = new Item();

        item = item.with("inflected_hindi", inflectedKey.getInflectedHindi());

        item = item.with("inflected_hindi_index", inflectedKey.getInflectedHindiIndex());

        return item;
    }

    public static InflectedKey fromMap(Map<String, Object> sAvs){
        InflectedKey inflectedKey = new InflectedKey();
        Object wordIndex =  sAvs.get("inflected_hindi_index");

        inflectedKey.setInflectedHindi((String)sAvs.get("inflected_hindi"))
                .setInflectedHindiIndex(new BigDecimal(wordIndex.toString()).intValue());

        return inflectedKey;
    }

    public static Map<String, AttributeValue> toAvMap(InflectedKey master){

        Map<String, AttributeValue> result = new HashMap<>();
        result.put("inflected_hindi", new AttributeValue(master.getInflectedHindi()));
        result.put("inflected_hindi_index", new AttributeValue(Integer.toString(master.getInflectedHindiIndex())));

        return result;
    }


}
