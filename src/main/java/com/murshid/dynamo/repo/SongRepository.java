package com.murshid.dynamo.repo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.murshid.dynamo.DynamoAccessor;
import com.murshid.dynamo.SongItemConverter;
import com.murshid.dynamo.domain.Song;

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
            return SongItemConverter.convert(item);
        }
    }

}
