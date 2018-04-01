package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.murshid.dynamo.domain.Master;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MasterConverter {

    public static Master convert(Item item){
        Master master = new Master();
        if (item.isPresent("hindi_word")){
            master.setHindiWord(item.getString("hindi_word"));
        }

        if (item.isPresent("word_index")){
            master.setWordIndex(item.getInt("word_index"));
        }

        if (item.isPresent("urdu_spelling")){
            master.setUrduSpelling(item.getString("urdu_spelling"));
        }

        if (item.isPresent("part_of_speech")){
            master.setPartOfSpeech(PartOfSpeech.valueOf(item.getString("part_of_speech")));
        }

        if (item.isPresent("canonical_keys")){
            master.setCanonicalKeys((List<CanonicalKey>)item.get("canonical_keys"));
        }


        if (item.isPresent("accidence")){
            master.setAccidence((List<Accidence>)item.get("accidence"));
        }

        return master;
    }

    public static Map<String, AttributeValue> convertToAvMap(Master master){

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
        result.put("hindi_word", new AttributeValue(master.getHindiWord()));
        result.put("word_index", new AttributeValue(Integer.toString(master.getWordIndex())));
        result.put("urdu_spelling", new AttributeValue(master.getUrduSpelling()));
        result.put("part_of_speech", new AttributeValue(master.getPartOfSpeech().name()));
        result.put("canonical_keys", cksList);
        result.put("accidence", accsList  );
        return result;
    }

    public static Master fromAvMap(Map<String, AttributeValue> sAvs){
        Master master = new Master();
        master.setHindiWord(sAvs.get("hindi_word").getS())
                .setPartOfSpeech(PartOfSpeech.valueOf(sAvs.get("part_of_speech").getS()))
                .setUrduSpelling(sAvs.get("urdu_spelling").getS())
                .setWordIndex(Integer.valueOf(sAvs.get("word_index").getN()));

        List<CanonicalKey> canonicalKeys = sAvs.get("canonical_keys")
                .getL().stream()
                .map(av -> CanonicalKey.fromAvMap(av.getM()) )
                .collect(Collectors.toList());

        List<Accidence> accidence = sAvs.get("accidence")
                .getL().stream()
                .map(av -> Accidence.valueOf(av.getS()))
                .collect(Collectors.toList());

        master.setCanonicalKeys(canonicalKeys);
        master.setAccidence(accidence);

        return master;
    }

    public static Item convert(Master master){
        Item item = new Item();

        item = item.with("hindi_word", master.getHindiWord());

        item = item.with("word_index", master.getWordIndex());

        item = item.with("urdu_spelling", master.getUrduSpelling());

        item = item.with("part_of_speech", master.getPartOfSpeech().name());

        item = item.withList("canonical_keys", master.getCanonicalKeys().stream()
                .map(CanonicalKey::toMap).collect(Collectors.toList()));

        item = item.withList("accidence", master.getAccidence().stream()
                             .map(Accidence::name).collect(Collectors.toList()));

        return item;
    }


}
