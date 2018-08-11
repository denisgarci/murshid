package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.murshid.dynamo.domain.NotInflected;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.PartOfSpeech;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NotInflectedConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotInflectedConverter.class);

    public static NotInflected convert(Item item){
        NotInflected inflected = new NotInflected();

        if (item.isPresent("hindi")){
            inflected.setHindi(item.getString("hindi"));
        }

        if (item.isPresent("urdu")){
            inflected.setUrdu(item.getString("urdu"));
        }

        if (item.isPresent("hindi_index")){
            inflected.setHindiIndex(item.getInt("hindi_index"));
        }

        if (item.isPresent("part_of_speech")){
            inflected.setPartOfSpeech(PartOfSpeech.valueOf(item.getString("part_of_speech")));
        }

        if (item.isPresent("master_dictionary_key")){
            inflected.setMasterDictionaryKey(DictionaryKey.fromMap(item.getMap("master_dictionary_key")));
        }


        return inflected;
    }

    public static NotInflected fromAvMap(Map<String, AttributeValue> sAvs){
        NotInflected master = new NotInflected();
        master
            .setHindi(sAvs.get("hindi").getS())
            .setUrdu(sAvs.get("urdu").getS())
            .setPartOfSpeech(PartOfSpeech.valueOf(sAvs.get("part_of_speech").getS()))
            .setHindiIndex(Integer.valueOf(sAvs.get("hindi_index").getN()))
            .setMasterDictionaryKey(DictionaryKey.fromAvMap(sAvs.get("master_dictionary_key").getM()));

        return master;
    }

    public static Item convert(NotInflected master){
        Item item = new Item();

        item = item.with("hindi", master.getHindi());

        item = item.with("urdu", master.getUrdu());

        item = item.with("hindi_index", master.getHindiIndex());

        item = item.with("part_of_speech", master.getPartOfSpeech().name());

        item = item.withMap("master_dictionary_key", master.getMasterDictionaryKey().toMap());

        return item;
    }


}
