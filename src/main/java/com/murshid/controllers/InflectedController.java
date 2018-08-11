package com.murshid.controllers;

import com.google.common.collect.Lists;
import com.murshid.dynamo.domain.Song;
import com.murshid.persistence.domain.Inflected;
import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.persistence.domain.views.InflectedView;
import com.murshid.services.InflectedService;
import com.murshid.services.MasterDictionaryService;
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
    private MasterDictionaryService masterDictionaryService;


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
    public ResponseEntity<String> insertNew(@RequestBody InflectedView inflectedView) {

        if (!inflectedService.validateSpellCheckIngroup(Lists.newArrayList(inflectedView)).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        spellCheckService.loadUrdus(Lists.newArrayList(inflectedView));

        Optional<MasterDictionary> masterDictionary = masterDictionaryService.findByHindiWordAndWordIndex(inflectedView.getMasterDictionaryKey().hindiWord, inflectedView.getMasterDictionaryKey().wordIndex);
        if (!masterDictionary.isPresent()){
            LOGGER.info("master dictionary word {} index {} does not exist", inflectedView.getMasterDictionaryKey().hindiWord, inflectedView.getMasterDictionaryKey().wordIndex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (inflectedService.isValid(inflectedView)) {
            Inflected inflected = inflectedService.fromView(inflectedView, masterDictionary.get());

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
    public ResponseEntity<String> insertAllverbsWithExplode(@RequestBody InflectedView inflectedView) {

        spellCheckService.loadUrdus(Lists.newArrayList(inflectedView));

        if (!inflectedService.isInfinitiveMasculineSingularDirect(inflectedView)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!inflectedService.isValid(inflectedView)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<MasterDictionary> masterDictionary = masterDictionaryService.findByHindiWordAndWordIndex(inflectedView.getMasterDictionaryKey().hindiWord, inflectedView.getMasterDictionaryKey().wordIndex);
        if (!masterDictionary.isPresent()){
            LOGGER.info("master dictionary word {} index {} does not exist", inflectedView.getMasterDictionaryKey().hindiWord, inflectedView.getMasterDictionaryKey().wordIndex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<Inflected> existing = inflectedService.findByMasterDictionaryId(masterDictionary.get().getId());

        Inflected infinitive = inflectedService.fromView(inflectedView, masterDictionary.get());

        List<Inflected> explodedVerbs = inflectedService.explodeAllVerbs(infinitive);

        List<Inflected> remainder = InflectedService.subtractByAccidence(explodedVerbs, existing);

        if (!inflectedService.validateSpellCheckIngroupWithSupplement(remainder, masterDictionary.get().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        //first validate them all
        for (Inflected master: explodedVerbs) {
            if (!inflectedService.isValid(inflectedView)) {
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
    public ResponseEntity<String> insertNewWithExplode(@RequestBody InflectedView inflectedView) {

        if (!inflectedService.isValid(inflectedView)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }


        Optional<MasterDictionary> masterDictionary = masterDictionaryService.findByHindiWordAndWordIndex(inflectedView.getMasterDictionaryKey().hindiWord, inflectedView.getMasterDictionaryKey().wordIndex);
        if (!masterDictionary.isPresent()){
            LOGGER.info("master dictionary word {} index {} does not exist", inflectedView.getMasterDictionaryKey().hindiWord, inflectedView.getMasterDictionaryKey().wordIndex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Inflected inflected = inflectedService.fromView(inflectedView, masterDictionary.get());

        List<Inflected> exploded = inflectedService.explode(inflected);

        spellCheckService.loadUrdus(exploded);

        if (!inflectedService.validateSpellCheckIngroupWithSupplement(exploded, masterDictionary.get().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        //first validate them all
        for (Inflected master: exploded) {
            if (!inflectedService.isValid(inflectedView)) {
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
    public ResponseEntity<String> upsert(@RequestBody InflectedView inflectedView) {

        spellCheckService.loadUrdus(Lists.newArrayList(inflectedView));

        if (!inflectedService.validateSpellCheckIngroup(Lists.newArrayList(inflectedView)).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (inflectedService.isValid(inflectedView)) {

            Optional<MasterDictionary> masterDictionary = masterDictionaryService.findByHindiWordAndWordIndex(inflectedView.getMasterDictionaryKey().hindiWord, inflectedView.getMasterDictionaryKey().wordIndex);
            if (!masterDictionary.isPresent()){
                LOGGER.info("master dictionary word {} index {} does not exist", inflectedView.getMasterDictionaryKey().hindiWord, inflectedView.getMasterDictionaryKey().wordIndex);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Inflected inflected = inflectedService.fromView(inflectedView, masterDictionary.get());
            inflected.getInflectedKey().setInflectedHindiIndex(inflectedView.getInflectedHindiIndex());

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

    @Inject
    public InflectedController setMasterDictionaryService(MasterDictionaryService masterDictionaryService) {
        this.masterDictionaryService = masterDictionaryService;
        return this;
    }



}
