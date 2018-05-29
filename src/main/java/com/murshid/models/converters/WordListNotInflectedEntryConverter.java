package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.murshid.persistence.domain.views.InflectedKey;
import com.murshid.persistence.domain.views.NotInflectedKey;
import com.murshid.persistence.domain.views.SongWordsToInflectedTable;
import com.murshid.persistence.domain.views.SongWordsToNotInflectedTable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WordListNotInflectedEntryConverter {

    static Gson gson = new Gson();

    public static SongWordsToNotInflectedTable fromMap(Map map){
        SongWordsToNotInflectedTable songWordsToNotInflectedTable = new SongWordsToNotInflectedTable();
        songWordsToNotInflectedTable.setIndices((List)map.get("song_word_indices"));

        NotInflectedKey notInflectedKey = NotInflectedKeyConverter.fromMap((Map)map.get("not_inflected_key"));

        songWordsToNotInflectedTable.setNotInflectedKey(notInflectedKey);

       return songWordsToNotInflectedTable;
    }

    public static Map toMap(SongWordsToNotInflectedTable wlME){
        String jsonString = gson.toJson(wlME);

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(jsonString, type);
    }

    public static Item toItem(SongWordsToNotInflectedTable songWordsToInflectedTable){
        String jsonString = gson.toJson(songWordsToInflectedTable);
        return Item.fromJSON(jsonString);
    }

    public static List<Item> toItems(List<SongWordsToNotInflectedTable> wordListMasterEntries){
        return wordListMasterEntries.stream().map(WordListNotInflectedEntryConverter::toItem).collect(Collectors.toList());
    }

    public static List<Map> toMaps(List<SongWordsToNotInflectedTable> wordListMasterEntries){
        return wordListMasterEntries.stream().map(WordListNotInflectedEntryConverter::toMap).collect(Collectors.toList());
    }


}
