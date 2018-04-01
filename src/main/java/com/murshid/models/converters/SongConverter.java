package com.murshid.models.converters;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.murshid.dynamo.domain.Song;

import java.util.List;

public class SongConverter {

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

        return song;
    }


    public static Item convert(Song song){
        Item item = new Item();

        item = item.with("song", song.getSong());

        item = item.with("author", song.getAuthor());

        item = item.with("latin_title", song.getTitleLatin());

        item = item.with("hindi_title", song.getTitleHindi());

        item = item.withList("media", song.getMedia());

        return item;
    }


}
