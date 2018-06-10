package com.murshid.services;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.*;
import com.murshid.persistence.domain.views.CanonicalWrapper;
import com.murshid.persistence.domain.views.DictionaryEntryView;
import com.murshid.persistence.repo.DictionaryEntryRepository;
import com.murshid.persistence.repo.MasterDictionaryRepository;
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
    public Map<String, Map<DictionarySource, List<DictionaryEntryView>>> createDictionaryEntriesForInflected(Song song) {

        //Inflected entries
        LinkedTreeMap< String, LinkedTreeMap> inflectedEntries =  gson.fromJson(song.getInflectedEntries(), LinkedTreeMap.class);

        Set<DictionaryKey> masterDictionaryKeys = inflectedEntries.entrySet().stream()
                .map(me -> {
                      LinkedTreeMap mdkMap = (LinkedTreeMap)me.getValue().get("master_dictionary_key");
                      return DictionaryKey.fromMap(mdkMap);
                    }
                ).collect(Collectors.toSet());

        Map<String, Map<DictionarySource, List<DictionaryEntryView>>> dictionaryEntriesForInflected = createDictionaryEntriesMap2(masterDictionaryKeys);
        //addDictionaryRelations(dictionaryEntriesForInflected);
        song.setDictionaryEntriesInflected(gson.toJson(dictionaryEntriesForInflected));

        songRepository.save(song);

        return dictionaryEntriesForInflected;
    }

    Set<DictionaryKey> masterDictionaryKeys(LinkedTreeMap masterDictionaryKey){
        String mHindiWord = masterDictionaryKey.get("hindi_word").toString();
        int mWordIndex = Double.valueOf((Double)masterDictionaryKey.get("word_index")).intValue();
        MasterDictionary masterDictionary = masterDictionaryRepository.findByHindiWordAndWordIndex(mHindiWord, mWordIndex);
        List<DictionaryEntry> dictionaryEntries = dictionaryEntryRepository.findByMasterDictionary(masterDictionary);
        return dictionaryEntries.stream().map(DictionaryEntry::getMasterDictionary).map(md -> {
            DictionaryKey dk = new DictionaryKey();
            dk.setWordIndex(md.getWordIndex()).setHindiWord(md.getHindiWord());
            return dk;
        }).collect(Collectors.toSet());
    }

    /**
     * increases each list on the value side of the incoming map
     * with related dictionary elements, if any available in dictionary_relations.
     * @param dictionaryEntries
     */
    public void addDictionaryRelations(Map<String, List<DictionaryEntryView>> dictionaryEntries){
        dictionaryEntries.values().forEach(va -> {
            DictionaryEntryView origin = va.get(0);
            List<DictionaryEntryView> additional = dictionaryRelationsService.find(origin);
            va.addAll(additional);
        });
    }

    public Map<String, Map<DictionarySource, List<DictionaryEntryView>>> createDictionaryEntriesForNotInflected(Song song) {

        LinkedTreeMap< String, LinkedTreeMap> notInflectedEntries =  gson.fromJson(song.getNotInflectedEntries(), LinkedTreeMap.class);

        Set<DictionaryKey> masterDictionaryKeys = notInflectedEntries.entrySet().stream()
                .map(me -> {
                            LinkedTreeMap mdkMap = (LinkedTreeMap)me.getValue().get("master_dictionary_key");
                            return DictionaryKey.fromMap(mdkMap);
                        }
                ).collect(Collectors.toSet());

        Map<String, Map<DictionarySource, List<DictionaryEntryView>>> dictionaryEntriesForNotInflected = createDictionaryEntriesMap2(masterDictionaryKeys);
        //addDictionaryRelations(dictionaryEntriesForNotInflected);
        song.setDictionaryEntriesNotInflected(gson.toJson(dictionaryEntriesForNotInflected));

        songRepository.save(song);

        return dictionaryEntriesForNotInflected;
    }


    /**
     * Creates the base map of key => dictionary entries (list values are unary, without the references)
     *
     * @param masterDictionaryKeys
     * @return
     */
    private Map<String, Map<DictionarySource, List<DictionaryEntryView>>> createDictionaryEntriesMap2(Set<DictionaryKey> masterDictionaryKeys) {
        Map<String, Map<DictionarySource, List<DictionaryEntryView>>> result = new HashMap<>();
        masterDictionaryKeys.forEach(cks -> {

            String mapKey = cks.hindiWord.concat("_").concat(Integer.toString(cks.wordIndex));

            MasterDictionary masterDictionary = masterDictionaryRepository.findByHindiWordAndWordIndex(cks.hindiWord, cks.wordIndex);
            List<DictionaryEntry> dictionaryEntries1 = dictionaryEntryRepository.findByMasterDictionary(masterDictionary);

            dictionaryEntries1.forEach(de -> {
                switch (de.getDictionarySource()) {
                    case WIKITIONARY: {
                        Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(de.masterDictionary.getHindiWord(), de.getWordIndex());
                        if (wikitionaryEntry.isPresent()) {
                            WikitionaryEntry entry = wikitionaryEntry.get();

                            DictionaryEntryView dew = new DictionaryEntryView()
                                    .setDictionarySource(DictionarySource.WIKITIONARY)
                                    .setHindiWord(entry.getHindiWord())
                                    .setMeaning(entry.getMeaning())
                                    .setPartOfSpeech(entry.getPartOfSpeech())
                                    .setWordIndex(entry.getWordIndex());

                            if(result.get(mapKey) == null){
                                result.put(mapKey, new HashMap<>());
                            }

                            result.get(mapKey).put(DictionarySource.WIKITIONARY, Lists.newArrayList(dew));

                        }
                    }
                    break;
                    case PLATTS: {

                        Optional<PlattsEntry> prattsEntry = plattsService.findOne(de.masterDictionary.getHindiWord(), de.getWordIndex());
                        if (prattsEntry.isPresent()) {
                            PlattsEntry entry = prattsEntry.get();

                            DictionaryEntryView dew = new DictionaryEntryView()
                                    .setDictionarySource(DictionarySource.PLATTS)
                                    .setHindiWord(entry.getHindiWord())
                                    .setMeaning(entry.getMeaning())
                                    .setPartOfSpeech(entry.getPartOfSpeech())
                                    .setWordIndex(entry.getWordIndex());

                            if(result.get(mapKey) == null){
                                result.put(mapKey, new HashMap<>());
                            }

                            result.get(mapKey).put(DictionarySource.PLATTS, Lists.newArrayList(dew));

                        }
                    }
                    break;

                    case REKHTA: {

                        Optional<RekhtaEntry> prattsEntry = rekhtaService.findOne(de.masterDictionary.getHindiWord(), de.getWordIndex());
                        if (prattsEntry.isPresent()) {
                            RekhtaEntry entry = prattsEntry.get();

                            DictionaryEntryView dew = new DictionaryEntryView()
                                    .setDictionarySource(DictionarySource.REKHTA)
                                    .setHindiWord(entry.getHindiWord())
                                    .setMeaning(entry.getMeaning())
                                    .setPartOfSpeech(entry.getPartOfSpeech())
                                    .setWordIndex(entry.getWordIndex());

                            if(result.get(mapKey) == null){
                                result.put(mapKey, new HashMap<>());
                            }

                            result.get(mapKey).put(DictionarySource.REKHTA, Lists.newArrayList(dew));

                        }
                    }
                    break;

                    case MURSHID: {

                        Optional<MurshidEntry> gonzaloEntry = murshidService.findOne(de.masterDictionary.getHindiWord(), de.getWordIndex());
                        if (gonzaloEntry.isPresent()) {
                            MurshidEntry entry = gonzaloEntry.get();

                            DictionaryEntryView dew = new DictionaryEntryView()
                                    .setDictionarySource(DictionarySource.MURSHID)
                                    .setHindiWord(entry.getHindiWord())
                                    .setMeaning(entry.getMeaning())
                                    .setPartOfSpeech(entry.getPartOfSpeech())
                                    .setWordIndex(entry.getWordIndex());

                            if(result.get(mapKey) == null){
                                result.put(mapKey, new HashMap<>());
                            }

                            result.get(mapKey).put(DictionarySource.MURSHID, Lists.newArrayList(dew));

                        }

                    }
                    break;
                }
            });
        });
        return result;
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
    private MasterDictionaryRepository masterDictionaryRepository;

    @Inject
    private DictionaryEntryRepository dictionaryEntryRepository;


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
