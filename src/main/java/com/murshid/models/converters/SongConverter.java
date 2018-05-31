package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.murshid.dynamo.domain.Song;
import com.murshid.persistence.domain.views.SongWordsToInflectedTable;
import com.murshid.persistence.domain.views.SongWordsToNotInflectedTable;
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

        if (item.isPresent("english_translation")){
            song.setEnglishTranslation(item.getString("english_translation"));
        }

        if (item.isPresent("english_translation_html")){
            song.setEnglishTranslationHtml(item.getString("english_translation_html"));
        }


        if (item.isPresent("media")){
            song.setMedia((List<String>) item.get("media"));
        }

        if (item.isPresent("word_list_master")) {
            List<Map<String, Object>> itemList = item.getList("word_list_master");
            List<SongWordsToInflectedTable> wlmEntries = itemList.stream()
                    .map(a -> WordListMasterEntryConverter.fromMap(a))
                    .collect(Collectors.toList());

            song.setWordListMaster(wlmEntries);
        }

        if (item.isPresent("word_list_not_inflected")) {
            List<Map<String, Object>> itemList = item.getList("word_list_not_inflected");
            List<SongWordsToNotInflectedTable> wlmEntries = itemList.stream()
                    .map(a -> WordListNotInflectedEntryConverter.fromMap(a))
                    .collect(Collectors.toList());

            song.setWordListNotInflected(wlmEntries);
        }


        if (item.isPresent("html")){
            song.setHtml(item.getString("html"));
        }

        if (item.isPresent("inflected_entries")){
            song.setInflectedEntries((String)item.get("inflected_entries"));
        }

        if (item.isPresent("not_inflected_entries")){
            song.setNotInflectedEntries((String)item.get("not_inflected_entries"));
        }


        if (item.isPresent("dictionary_entries")){
            song.setDictionaryEntriesInflected((String)item.get("dictionary_entries"));
        }

        if (item.isPresent("dictionary_entries_not_inflected")){
            song.setDictionaryEntriesNotInflected((String)item.get("dictionary_entries_not_inflected"));
        }


        return song;
    }

    private static SongWordsToInflectedTable fromJson(String json){
        try{
             return mapper.readValue(json, SongWordsToInflectedTable.class);
        }catch (IOException ex){
            throw new RuntimeException("cannpt understand this json as a SongWordsToInflectedTable" + json);
        }
    }


    public static Item convert(Song song){
        Item item = new Item();

        item = item.with("song", song.getSong());

        item = item.with("english_translation", song.getEnglishTranslation());

        item = item.with("english_translation_html", song.getEnglishTranslationHtml());

        item = item.with("author", song.getAuthor());

        item = item.with("title_latin", song.getTitleLatin());

        item = item.with("title_hindi", song.getTitleHindi());

        item = item.withList("media", song.getMedia());

        List<SongWordsToInflectedTable> wordListMasterEntries = song.getWordListMaster();
        if (wordListMasterEntries != null) {
            List<Map> inflectedItems = WordListMasterEntryConverter.toMaps(wordListMasterEntries);
            item = item.withList("word_list_master", inflectedItems);
        }

        if (song.getHtml() != null) {
            item = item.withString("html", song.getHtml());
        }

        List<SongWordsToNotInflectedTable> songWordsToNotInflectedTables = song.getWordListNotInflected();
        if (songWordsToNotInflectedTables != null) {
            List<Map> notInflectedItems = WordListNotInflectedEntryConverter.toMaps(songWordsToNotInflectedTables);
            item = item.withList("word_list_not_inflected", notInflectedItems);
        }

        if (song.getInflectedEntries() != null ) {
            item = item.withString("inflected_entries", song.getInflectedEntries());
        }

        if (song.getNotInflectedEntries() != null ) {
            item = item.withString("not_inflected_entries", song.getNotInflectedEntries());
        }

        if (song.getDictionaryEntriesInflected() != null) {
            item = item.withString("dictionary_entries", song.getDictionaryEntriesInflected());
        }

        if (song.getDictionaryEntriesNotInflected() != null) {
            item = item.withString("dictionary_entries_not_inflected", song.getDictionaryEntriesNotInflected());
        }


        return item;
    }


}
