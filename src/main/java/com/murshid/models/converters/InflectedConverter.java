package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.murshid.dynamo.domain.Inflected;
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

        if (item.isPresent("canonical_hindi")){
            inflected.setCanonicalHindi(item.getString("canonical_hindi"));
        }

        if (item.isPresent("canonical_urdu")){
            inflected.setCanonicalUrdu(item.getString("canonical_urdu"));
        }

        if (item.isPresent("canonical_keys")){
            List<Object> cksObjList = (List)item.get("canonical_keys");
            Set<CanonicalKey> cksSet = cksObjList.stream().map(obj -> CanonicalKey.fromMap((Map)obj))
                    .collect(Collectors.toSet());
            inflected.setCanonicalKeys(cksSet);
        }


        if (item.isPresent("accidence")){
            inflected.setAccidence(((List<String>)item.get("accidence")).stream().map(Accidence::valueOf).collect(Collectors.toSet()));
        }

        return inflected;
    }

    public static Map<String, AttributeValue> convertToAvMap(Inflected master){

        List<AttributeValue> canonicalKeys = master.getCanonicalKeys().stream()
                .map(ck -> {
                    AttributeValue attributeValue = new AttributeValue();
                    attributeValue.setM(CanonicalKey.toAvMap(ck));
                    return attributeValue;
                } )
                .collect(Collectors.toList());

        AttributeValue cksList= new AttributeValue();
        cksList.setL(canonicalKeys);

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
        result.put("inflected_hindi_index", new AttributeValue(Integer.toString(master.getInflectedHindiIndex())));
        result.put("part_of_speech", new AttributeValue(master.getPartOfSpeech().name()));
        result.put("canonical_hindi", new AttributeValue(master.getCanonicalHindi()));
        result.put("canonical_urdu", new AttributeValue(master.getCanonicalUrdu()));
        result.put("canonical_keys", cksList);
        result.put("accidence", accsList  );

        return result;
    }

    public static Inflected fromAvMap(Map<String, AttributeValue> sAvs){
        Inflected master = new Inflected();
        master
                .setInflectedHindi(sAvs.get("inflected_hindi").getS())
                .setInflectedUrdu(sAvs.get("inflected_urdu").getS())
                .setPartOfSpeech(PartOfSpeech.valueOf(sAvs.get("part_of_speech").getS()))
                .setCanonicalHindi(sAvs.get("canonical_hindi").getS())
                .setCanonicalUrdu(sAvs.get("canonical_urdu").getS())
                .setInflectedHindiIndex(Integer.valueOf(sAvs.get("inflected_hindi_index").getN()));

        Set<CanonicalKey> canonicalKeys = sAvs.get("canonical_keys")
                .getL().stream()
                .map(av -> CanonicalKey.fromAvMap(av.getM()) )
                .collect(Collectors.toSet());

        AttributeValue accidenceAv = sAvs.get("accidence");

        if (accidenceAv!= null) {
            Set<Accidence> accidence =
            accidenceAv
                    .getL().stream()
                    .map(av -> Accidence.valueOf(av.getS()))
                    .collect(Collectors.toSet());
            master.setAccidence(accidence);
        }

        master.setCanonicalKeys(canonicalKeys);


        return master;
    }

    public static Item convert(Inflected master){
        Item item = new Item();

        item = item.with("inflected_hindi", master.getInflectedHindi());

        item = item.with("inflected_urdu", master.getInflectedUrdu());

        item = item.with("canonical_hindi", master.getCanonicalHindi());

        item = item.with("canonical_urdu", master.getCanonicalUrdu());

        item = item.with("inflected_hindi_index", master.getInflectedHindiIndex());

        item = item.with("part_of_speech", master.getPartOfSpeech().name());

        item = item.withList("canonical_keys", master.getCanonicalKeys().stream()
                .map(CanonicalKey::toMap).collect(Collectors.toList()));

        Set<Accidence> accidence = master.getAccidence();
        if (accidence != null) {
            item = item.withList("accidence", accidence.stream()
                    .map(Accidence::name).collect(Collectors.toList()));
        }

        return item;
    }


}
