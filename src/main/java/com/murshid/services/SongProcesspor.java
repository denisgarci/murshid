package com.murshid.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.common.collect.Sets;
import com.murshid.dynamo.domain.Master;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.persistence.repo.SpellCheckRepository;
import com.murshid.utils.SongUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Named
public class SongProcesspor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryLetterIngestor.class);

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
            return tokens.stream().filter(s-> spellCheckRepository.findOne(s) == null).collect(Collectors.toSet());
        }
        return Sets.newTreeSet();
    }

    /**
     * Used to measure progress on a song.
     * Returns all song tokens that are not yet in Master.
     * @param songTitleLatin            e.g. "alvida"
     * @return                          a set of all tokens not present in Master
     */
    public Set<String> wordTokensNotInMaster(@Nonnull String songTitleLatin){
        Optional<String> song = getSong(songTitleLatin);
        Set<String> result = new HashSet<>();
        if (song.isPresent()){
            Set<String> tokens = SongUtils.hindiTokens(song.get());
            for (String token: tokens){
                List<Master> inMaster =  masterService.getWords(token);
                if (inMaster.isEmpty()){
                    result.add(token);
                }
            }
        }
        return result;
    }


    @Inject
    private SpellCheckRepository spellCheckRepository;

    @Inject
    private MasterService masterService;



}
