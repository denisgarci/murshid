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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class SongsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryLetterIngestor.class);
    private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";

    private SongRepository songRepository;
    private SpellCheckRepository spellCheckRepository;
    private InflectedService inflectedService;

    /**
     * Rerurns the texts of a song from the Dynamo songs table, if it exists.
     * @param songTitleLatin        the latin title of the desired song
     * @return                      the song's text
     */
    private Optional<String> getSong(@Nonnull String songTitleLatin) {

        Table table = DynamoAccessor.dynamoDB.getTable("songs");
        Item item = table.getItem("title_latin", songTitleLatin);

        if (item.isPresent("song")){
            return Optional.of(item.getString("song"));
        } else {
            return Optional.empty();
        }
    }

    /**
     * re-ingests the lyrics of a song (already present in the Dynamo Songs) from a file.
     */
    public boolean ingestEnglishTranslation(String songTitleLatin, String fileName){

        Song song = songRepository.findOne(songTitleLatin);

        if (song == null){
            return false;
        }else{

            try {
                final URL resource = getClass().getClassLoader()
                        .getResource("translations/" + fileName);
                if (resource != null) {
                    String translationText = new String(Files.readAllBytes(
                            Paths.get(resource.toURI())));
                    song.setEnglishTranslation(translationText);
                    songRepository.save(song);
                }else{
                    LOGGER.error("no song file {} found for song title {}", fileName, songTitleLatin);
                    return false;
                }

            }catch (URISyntaxException | IOException ex){
                return false;
            }

            return true;
        }
    }

    /**
     * re-ingests the lyrics of a song (already present in the Dynamo Songs) from a file.
     * It needs for the song file to be in the classpath (built)
     */
    public boolean ingestSong(String songTitleLatin, String fileName){

        Song song = songRepository.findOne(songTitleLatin);

        if (song == null){
            return false;
        }else{

            try {
                final URL resource = getClass().getClassLoader()
                        .getResource("songs/" + fileName);
                if (resource != null) {
                    String songText =  new String(Files.readAllBytes(
                            Paths.get(resource
                                    .toURI())));

                    song.setSong(songText);
                    songRepository.save(song);
                }else{
                    LOGGER.error("no song file {} found for song title {}", fileName, songTitleLatin);
                    return false;
                }
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
     */
    public Set<String> newWordsInSong(@Nonnull String songTitleLatin){
        Optional<String> song = getSong(songTitleLatin);
        if (song.isPresent()){
            Set<String> tokens = SongUtils.wordTokens(song.get());
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
            Set<String> tokens = SongUtils.wordTokens(song.get());
            for (String token: tokens){
                List<Inflected> inMaster =  inflectedService.getByInflectedWord(token);
                if (inMaster.isEmpty()){
                    result.add(token);
                }
            }
        }
        return result;
    }

    public void generateSpans(@Nonnull String songTitleLatin){
        Song song = songRepository.findOne(songTitleLatin);
        if (song != null){
            StringBuilder result = new StringBuilder();
            String songText = song.getSong();
            Set<String> hindiTokens = SongUtils.wordTokens(songText);

            String[] allTokens = songText.split(String.format(WITH_DELIMITER, "\\s+|\\\\n|\\?|\\,|\\[|\\]"));
            //allTokens = SongUtils.eliminateThingsWithinBrackets(allTokens);

            int index = 0;
            int tokenIndex = 0;
            boolean inside = false;
            while(tokenIndex < allTokens.length){
                String token = allTokens[tokenIndex];

                switch(token) {
                    case "[":
                        inside = true;
                        tokenIndex++;
                        result.append(token);
                        break;
                    case "]":
                        inside = false;
                        tokenIndex++;
                        result.append(token);
                        break;
                    default:
                        if (hindiTokens.contains(token)) {
                            if (!inside) {
                                index += 1;
                                result.append("<span class=\"relevant\" id=\"").append(index).append("\">").append( token).append("</span>");
                                result.append(" ");
                            } else {
                                result.append(token);
                            }
                        } else {
                            replaceSpacesAndBreaks(result, token);
                        }
                        tokenIndex++;
                }
                songRepository.save(song.setHtml(result.toString()));
            }
        }
    }

    private void replaceSpacesAndBreaks(StringBuilder result, String token) {
        switch (token){
            case " ":
                result.append("&nbsp;");
                break;
            case "<br/>":
                result.append("<br/>");
                break;
            default:
                result.append(token);
        }
    }

    public void generateEnglishTranslationSpans(@Nonnull String songTitleLatin){
        Song song = songRepository.findOne(songTitleLatin);
        if (song != null){
            StringBuilder result = new StringBuilder();
            String translationText = song.getEnglishTranslation();
            Set<String> hindiTokens = SongUtils.wordTokens(translationText);

            String[] allTokens = translationText.split(String.format(WITH_DELIMITER, "\\s+|\\\\n|\\?|\\,"));

            int index = 0;
            for(String token: allTokens){
                if (hindiTokens.contains(token)){
                    index += 1;
                    result.append("<span class=\"translation_word\" id=\"").append(index).append("\">").append(token).append("</span>");
                    result.append(" ");
                }else{
                    replaceSpacesAndBreaks(result, token);
                }
                songRepository.save(song.setEnglishTranslationHtml(result.toString()));
            }
        }
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

        Song song = songRepository.findOne(songTitleLatin);
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
    public SongsService setSongRepository(SongRepository songRepository) {
        this.songRepository = songRepository;
        return this;
    }

    @Inject
    public SongsService setSpellCheckRepository(SpellCheckRepository spellCheckRepository) {
        this.spellCheckRepository = spellCheckRepository;
        return this;
    }

    @Inject
    public SongsService setInflectedService(InflectedService inflectedService) {
        this.inflectedService = inflectedService;
        return this;
    }



}
