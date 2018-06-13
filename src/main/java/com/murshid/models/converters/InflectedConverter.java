package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InflectedConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(InflectedConverter.class);

    public static Inflected convert(Item item){
        Inflected inflected = new Inflected();

        if (item.isPresent("inflected_hindi")){
            inflected.setInflectedHindi(item.getString("inflected_hindi"));
        }

        if (item.isPresent("inflected_urdu")){
            inflected.setInflectedUrdu(item.getString("inflected_urdu"));
        }

        if (item.isPresent("inflected_hindi_index")){
            inflected.setInflectedHindiIndex(item.getInt("inflected_hindi_index"));
        }

        if (item.isPresent("part_of_speech")){
            inflected.setPartOfSpeech(PartOfSpeech.valueOf(item.getString("part_of_speech")));
        }

        if (item.isPresent("master_dictionary_key")){
            inflected.setMasterDictionaryKey(DictionaryKey.fromMap(item.getMap("master_dictionary_key")));
        }

        if (item.isPresent("master_dictionary_id")){
            inflected.setMasterDictionaryId(item.getInt("master_dictionary_id"));
        }

        if (item.isPresent("accidence")){
            inflected.setAccidence( item.getList("accidence").stream().map(o -> Accidence.valueOf(o.toString())).collect(Collectors.toSet()));
        }

        return inflected;
    }

    public static Map<String, AttributeValue> convertToAvMap(Inflected master){

        List<AttributeValue> accidences = master.getAccidence().stream()
                .map(acc -> {
                    AttributeValue attributeValue = new AttributeValue();
                    attributeValue.setS(acc.name());
                    return attributeValue;
                })
                .collect(Collectors.toList());

        AttributeValue accsList= new AttributeValue();
        accsList.setL(accidences);

        Map<String, AttributeValue> result = new HashMap<>();
        result.put("inflected_hindi", new AttributeValue(master.getInflectedHindi()));
        result.put("inflected_urdu", new AttributeValue(master.getInflectedUrdu()));
        AttributeValue ihi = new AttributeValue(); ihi.setN(Integer.toString(master.getInflectedHindiIndex()));
        result.put("inflected_hindi_index", ihi);
        result.put("part_of_speech", new AttributeValue(master.getPartOfSpeech().name()));
        AttributeValue mdk = new AttributeValue();
        mdk.setM(DictionaryKey.toAvMap(master.getMasterDictionaryKey()));
        result.put("master_dictionary_key", mdk);
        AttributeValue mdi = new AttributeValue(); ihi.setN(Integer.toString(master.getMasterDictionaryId()));
        result.put("master_dictionary_id", mdi);
        result.put("accidence", accsList  );

        return result;
    }

    public static Inflected fromAvMap(Map<String, AttributeValue> sAvs){
        Inflected master = new Inflected();
        master
                .setInflectedHindi(sAvs.get("inflected_hindi").getS())
                .setInflectedUrdu(sAvs.get("inflected_urdu").getS())
                .setPartOfSpeech(PartOfSpeech.valueOf(sAvs.get("part_of_speech").getS()))
                .setInflectedHindiIndex(Integer.valueOf(sAvs.get("inflected_hindi_index").getN()))
                .setMasterDictionaryId(Integer.valueOf(sAvs.get("master_dictionary_id").getN()))
                .setMasterDictionaryKey(DictionaryKey.fromAvMap(sAvs.get("master_dictionary_key").getM()));

        AttributeValue accidenceAv = sAvs.get("accidence");

        if (accidenceAv!= null) {
            Set<Accidence> accidence =
            accidenceAv
                    .getL().stream()
                    .map(av -> Accidence.valueOf(av.getS()))
                    .collect(Collectors.toSet());
            master.setAccidence(accidence);
        }

        return master;
    }

    public static Item convert(Inflected master){
        Item item = new Item();

        item = item.with("inflected_hindi", master.getInflectedHindi());

        item = item.with("inflected_urdu", master.getInflectedUrdu());

        item = item.withInt("inflected_hindi_index", master.getInflectedHindiIndex());

        item = item.with("part_of_speech", master.getPartOfSpeech().name());

        item = item.withInt("master_dictionary_id", master.getMasterDictionaryId());

        item = item.withMap("master_dictionary_key", master.getMasterDictionaryKey().toMap());

        Set<Accidence> accidence = master.getAccidence();
        if (accidence != null) {
            item = item.withList("accidence", accidence.stream()
                    .map(Accidence::name).collect(Collectors.toList()));
        }

        return item;
    }


}
