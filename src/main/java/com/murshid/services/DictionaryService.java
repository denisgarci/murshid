package com.murshid.services;

import com.google.gson.Gson;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.MurshidEntry;
import com.murshid.persistence.domain.PlattsEntry;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.domain.WikitionaryEntry;
import com.murshid.persistence.domain.views.DictionaryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class DictionaryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryService.class);

    Gson gson = new Gson();

    /**
     * Creates the dictionaryEntries Javascript object
     *
     * @return a Map, transformable into a JSon object
     */
    public Map<String, DictionaryEntry> createDictionaryEntries(Song song) {
        Map<String, Object> masterEntries =  gson.fromJson(song.getMasterEntries(), Map.class);
        Set<String> canonicalKeys = masterEntries.entrySet().stream()
                .map(me -> (Map) me.getValue()).map(valMap -> (List<String>) valMap.get("canonical_keys"))
                .flatMap(l -> l.stream()).collect(Collectors.toSet());

        Map<String, DictionaryEntry> result = new HashMap<>();


        canonicalKeys.forEach(cks -> {

            String[] keyTokens = cks.split("_");
            DictionarySource dictionarySource = DictionarySource.valueOf(keyTokens[0]);
            int wordIndex = Integer.valueOf(keyTokens[keyTokens.length - 1]);
            String[] wordArrays = Arrays.copyOfRange(keyTokens, 1, keyTokens.length - 1);
            String word = String.join(" ", wordArrays);

            switch (dictionarySource) {
                case WIKITIONARY: {

                    Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(word, wordIndex);
                    if (wikitionaryEntry.isPresent()) {
                        WikitionaryEntry entry = wikitionaryEntry.get();

                        result.put(entry.getStringKey(), new DictionaryEntry()
                                .setDictionarySource(DictionarySource.WIKITIONARY)
                                .setHindiWord(entry.getHindiWord())
                                .setMeaning(entry.getMeaning())
                                .setPartOfSpeech(entry.getPartOfSpeech())
                                .setWordIndex(entry.getWordIndex()));
                    }
                }

                case PLATTS: {

                    Optional<PlattsEntry> prattsEntry = plattsService.findOne(word, wordIndex);
                    if (prattsEntry.isPresent()) {
                        PlattsEntry entry = prattsEntry.get();
                        result.put(entry.getStringKey(), new DictionaryEntry()
                                .setDictionarySource(DictionarySource.PLATTS)
                                .setHindiWord(entry.getHindiWord())
                                .setMeaning(entry.getMeaning())
                                .setPartOfSpeech(entry.getPartOfSpeech())
                                .setWordIndex(entry.getWordIndex()));
                    }
                }


                case REKHTA: {

                    Optional<RekhtaEntry> prattsEntry = rekhtaService.findOne(word, wordIndex);
                    if (prattsEntry.isPresent()) {
                        RekhtaEntry entry = prattsEntry.get();
                        result.put(entry.getStringKey(), new DictionaryEntry()
                                .setDictionarySource(DictionarySource.REKHTA)
                                .setHindiWord(entry.getHindiWord())
                                .setMeaning(entry.getMeaning())
                                .setPartOfSpeech(entry.getPartOfSpeech())
                                .setWordIndex(entry.getWordIndex()));
                    }
                }

                case MURSHID: {

                    Optional<MurshidEntry> gonzaloEntry = murshidService.findOne(word, wordIndex);
                    if (gonzaloEntry.isPresent()) {
                        MurshidEntry entry = gonzaloEntry.get();

                        result.put(entry.getStringKey(), new DictionaryEntry()
                                .setDictionarySource(DictionarySource.MURSHID)
                                .setHindiWord(entry.getHindiWord())
                                .setMeaning(entry.getMeaning())
                                .setPartOfSpeech(entry.getPartOfSpeech())
                                .setWordIndex(entry.getWordIndex()));
                    }
                    ;
                }
            }
        });

        song.setDictionaryEntries(gson.toJson(result));
        songRepository.save(song);

        return result;
    }

    @Inject
    private WikitionaryService wikitionaryService;

    @Inject
    private PlattsService plattsService;

    @Inject
    private MurshidService murshidService;

    @Inject
    private RekhtaService rekhtaService;

    @Inject
    private SongRepository songRepository;



}
