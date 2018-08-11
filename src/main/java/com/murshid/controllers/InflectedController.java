package com.murshid.controllers;

import com.google.common.collect.Lists;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.services.InflectedService;
import com.murshid.services.SongsService;
import com.murshid.services.SpellCheckService;
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
@SuppressWarnings("unused")
public class InflectedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InflectedController.class);

    private SongsService songsService;
    private InflectedService inflectedService;
    private SpellCheckService spellCheckService;

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

        spellCheckService.loadUrdus(Lists.newArrayList(inflected));

        if (!inflectedService.validateSpellCheckIngroup(Lists.newArrayList(inflected)).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (inflectedService.isValid(inflected)) {
            int masterDictionaryId = inflectedService.findMasterDictionaryId(inflected);
            inflected.setMasterDictionaryId(masterDictionaryId);

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

        spellCheckService.loadUrdus(Lists.newArrayList(infinitive));

        if (!inflectedService.isInfinitiveMasculineSingularDirect(infinitive)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!inflectedService.isValid(infinitive)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        int masterDictionaryId = inflectedService.findMasterDictionaryId(infinitive);
        infinitive.setMasterDictionaryId(masterDictionaryId);

        List<Inflected> existing = inflectedService.findByMasterDictionaryId(infinitive.getMasterDictionaryId());

        List<Inflected> explodedVerbs = inflectedService.explodeAllVerbs(infinitive);

        List<Inflected> remainder = InflectedService.subtractByAccidence(explodedVerbs, existing);

        if (!inflectedService.validateSpellCheckIngroupWithSupplement(remainder, masterDictionaryId)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

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


        int masterDictionaryId = inflectedService.findMasterDictionaryId(inflected);
        inflected.setMasterDictionaryId(masterDictionaryId);
        List<Inflected> exploded = inflectedService.explode(inflected);

        spellCheckService.loadUrdus(exploded);

        if (!inflectedService.validateSpellCheckIngroupWithSupplement(exploded, masterDictionaryId)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

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

        spellCheckService.loadUrdus(Lists.newArrayList(inflected));

        if (!inflectedService.validateSpellCheckIngroup(Lists.newArrayList(inflected)).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (inflectedService.isValid(inflected)) {
            int masterDictionaryId =  inflectedService.findMasterDictionaryId(inflected);
            inflected.setMasterDictionaryId(masterDictionaryId);
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

    @Inject
    public void setInflectedService(InflectedService inflectedService) {
        this.inflectedService = inflectedService;
    }

    @Inject
    public void setSongsService(SongsService songsService) {
        this.songsService = songsService;
    }

    @Inject
    public void setSpellCheckService(SpellCheckService spellCheckService) {
        this.spellCheckService = spellCheckService;
    }



}
