package com.murshid.services;

import com.google.common.collect.ImmutableMap;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.GonzaloEntry;
import com.murshid.persistence.domain.PrattsEntry;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.domain.WikitionaryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class DictionaryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryService.class);

    /**
     * Creates the dictionaryEntries Javscript object
     * @param songTitleLatin        the latin title of the Song under analysis
     * @return                      a Map, transformable into a JSon object
     */
    public Map<String, Object> createDictionaryEntries(String songTitleLatin){

        Map<String, Object> masterEntries = masterService.createMasterEntries(songTitleLatin);
        Set<String> canonicalKeys = masterEntries.entrySet().stream()
                .map(me -> (Map)me.getValue()).map(valMap -> (List<String>)valMap.get("canonical_keys"))
                .flatMap(l -> l.stream()).collect(Collectors.toSet());

        Map<String, Object> result = new HashMap<>();


        canonicalKeys.forEach(cks -> {

            String[] keyTokens = cks.split("_");
            DictionarySource dictionarySource = DictionarySource.valueOf(keyTokens[0]);
            int wordIndex = Integer.valueOf(keyTokens[keyTokens.length-1]);
            String[] wordArrays = Arrays.copyOfRange(keyTokens, 1, keyTokens.length-1 );
            String word = String.join(" ", wordArrays);

            DictionaryKey dictionaryKey = new DictionaryKey().setHindiWord(word).setWordIndex(wordIndex);

            switch (dictionarySource){
              case WIKITIONARY: {

                //Wikitionary
                Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(word, wordIndex);
                if (wikitionaryEntry.isPresent()){
                    WikitionaryEntry we = wikitionaryEntry.get();
                    String key = DictionarySource.WIKITIONARY.name()
                            .concat("_")
                            .concat(we.getHindiWord())
                            .concat("_")
                            .concat(Integer.toString(we.getWordIndex()));
                    Map<String, String> value = ImmutableMap.of("meaning", we.getMeaning(),
                                                                "hindiWord", we.getHindiWord(),
                                                                "dictionarySource",
                                                                DictionarySource.WIKITIONARY.name());
                    result.put(key, value);
                };
              }

              case PRATTS: {

                //Pratts
                Optional<PrattsEntry> prattsEntry = prattsService.findOne(word, wordIndex);
                if (prattsEntry.isPresent()){
                    PrattsEntry we = prattsEntry.get();
                    String key = DictionarySource.PRATTS.name()
                            .concat("_")
                            .concat(we.getHindiWord())
                            .concat("_")
                            .concat(Integer.toString(we.getWordIndex()));
                    Map<String, String> value = ImmutableMap.of("meaning", we.getMeaning(),
                                                                "hindiWord", we.getHindiWord(),
                                                                "dictionarySource", DictionarySource.PRATTS.name());
                    result.put(key, value);
                };
              }

              case REKHTA: {

                //Rekhta
                Optional<RekhtaEntry> prattsEntry = rekhtaService.findOne(word, wordIndex);
                if (prattsEntry.isPresent()){
                    RekhtaEntry we = prattsEntry.get();
                    String key = DictionarySource.REKHTA.name()
                            .concat("_")
                            .concat(we.getHindiWord())
                            .concat("_")
                            .concat(Integer.toString(we.getWordIndex()));
                    Map<String, String> value = ImmutableMap.of("meaning", we.getMeaning(),
                                                                "hindiWord", we.getHindiWord(),
                                                                "dictionarySource", DictionarySource.REKHTA.name());
                    result.put(key, value);
                };
              }

             case GONZALO: {

                 //Gonzalo
                 Optional<GonzaloEntry> gonzaloEntry = gonzaloService.findOne(word, wordIndex);
                 if (gonzaloEntry.isPresent()){
                   GonzaloEntry we = gonzaloEntry.get();

                   String key = DictionarySource.GONZALO.name()
                             .concat("_")
                             .concat(we.getHindiWord())
                             .concat("_")
                             .concat(Integer.toString(we.getWordIndex()));
                     Map<String, String> value = ImmutableMap.of("meaning", we.getMeaning(),
                                                                 "hindiWord", we.getHindiWord(),
                                                                 "dictionarySource", DictionarySource.GONZALO.name());
                     result.put(key, value);
                 };
             }
            }
       });

       return result;
    }

    @Inject
    private WikitionaryService wikitionaryService;

    @Inject
    private PrattsService prattsService;

    @Inject
    private GonzaloService gonzaloService;

    @Inject
    private RekhtaService rekhtaService;

    @Inject
    private MasterService masterService;



}
