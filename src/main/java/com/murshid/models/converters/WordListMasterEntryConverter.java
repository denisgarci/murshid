package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.murshid.persistence.domain.views.InflectedKey;
import com.murshid.persistence.domain.views.SongWordsToInflectedTable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WordListMasterEntryConverter {

    static Gson gson = new Gson();

    public static SongWordsToInflectedTable fromMap(Map map){
        SongWordsToInflectedTable songWordsToInflectedTable = new SongWordsToInflectedTable();
        songWordsToInflectedTable.setIndices((List)map.get("song_word_indices"));

        InflectedKey inflectedKey = InflectedKeyConverter.fromMap((Map)map.get("inflected_key"));

        songWordsToInflectedTable.setInflectedKey(inflectedKey);

       return songWordsToInflectedTable;
    }

    public static Map toMap(SongWordsToInflectedTable wlME){
        String jsonString = gson.toJson(wlME);

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(jsonString, type);
    }

    public static Item toItem(SongWordsToInflectedTable songWordsToInflectedTable){
        String jsonString = gson.toJson(songWordsToInflectedTable);
        return Item.fromJSON(jsonString);
    }

    public static List<Item> toItems(List<SongWordsToInflectedTable> wordListMasterEntries){
        return wordListMasterEntries.stream().map(WordListMasterEntryConverter::toItem).collect(Collectors.toList());
    }

    public static List<Map> toMaps(List<SongWordsToInflectedTable> wordListMasterEntries){
        return wordListMasterEntries.stream().map(WordListMasterEntryConverter::toMap).collect(Collectors.toList());
    }


}
