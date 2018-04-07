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
        if (item.isPresent("hindiWord")){
            master.setHindiWord(item.getString("hindiWord"));
        }

        if (item.isPresent("wordIndex")){
            master.setWordIndex(item.getInt("wordIndex"));
        }

        if (item.isPresent("urduSpelling")){
            master.setUrduSpelling(item.getString("urduSpelling"));
        }

        if (item.isPresent("partOfSpeech")){
            master.setPartOfSpeech(PartOfSpeech.valueOf(item.getString("partOfSpeech")));
        }

        if (item.isPresent("canonicalKeys")){
            master.setCanonicalKeys((List<CanonicalKey>)item.get("canonicalKeys"));
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
        result.put("hindiWord", new AttributeValue(master.getHindiWord()));
        result.put("wordIndex", new AttributeValue(Integer.toString(master.getWordIndex())));
        result.put("urduSpelling", new AttributeValue(master.getUrduSpelling()));
        result.put("partOfSpeech", new AttributeValue(master.getPartOfSpeech().name()));
        result.put("canonicalKeys", cksList);
        result.put("accidence", accsList  );
        return result;
    }

    public static Master fromAvMap(Map<String, AttributeValue> sAvs){
        Master master = new Master();
        master.setHindiWord(sAvs.get("hindiWord").getS())
                .setPartOfSpeech(PartOfSpeech.valueOf(sAvs.get("partOfSpeech").getS()))
                .setUrduSpelling(sAvs.get("urduSpelling").getS())
                .setWordIndex(Integer.valueOf(sAvs.get("wordIndex").getN()));

        List<CanonicalKey> canonicalKeys = sAvs.get("canonicalKeys")
                .getL().stream()
                .map(av -> CanonicalKey.fromAvMap(av.getM()) )
                .collect(Collectors.toList());

        AttributeValue accidenceAv = sAvs.get("accidence");
        if (accidenceAv!= null) {
            List<Accidence> accidence =
            accidenceAv
                    .getL().stream()
                    .map(av -> Accidence.valueOf(av.getS()))
                    .collect(Collectors.toList());
            master.setAccidence(accidence);
        }

        master.setCanonicalKeys(canonicalKeys);


        return master;
    }

    public static Item convert(Master master){
        Item item = new Item();

        item = item.with("hindiWord", master.getHindiWord());

        item = item.with("wordIndex", master.getWordIndex());

        item = item.with("urduSpelling", master.getUrduSpelling());

        item = item.with("partOfSpeech", master.getPartOfSpeech().name());

        item = item.withList("canonicalKeys", master.getCanonicalKeys().stream()
                .map(CanonicalKey::toMap).collect(Collectors.toList()));

        List<Accidence> accidence = master.getAccidence();
        if (accidence != null) {
            item = item.withList("accidence", accidence.stream()
                    .map(Accidence::name).collect(Collectors.toList()));
        }

        return item;
    }


}
