package com.murshid.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.murshid.dynamo.domain.Master;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;

import java.util.List;
import java.util.stream.Collectors;

public class MasterItemConverter {

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
