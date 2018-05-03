package com.murshid.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.WordListMasterEntryConverter;
import com.murshid.persistence.domain.views.SongWordsToInflectedTable;
import com.murshid.persistence.repo.SpellCheckRepository;
import com.murshid.utils.SongUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class SongsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryLetterIngestor.class);

    /**
     * Rerurns the texts of a song from the Dynamo songs table, if it exists.
     * @param songTitleLatin        the latin title of the desired song
     * @return                      the song's text
     */
    public Optional<String> getSong(@Nonnull String songTitleLatin) {

        Table table = DynamoAccessor.dynamoDB.getTable("songs");
        Item item = table.getItem("title_latin", songTitleLatin);

        if (item.isPresent("song")){
            return Optional.of(item.getString("song"));
        } else {
            return Optional.empty();
        }
    }

    /**
     * re-ingests a song (already present in the Dynamo Songs) from a file, and re-writes it into Master.
     * @param songTitleLatin
     * @param fileName
     * @return
     */
    public boolean ingestSong(String songTitleLatin, String fileName){

        Song song = songRepository.findOne(songTitleLatin);

        if (song == null){
            return false;
        }else{

            try {
                String songText =  new String(Files.readAllBytes(
                        Paths.get(getClass().getClassLoader()
                                          .getResource("songs/" + fileName)
                                          .toURI())));

                song.setSong(songText);
                songRepository.save(song);
            }catch (URISyntaxException | IOException ex){
                return false;
            }

            return true;
        }
    }

    public Optional<Song> findByLatinTitle(String songTitleLatin){
        Song song = songRepository.findOne(songTitleLatin);
        if (song == null){
            return Optional.empty();
        }else {
            return Optional.of(song);
        }
    }


    /**
     * Grabs a song from the Dynamo Songs, and tells which of its word are not in the spell check repository
     * @param songTitleLatin
     * @return
     */
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
                List<Inflected> inMaster =  inflectedService.getByInflectedWord(token);
                if (inMaster.isEmpty()){
                    result.add(token);
                }
            }
        }
        return result;
    }

    /**
     * Locates a song by its latin title, then creates a map of number->word, which will be used
     * to geographically link those words to Master entries..
     * @param songTitleLatin        e.g. "Alvida"
     * @return                      a Map("10", "दिल")
     */
    private Map<String, String> createSongIndex(@Nonnull String songTitleLatin){
        Optional<String> song = getSong(songTitleLatin);
        Map<String, String> result = new LinkedHashMap<>();
        if (song.isPresent()){
            List<String> words = SongUtils.allHindiTokens(song.get());
            int i = 10;
            for (String word : words){
                result.put(Integer.toString(i), word);
                i+=10;
            }
        }
        return result;
    }

    public boolean validate(@Nonnull String songTitleLatin, SongWordsToInflectedTable songWordsToInflectedTable){
        Song song = songRepository.findOne(songTitleLatin);
        if (song == null){
            LOGGER.error("the song {} was not found in Master", songTitleLatin);
            return false;
        }

        String hindiWord= songWordsToInflectedTable.getInflectedKey().getInflectedHindi();
        int wordIndex = songWordsToInflectedTable.getInflectedKey().getInflectedHindiIndex();
        if (!inflectedService.exists(hindiWord, wordIndex)){
            LOGGER.error("the inflected key with inflected_hindi {} and inflected_hindi_index={} does not exist", hindiWord, wordIndex);
            return false;
        }

        if (song.getWordListMaster().contains(songWordsToInflectedTable)){
            LOGGER.error("the song {} already contains this songWordsToInflectedTable entry={}", songTitleLatin,
                         songWordsToInflectedTable);
            return false;
        }

        return true;
    }

    public void addEntryToWordListMaster(@Nonnull String songTitleLatin, SongWordsToInflectedTable songWordsToInflectedTable){
        List<String> indices = songWordsToInflectedTable.getIndices();

        Song song = songRepository.findOne(songTitleLatin);
        Map<String, String> result = new LinkedHashMap<>();
        if (song != null){
            Map<String, Object> entry = new HashMap<>();
            entry.put("song_word_indices", songWordsToInflectedTable.getIndices());

            Map<String, Object> masterKey = new HashMap<>();
            masterKey.put("inflected_hindi", songWordsToInflectedTable.getInflectedKey().getInflectedHindi());
            masterKey.put("inflected_hindi_index", songWordsToInflectedTable.getInflectedKey().getInflectedHindiIndex());

            entry.put("inflected_key", masterKey);

            List<SongWordsToInflectedTable> wordListMasterField = song.getWordListMaster();
            if (wordListMasterField == null){
                wordListMasterField = Lists.newArrayList();
                song.setWordListMaster(wordListMasterField);
            }
            wordListMasterField.add(WordListMasterEntryConverter.fromMap(entry));
            songRepository.save(song);
        }
    }

    @Inject
    private SongRepository songRepository;

    @Inject
    private SpellCheckRepository spellCheckRepository;

    @Inject
    private InflectedService inflectedService;



}
