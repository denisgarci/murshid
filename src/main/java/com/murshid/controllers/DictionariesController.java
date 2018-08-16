package com.murshid.controllers;

import com.google.common.collect.Lists;
import com.murshid.dynamo.domain.Song;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.DictionaryEntry;
import com.murshid.persistence.domain.Inflected;
import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.persistence.domain.views.*;
import com.murshid.persistence.repo.DictionaryEntryRepository;
import com.murshid.persistence.repo.MasterDictionaryRepository;
import com.murshid.services.DictionaryService;
import com.murshid.services.MasterDictionaryService;
import com.murshid.services.SongsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("dictionaries")
public class DictionariesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaturvediController.class);

    private DictionaryService dictionaryService;
    private SongsService songsService;
    private MasterDictionaryService masterDictionaryService;
    private DictionaryEntryRepository dictionaryEntryRepository;

    /**
     * presents the JS object that will be written in the corresponding Song.
     * It contains all dictionary entries necessary for the words in the song.
     *
     * It uses as a starting point the song's not-inflected entries
     *
     * As a side-effect, it recreates this Json object as a string in the song.
     *
     * @param songLatinName         e.g. "Alvida"
     * @return                      a Map of "key" -> List<DictionaryEntryView> apt to be transformed into Json
     */
    @GetMapping("/createDictionaryEntriesForNotInflected")
    public @ResponseBody
    Map<String, Map<DictionarySource, List<DictionaryEntryView>>> createDictionaryEntriesForNotInflected(@RequestParam(name = "songLatinName") String songLatinName) {
        Optional<Song> song = songsService.findByLatinTitle(songLatinName);
        return song.map( s -> dictionaryService.createDictionaryEntriesForNotInflected(s))
                .orElse(null);
    }

    /**
     * presents the JS object that will be written in the corresponding Song.
     * It contains all dictionary entries necessary for the words in the song.
     *
     * It uses as a starting point the song's inflected entries
     *
     * As a side-effect, it recreates this Json object as a string in the song.
     *
     * @param songLatinName         e.g. "Alvida"
     * @return                      a Map of "key" -> List<DictionaryEntryView> apt to be transformed into Json
     */
    @GetMapping("/createDictionaryEntriesForInflected")
    public @ResponseBody
    Map<String, Map<DictionarySource, List<DictionaryEntryView>>> createDictionaryEntriesForInflected(@RequestParam(name = "songLatinName") String songLatinName) {
        Optional<Song> song = songsService.findByLatinTitle(songLatinName);
        return song.map(dictionaryService::createDictionaryEntriesForInflected)
                .orElse(null);
    }

    @PostMapping("/masterDictionaryAndDictionaryEntries")
    public ResponseEntity<String> insertMasterDictionary(@RequestBody MasterDictionaryView masterDictionaryView) {

        if (masterDictionaryService.findByHindiWordAndWordIndex(masterDictionaryView.getHindi(), masterDictionaryView.getIndex()).isPresent()){
            LOGGER.info("a combination of hindi word ={} and index={} already exist in master_dictionary", masterDictionaryView.getHindi(), masterDictionaryView.getIndex());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (masterDictionaryService.persistMasterDictionaryAndEntries(masterDictionaryView)){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/findInDictionaries")
    public @ResponseBody
    Map<String, CanonicalWrapper> returnMap(@RequestParam(name = "hindiWord") String hindiWord) {

        Map<String, String> result = new HashMap<>();


            List<CanonicalWrapper> canonicalWrappers = dictionaryService.findDictionaryEntries(hindiWord);
            Map<String, CanonicalWrapper> partial = canonicalWrappers.stream()
                    .collect(Collectors.toMap(CanonicalWrapper::getKey, Function.identity()));
            return partial;
    }

    @Inject
    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @Inject
    public DictionariesController setMasterDictionaryService(MasterDictionaryService masterDictionaryService) {
        this.masterDictionaryService = masterDictionaryService;
        return this;
    }

    @Inject
    public void setSongsService(SongsService songsService) {
        this.songsService = songsService;
    }


}
