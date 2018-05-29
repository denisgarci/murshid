package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.NotInflected;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        if (item.isPresent("canonical_keys")){
            List<Object> cksObjList = (List)item.get("canonical_keys");
            Set<CanonicalKey> cksSet = cksObjList.stream().map(obj -> CanonicalKey.fromMap((Map)obj))
                    .collect(Collectors.toSet());
            inflected.setCanonicalKeys(cksSet);
        }

        return inflected;
    }

    public static Map<String, AttributeValue> convertToAvMap(NotInflected master){

        List<AttributeValue> canonicalKeys = master.getCanonicalKeys().stream()
                .map(ck -> {
                    AttributeValue attributeValue = new AttributeValue();
                    attributeValue.setM(CanonicalKey.toAvMap(ck));
                    return attributeValue;
                } )
                .collect(Collectors.toList());

        AttributeValue cksList= new AttributeValue();
        cksList.setL(canonicalKeys);

        Map<String, AttributeValue> result = new HashMap<>();
        result.put("hindi", new AttributeValue(master.getHindi()));
        result.put("inflected_urdu", new AttributeValue(master.getUrdu()));
        result.put("hindi_index", new AttributeValue(Integer.toString(master.getHindiIndex())));
        result.put("part_of_speech", new AttributeValue(master.getPartOfSpeech().name()));
        result.put("canonical_keys", cksList);

        return result;
    }

    public static NotInflected fromAvMap(Map<String, AttributeValue> sAvs){
        NotInflected master = new NotInflected();
        master
                .setHindi(sAvs.get("hindi").getS())
                .setUrdu(sAvs.get("urdu").getS())
                .setPartOfSpeech(PartOfSpeech.valueOf(sAvs.get("part_of_speech").getS()))
                .setHindiIndex(Integer.valueOf(sAvs.get("hindi_index").getN()));

        Set<CanonicalKey> canonicalKeys = sAvs.get("canonical_keys")
                .getL().stream()
                .map(av -> CanonicalKey.fromAvMap(av.getM()) )
                .collect(Collectors.toSet());

        master.setCanonicalKeys(canonicalKeys);


        return master;
    }

    public static Item convert(NotInflected master){
        Item item = new Item();

        item = item.with("hindi", master.getHindi());

        item = item.with("urdu", master.getUrdu());

        item = item.with("hindi_index", master.getHindiIndex());

        item = item.with("part_of_speech", master.getPartOfSpeech().name());

        item = item.withList("canonical_keys", master.getCanonicalKeys().stream()
                .map(CanonicalKey::toMap).collect(Collectors.toList()));

        return item;
    }


}
