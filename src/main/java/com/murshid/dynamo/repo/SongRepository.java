package com.murshid.dynamo.repo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.murshid.dynamo.domain.Song;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.SongConverter;

import javax.inject.Named;

@Named
public class SongRepository {

    public Song findOne(String latinTitle){
        Table table = DynamoAccessor.dynamoDB.getTable("songs");
        KeyAttribute keyAttribute = new KeyAttribute("title_latin", latinTitle);
        Item item = table.getItem(keyAttribute);
        if (item == null){
            return null;
        }else{
            return SongConverter.convert(item);
        }
    }

    public void save(Song song){
        Table table = DynamoAccessor.dynamoDB.getTable("songs");
        Item item = SongConverter.convert(song);
        table.putItem(item);
    }

}
