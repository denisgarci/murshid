package com.murshid.services;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.*;
import com.murshid.persistence.domain.views.CanonicalWrapper;
import com.murshid.persistence.domain.views.DictionaryEntryView;
import com.murshid.persistence.repo.DictionaryEntryRepository;
import com.murshid.persistence.repo.MasterDictionaryRepository;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class DictionaryService {

    private static Gson gson = new Gson();
    private static Type mapType = new TypeToken<LinkedTreeMap<String, LinkedTreeMap>>() {}.getType();

    private DictionaryRelationsService dictionaryRelationsService;
    private MasterDictionaryRepository masterDictionaryRepository;
    private DictionaryEntryRepository dictionaryEntryRepository;
    private WikitionaryService wikitionaryService;
    private PlattsService plattsService;
    private MurshidService murshidService;
    private RekhtaService rekhtaService;
    private SongRepository songRepository;

    /**
     * Creates the dictionaryEntries Javascript object, for the inflected entries
     *
     * @return a Map, transformable into a JSon object
     */
    public Map<String, Map<DictionarySource, List<DictionaryEntryView>>> createDictionaryEntriesForInflected(Song song) {

        //Inflected entries

        LinkedTreeMap< String, LinkedTreeMap> inflectedEntries =  gson.fromJson(song.getInflectedEntries(), mapType);

        Set<Pair<DictionaryKey, String>> pairs = inflectedEntries.entrySet().stream()
                .map(me -> {
                            LinkedTreeMap value = me.getValue();
                            if (value.containsKey("master_dictionary_key")){
                                LinkedTreeMap mdkMap = (LinkedTreeMap)value.get("master_dictionary_key");
                                return Pair.of(DictionaryKey.fromMap(mdkMap), (String)value.get("canonical_hindi"));
                            }else{
                                throw new IllegalArgumentException(String.format("the inflected entry %s does not have a master_dictionary_key", value));
                            }
                        }
                ).collect(Collectors.toSet());

        Map<String, Map<DictionarySource, List<DictionaryEntryView>>> dictionaryEntriesForInflected = createDictionaryEntriesMap(pairs);
        song.setDictionaryEntriesInflected(gson.toJson(dictionaryEntriesForInflected));

        songRepository.save(song);

        return dictionaryEntriesForInflected;
    }

    public Map<String, Map<DictionarySource, List<DictionaryEntryView>>> createDictionaryEntriesForNotInflected(Song song) {

        LinkedTreeMap< String, LinkedTreeMap> notInflectedEntries =  gson.fromJson(song.getNotInflectedEntries(), mapType);

        Set<Pair<DictionaryKey, String>> pairs = notInflectedEntries.entrySet().stream()
                .map(me -> {
                            LinkedTreeMap mdkMap = (LinkedTreeMap)me.getValue().get("master_dictionary_key");
                            return Pair.of(DictionaryKey.fromMap(mdkMap), "");
                        }
                ).collect(Collectors.toSet());

        Map<String, Map<DictionarySource, List<DictionaryEntryView>>> dictionaryEntriesForNotInflected = createDictionaryEntriesMap(pairs);
        song.setDictionaryEntriesNotInflected(gson.toJson(dictionaryEntriesForNotInflected));

        songRepository.save(song);

        return dictionaryEntriesForNotInflected;
    }


    private Map<String, Map<DictionarySource, List<DictionaryEntryView>>> createDictionaryEntriesMap(Set<Pair<DictionaryKey, String>> masterDictionaryKeys) {
        Map<String, Map<DictionarySource, List<DictionaryEntryView>>> result = new HashMap<>();
        masterDictionaryKeys.forEach(pair -> {
            DictionaryKey cks = pair.getLeft();
            String canonicalWord = pair.getRight();

            String mapKey = cks.hindiWord.concat("_").concat(Integer.toString(cks.wordIndex));

            MasterDictionary masterDictionary = masterDictionaryRepository.findByHindiWordAndWordIndex(cks.hindiWord, cks.wordIndex);
            List<DictionaryEntry> dictionaryEntries = dictionaryEntryRepository.findByMasterDictionary(masterDictionary);


            dictionaryEntries.forEach(de -> {
                result.computeIfAbsent(mapKey, (k) -> new HashMap<>());
                switch (de.getDictionarySource()) {
                    case WIKITIONARY: {
                        List<WikitionaryEntry> concreteEntries = Lists.newArrayList(toWikitionaryEntry(de, canonicalWord));
                        concreteEntries.addAll(dictionaryRelationsService.getRelatedDictionaryEntries(masterDictionary.getHindiWord(), masterDictionary.getWordIndex(), DictionarySource.WIKITIONARY)
                                .stream().map(die -> toWikitionaryEntry(die, die.masterDictionary.getHindiWord())).collect(Collectors.toList()));

                        List<DictionaryEntryView> dews = concreteEntries.stream().map(this::fromConcreteDictionaryEntry).collect(Collectors.toList());

                        result.get(mapKey).put(DictionarySource.WIKITIONARY, dews);
                    }
                    break;
                    case PLATTS: {
                        List<PlattsEntry> concreteEntries = Lists.newArrayList(toPlattsEntry(de, canonicalWord));
                        concreteEntries.addAll(dictionaryRelationsService.getRelatedDictionaryEntries(masterDictionary.getHindiWord(), masterDictionary.getWordIndex(), DictionarySource.PLATTS)
                                .stream().map(die -> toPlattsEntry(die, die.masterDictionary.getHindiWord())).collect(Collectors.toList()));

                        List<DictionaryEntryView> dews = concreteEntries.stream().map(this::fromConcreteDictionaryEntry).collect(Collectors.toList());

                        result.get(mapKey).put(DictionarySource.PLATTS, dews);
                    }
                    break;

                    case REKHTA: {
                        List<RekhtaEntry> concreteEntries = Lists.newArrayList(toRekhtaEntry(de, canonicalWord));
                        concreteEntries.addAll(dictionaryRelationsService.getRelatedDictionaryEntries(masterDictionary.getHindiWord(), masterDictionary.getWordIndex(), DictionarySource.REKHTA)
                                .stream().map(die -> toRekhtaEntry(die, die.masterDictionary.getHindiWord())).collect(Collectors.toList()));

                        List<DictionaryEntryView> dews = concreteEntries.stream().map(this::fromConcreteDictionaryEntry).collect(Collectors.toList());

                        result.get(mapKey).put(DictionarySource.REKHTA, dews);
                    }
                    break;

                    case MURSHID: {
                        List<MurshidEntry> concreteEntries = Lists.newArrayList(toMurshidEntry(de, canonicalWord));
                        concreteEntries.addAll(dictionaryRelationsService.getRelatedDictionaryEntries(masterDictionary.getHindiWord(), masterDictionary.getWordIndex(), DictionarySource.MURSHID)
                                .stream().map(die -> toMurshidEntry(die, die.masterDictionary.getHindiWord())).collect(Collectors.toList()));

                        List<DictionaryEntryView> dews = concreteEntries.stream().map(this::fromConcreteDictionaryEntry).collect(Collectors.toList());

                        result.get(mapKey).put(DictionarySource.MURSHID, dews);
                    }
                    break;
                }
            });
        });
        return result;
    }

    private WikitionaryEntry toWikitionaryEntry(DictionaryEntry dictionaryEntry, String canonicalWord){
        WikitionaryEntry entry = wikitionaryService.findOne(dictionaryEntry.masterDictionary.getHindiWord(), dictionaryEntry.getWordIndex())
                .orElseThrow(() ->
                        new RuntimeException(String.format("wikitionary dictionary entry not found for dictionaryEntry record %s-%s", dictionaryEntry.masterDictionary.getHindiWord(),dictionaryEntry.getWordIndex())));
        entry.setCanonicalHindi(canonicalWord);
        return entry;
    }

    private PlattsEntry toPlattsEntry(DictionaryEntry dictionaryEntry, String canonicalWord){
        PlattsEntry entry = plattsService.findOne(dictionaryEntry.masterDictionary.getHindiWord(), dictionaryEntry.getWordIndex())
                .orElseThrow(() ->
                        new RuntimeException(String.format("platts dictionary entry not found for dictionaryEntry record %s-%s", dictionaryEntry.masterDictionary.getHindiWord(),dictionaryEntry.getWordIndex())));
        entry.setCanonicalHindi(canonicalWord);
        return entry;
    }

    private RekhtaEntry toRekhtaEntry(DictionaryEntry dictionaryEntry, String canonicalWord){
        RekhtaEntry entry = rekhtaService.findOne(dictionaryEntry.masterDictionary.getHindiWord(), dictionaryEntry.getWordIndex())
                .orElseThrow(() ->
                        new RuntimeException(String.format("rekhta dictionary entry not found for dictionaryEntry record %s-%s", dictionaryEntry.masterDictionary.getHindiWord(),dictionaryEntry.getWordIndex())));
        entry.setCanonicalHindi(canonicalWord);
        return entry;
    }

    private MurshidEntry toMurshidEntry(DictionaryEntry dictionaryEntry, String canonicalWord){
        MurshidEntry entry = murshidService.findOne(dictionaryEntry.masterDictionary.getHindiWord(), dictionaryEntry.getWordIndex())
                .orElseThrow(() ->
                        new RuntimeException(String.format("rekhta dictionary entry not found for dictionaryEntry record %s-%s", dictionaryEntry.masterDictionary.getHindiWord(),dictionaryEntry.getWordIndex())));
        entry.setCanonicalHindi(canonicalWord);
        return entry;
    }



    private <T extends IDictionaryEntry> DictionaryEntryView fromConcreteDictionaryEntry(T concreteDictionaryEntry){

        return new DictionaryEntryView().setMeaning(concreteDictionaryEntry.getMeaning())
                .setHindiWord(concreteDictionaryEntry.getHindiWord())
                .setWordIndex(concreteDictionaryEntry.getWordIndex())
                .setDictionarySource(concreteDictionaryEntry.getDictionarySource())
                .setCanonicalHindi(concreteDictionaryEntry.getCanonicalHindi())
                .setPartOfSpeech(concreteDictionaryEntry.getPartOfSpeech());
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
    public void setDictionaryRelationsService(DictionaryRelationsService dictionaryRelationsService) {
        this.dictionaryRelationsService = dictionaryRelationsService;
    }

    @Inject
    public void setMasterDictionaryRepository(MasterDictionaryRepository masterDictionaryRepository) {
        this.masterDictionaryRepository = masterDictionaryRepository;
    }

    @Inject
    public void setDictionaryEntryRepository(DictionaryEntryRepository dictionaryEntryRepository) {
        this.dictionaryEntryRepository = dictionaryEntryRepository;
    }

    @Inject
    public void setWikitionaryService(WikitionaryService wikitionaryService) {
        this.wikitionaryService = wikitionaryService;
    }

    @Inject
    public void setPlattsService(PlattsService plattsService) {
        this.plattsService = plattsService;
    }

    @Inject
    public void setMurshidService(MurshidService murshidService) {
        this.murshidService = murshidService;
    }

    @Inject
    public void setRekhtaService(RekhtaService rekhtaService) {
        this.rekhtaService = rekhtaService;
    }

    @Inject
    public void setSongRepository(SongRepository songRepository) {
        this.songRepository = songRepository;
    }




}
