package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.murshid.dynamo.domain.Song;
import com.murshid.persistence.domain.views.WordListMasterEntry;
import jersey.repackaged.com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SongConverter {

    static ObjectMapper mapper = new ObjectMapper();

    public static Song convert(Item item){
        Song song = new Song();
        if (item.isPresent("title_hindi")){
            song.setTitleHindi(item.getString("title_hindi"));
        }

        if (item.isPresent("title_latin")){
            song.setTitleLatin(item.getString("title_latin"));
        }

        if (item.isPresent("author")){
            song.setAuthor(item.getString("author"));
        }

        if (item.isPresent("song")){
            song.setSong(item.getString("song"));
        }

        if (item.isPresent("media")){
            song.setMedia((List<String>) item.get("media"));
        }

        if (item.isPresent("word_list")){
            song.setWordList((Map<String, String>) item.get("word_list"));
        }





            if (item.isPresent("word_list_master")) {
                List<WordListMasterEntry> wordListMasterEntries = Lists.newArrayList();
                List<Map<String, Object>> itemList = item.getList("word_list_master");
                List<WordListMasterEntry> wlmEntries = itemList.stream()
                        .map(a -> WordListMasterEntryConverter.fromMap(a))
                        .collect(Collectors.toList());

                song.setWordListMaster(wlmEntries);
            }


        return song;
    }

    private static WordListMasterEntry fromJson(String json){
        try{
             return mapper.readValue(json, WordListMasterEntry.class);
        }catch (IOException ex){
            throw new RuntimeException("cannpt understand this json as a WordListMasterEntry" + json);
        }
    }


    public static Item convert(Song song){
        Item item = new Item();

        item = item.with("song", song.getSong());

        item = item.with("author", song.getAuthor());

        item = item.with("title_latin", song.getTitleLatin());

        item = item.with("title_hindi", song.getTitleHindi());

        item = item.withList("media", song.getMedia());

        item = item.withMap("word_list", song.getWordList());

        List<WordListMasterEntry> wordListMasterEntries = song.getWordListMaster();
        List<Map> items = WordListMasterEntryConverter.toMaps(wordListMasterEntries);


        item = item.withList("word_list_master", items);

        return item;
    }


}
