package com.murshid.controllers;

import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.services.InflectedService;
import com.murshid.services.MasterDictionaryService;
import com.murshid.services.SongsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;

@Controller
@RequestMapping("inflected")
public class InflectedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InflectedController.class);

    private MasterDictionaryService masterDictionaryService;
    private SongsService songsService;
    private InflectedService inflectedService;

    @GetMapping("/tokensNotInInflected")
    public @ResponseBody
    Set<String> tokensNotInMaster(@RequestParam(name = "songName") String songName) {
        return songsService.wordTokensNotInMaster(songName);
    }

    @GetMapping("/tokensNotInSpellChecker")
    public @ResponseBody
    Set<String> tokensNotInSpellChecker(@RequestParam(name = "songName") String songName) {
        return songsService.newWordsInSong(songName);
    }

    @GetMapping("/findWord")
    public @ResponseBody
    List<Inflected> findInKeyAndBody(@RequestParam(name = "hindiWord") String word) {
        return inflectedService.getByInflectedWord(word);
    }

    @GetMapping("/findByCanonicalWord")
    public @ResponseBody
    List findInByCanonicalWord(@RequestParam(name = "canonicalWord") String canonicalWord) {
        return inflectedService.findByCanonicalWord(canonicalWord);
    }

    @GetMapping("/generateInflectedEntriesInSong")
    public @ResponseBody
    Map<String, Object> findAllInSongJs(@RequestParam(name = "songLatinName") String songLatinName) {
        Optional<Song> song = songsService.findByLatinTitle(songLatinName);
        if (song.isPresent()){
            return inflectedService.generateInflectedEntries(song.get());
        }else{
            LOGGER.info("no song found with name={}", songLatinName);
            return Collections.emptyMap();
        }
    }

    @PostMapping("/insertNew")
    public ResponseEntity<String> insertNew(@RequestBody Inflected inflected) {
        inflected.setInflectedHindiIndex(inflectedService.suggestNewIndex(inflected.getInflectedHindi()));
        if (inflectedService.isValid(inflected)) {
            inflected = complementMasterDictionaryId(inflected);
            if (inflectedService.exists(inflected.getInflectedHindi(), inflected.getInflectedHindiIndex())){
                LOGGER.info("inflected hindi word {} index {} already exists in inflected table in DynamoDB", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            boolean success = inflectedService.save(inflected);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/insertAllVerbsWithExplode")
    public ResponseEntity<String> insertAllverbsWithExplode(@RequestBody Inflected infinitive) {

        if (!inflectedService.isInfinitiveMasculineSingularDirect(infinitive)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        //complementCanonicalKeys(infinitive);

        if (!inflectedService.isValid(infinitive)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        infinitive = complementMasterDictionaryId(infinitive);

        List<Inflected> explodedVerbs = inflectedService.explodeAllVerbs(infinitive);

        //first validate them all
        for (Inflected master: explodedVerbs) {
            if (!inflectedService.isValid(master)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        boolean wroteAll = inflectedService.writeSeveralWithSuggestedIndexes(explodedVerbs);
        if (!wroteAll) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/insertNewWithExplode")
    public ResponseEntity<String> insertNewWithExplode(@RequestBody Inflected inflected) {

        if (!inflectedService.isValid(inflected)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        inflected = complementMasterDictionaryId(inflected);
        List<Inflected> exploded = inflectedService.explode(inflected);

        //first validate them all
        for (Inflected master: exploded) {
            if (!inflectedService.isValid(master)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        //then write
        boolean wroteAll = inflectedService.writeSeveralWithSuggestedIndexes(exploded);
        if (!wroteAll) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody Inflected inflected) {

        if (inflectedService.isValid(inflected)) {
            inflected = complementMasterDictionaryId(inflected);
            boolean success = inflectedService.save(inflected);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private Inflected complementMasterDictionaryId(Inflected inflected){
        Optional<MasterDictionary> masterDictionary = masterDictionaryService.findByHindiWordAndWordIndex(inflected.getMasterDictionaryKey().hindiWord, inflected.getMasterDictionaryKey().wordIndex);
        if (masterDictionary.isPresent()){
            inflected.setMasterDictionaryId(masterDictionary.get().getId());
        }else{
            throw new RuntimeException(String.format("master dictionary entry not found for inflected %sलेना-%s", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex()));
        }
        return inflected;
    }

    @Inject
    public void setInflectedService(InflectedService inflectedService) {
        this.inflectedService = inflectedService;
    }

    @Inject
    public void setMasterDictionaryService(MasterDictionaryService masterDictionaryService) {
        this.masterDictionaryService = masterDictionaryService;
    }

    @Inject
    public void setSongsService(SongsService songsService) {
        this.songsService = songsService;
    }

}
