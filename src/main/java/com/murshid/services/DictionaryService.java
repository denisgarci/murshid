package com.murshid.services;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.MurshidEntry;
import com.murshid.persistence.domain.PlattsEntry;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.domain.WikitionaryEntry;
import com.murshid.persistence.domain.views.CanonicalWrapper;
import com.murshid.persistence.domain.views.DictionaryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class DictionaryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryService.class);

    Gson gson = new Gson();

    /**
     * Creates the dictionaryEntries Javascript object, for the inflected entries
     *
     * @return a Map, transformable into a JSon object
     */
    public Map<String, List<DictionaryEntry>> createDictionaryEntriesForInflected(Song song) {

        //Inflected entries
        Map<String, Object> inflectedEntries =  gson.fromJson(song.getInflectedEntries(), Map.class);
        Set<String> canonicalKeysFromInflected = inflectedEntries.entrySet().stream()
                .map(me -> (Map) me.getValue()).map(valMap -> (List<String>) valMap.get("canonical_keys"))
                .flatMap(l -> l.stream()).collect(Collectors.toSet());

        Map<String, List<DictionaryEntry>> dictionaryEntriesForInflected = createDictionaryEntriesMap(canonicalKeysFromInflected);
        addDictionaryRelations(dictionaryEntriesForInflected);
        song.setDictionaryEntriesInflected(gson.toJson(dictionaryEntriesForInflected));

        songRepository.save(song);

        return dictionaryEntriesForInflected;
    }

    /**
     * increases each list on the value side of the incoming map
     * with related dictionary elements, if any available in dicitonary_relations.
     * @param dictionaryEntries
     */
    public void addDictionaryRelations(Map<String, List<DictionaryEntry>> dictionaryEntries){
        dictionaryEntries.values().forEach(va -> {
            DictionaryEntry origin = va.get(0);
            List<DictionaryEntry> additional = dictionaryRelationsService.find(origin);
            va.addAll(additional);
        });
    }

    public Map<String, List<DictionaryEntry>> createDictionaryEntriesForNotInflected(Song song) {

        Map<String, Object> notInflectedEntries =  gson.fromJson(song.getNotInflectedEntries(), Map.class);
        Set<String> canonicalKeysFromNotInflected = notInflectedEntries.entrySet().stream()
                .map(me -> (Map) me.getValue()).map(valMap -> (List<String>) valMap.get("canonical_keys"))
                .flatMap(l -> l.stream()).collect(Collectors.toSet());

        Map<String, List<DictionaryEntry>> dictionaryEntriesForNotInflected = createDictionaryEntriesMap(canonicalKeysFromNotInflected);
        addDictionaryRelations(dictionaryEntriesForNotInflected);
        song.setDictionaryEntriesNotInflected(gson.toJson(dictionaryEntriesForNotInflected));

        songRepository.save(song);

        return dictionaryEntriesForNotInflected;
    }


    /**
     * Creates the base map of key => dictionary entries (list values are unary, without the references)
     * @param canonicalKeys
     * @return
     */
    private Map<String, List<DictionaryEntry>> createDictionaryEntriesMap(Set<String> canonicalKeys){
        Map<String, List<DictionaryEntry>> dictionaryEntries = new HashMap<>();
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

                        dictionaryEntries.put(entry.getStringKey(), Lists.newArrayList(new DictionaryEntry()
                                .setDictionarySource(DictionarySource.WIKITIONARY)
                                .setHindiWord(entry.getHindiWord())
                                .setMeaning(entry.getMeaning())
                                .setPartOfSpeech(entry.getPartOfSpeech())
                                .setWordIndex(entry.getWordIndex())));
                    }
                }
                break;

                case PLATTS: {

                    Optional<PlattsEntry> prattsEntry = plattsService.findOne(word, wordIndex);
                    if (prattsEntry.isPresent()) {
                        PlattsEntry entry = prattsEntry.get();
                        dictionaryEntries.put(entry.getStringKey(), Lists.newArrayList(new DictionaryEntry()
                                .setDictionarySource(DictionarySource.PLATTS)
                                .setHindiWord(entry.getHindiWord())
                                .setMeaning(entry.getMeaning())
                                .setPartOfSpeech(entry.getPartOfSpeech())
                                .setWordIndex(entry.getWordIndex())));
                    }
                }
                break;


                case REKHTA: {

                    Optional<RekhtaEntry> prattsEntry = rekhtaService.findOne(word, wordIndex);
                    if (prattsEntry.isPresent()) {
                        RekhtaEntry entry = prattsEntry.get();
                        dictionaryEntries.put(entry.getStringKey(), Lists.newArrayList(new DictionaryEntry()
                                .setDictionarySource(DictionarySource.REKHTA)
                                .setHindiWord(entry.getHindiWord())
                                .setMeaning(entry.getMeaning())
                                .setPartOfSpeech(entry.getPartOfSpeech())
                                .setWordIndex(entry.getWordIndex())));
                    }
                }
                break;

                case MURSHID: {

                    Optional<MurshidEntry> gonzaloEntry = murshidService.findOne(word, wordIndex);
                    if (gonzaloEntry.isPresent()) {
                        MurshidEntry entry = gonzaloEntry.get();

                        dictionaryEntries.put(entry.getStringKey(), Lists.newArrayList(new DictionaryEntry()
                                .setDictionarySource(DictionarySource.MURSHID)
                                .setHindiWord(entry.getHindiWord())
                                .setMeaning(entry.getMeaning())
                                .setPartOfSpeech(entry.getPartOfSpeech())
                                .setWordIndex(entry.getWordIndex())));
                    }

                }
                break;
            }
        });

        return dictionaryEntries;
    }

    public List<CanonicalWrapper> findDictionaryEntries(@Nonnull String hindiWord){

        List<CanonicalWrapper> result = new ArrayList<>();
        result.addAll( wikitionaryService.findByHindiWord(hindiWord).stream().map(we -> new CanonicalWrapper(DictionarySource.WIKITIONARY, we)).collect(Collectors.toList()));
        result.addAll(plattsService.findByHindiWord(hindiWord).stream().map(we -> new CanonicalWrapper(DictionarySource.PLATTS, we)).collect(Collectors.toList()));
        result.addAll( rekhtaService.findByHindiWord(hindiWord).stream().map(we -> new CanonicalWrapper(DictionarySource.REKHTA, we)).collect(Collectors.toList()));
        result.addAll(murshidService.findByHindiWord(hindiWord).stream().map(we -> new CanonicalWrapper(DictionarySource.MURSHID, we)).collect(Collectors.toList()));

        return result;
    }

    @Inject
    private DictionaryRelationsService dictionaryRelationsService;


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
