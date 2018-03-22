package com.murshid.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.common.collect.Sets;
import com.murshid.dynamo.DynamoAccessor;
import com.murshid.persistence.HindiWordsRepository;
import com.murshid.utils.SongUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Named
public class SongProcesspor {

    public Optional<String> getSong(@Nonnull String songTitleLatin) {

        Table table = DynamoAccessor.dynamoDB.getTable("songs");
        Item item = table.getItem("title_latin", songTitleLatin);

        if (item.isPresent("song")){
            return Optional.of(item.getString("song"));
        } else {
            return Optional.empty();
        }
    }

    public Set<String> newWordsInSong(@Nonnull String songTitleLatin){
        Optional<String> song = getSong(songTitleLatin);
        if (song.isPresent()){
            Set<String> tokens = SongUtils.hindiTokens(song.get());
            return tokens.stream().filter(s-> hindiWordsRepository.findOne(s) == null).collect(Collectors.toSet());
        }
        return Sets.newTreeSet();
    }


    @Inject
    private HindiWordsRepository hindiWordsRepository;


}
