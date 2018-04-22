package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.murshid.persistence.domain.views.MasterKey;
import com.murshid.persistence.domain.views.WordListMasterEntry;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WordListMasterEntryConverter {

    static Gson gson = new Gson();

    public static WordListMasterEntry fromMap(Map map){
        WordListMasterEntry wordListMasterEntry = new WordListMasterEntry();
        wordListMasterEntry.setIndices((List)map.get("indices"));

        MasterKey masterKey = MasterKeyConverter.fromMap((Map)map.get("master_key"));

        wordListMasterEntry.setMasterKey(masterKey);

       return wordListMasterEntry;
    }

    public static Map toMap(WordListMasterEntry wlME){
        String jsonString = gson.toJson(wlME);

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(jsonString, type);
    }

    public static Item toItem(WordListMasterEntry wordListMasterEntry){
        String jsonString = gson.toJson(wordListMasterEntry);
        return Item.fromJSON(jsonString);
    }

    public static List<Item> toItems(List<WordListMasterEntry> wordListMasterEntries){
        return wordListMasterEntries.stream().map(WordListMasterEntryConverter::toItem).collect(Collectors.toList());
    }

    public static List<Map> toMaps(List<WordListMasterEntry> wordListMasterEntries){
        return wordListMasterEntries.stream().map(WordListMasterEntryConverter::toMap).collect(Collectors.toList());
    }


}
