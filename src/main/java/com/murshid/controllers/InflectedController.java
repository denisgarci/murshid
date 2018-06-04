package com.murshid.controllers;

import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.models.CanonicalKey;
import com.murshid.services.InflectedService;
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
        List<Inflected> result = inflectedService.getByInflectedWord(word);
        return result;
    }

    @GetMapping("/findByCanonicalWord")
    public @ResponseBody
    List findInByCanonicalWord(@RequestParam(name = "hindiWordIndex") String canonicalWord) {
        List result = inflectedService.findByCanonicalWord(canonicalWord);
        return result;
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
        complementCanonicalKeys(inflected);
        inflected.setInflectedHindiIndex(inflectedService.suggestNewIndex(inflected.getInflectedHindi()));
        if (inflectedService.isValid(inflected)) {
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
    public ResponseEntity<String> insertAllverbsWithExplode(@RequestBody Inflected inflected) {

        if (!inflectedService.isInfinitiveMasculineSingularDirect(inflected)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        complementCanonicalKeys(inflected);

        if (!inflectedService.isValid(inflected)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<Inflected> explodedVerbs = inflectedService.explodeAllVerbs(inflected);

        //first validate them all
        for (Inflected master: explodedVerbs) {
            if (!inflectedService.isValid(master)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        //then check if any of them is already in inflected
        boolean someExist = inflectedService.duplicatesInflected(inflected.getCanonicalHindi(), explodedVerbs);
        if (someExist){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean wroteAll = inflectedService.writeSeveralWithSuggestedIndexes(explodedVerbs);
        if (!wroteAll) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/insertNewWithExplode")
    public ResponseEntity<String> insertNewWithExplode(@RequestBody Inflected inflected) {
        complementCanonicalKeys(inflected);
        if (!inflectedService.isValid(inflected)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

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
        complementCanonicalKeys(inflected);
        if (inflectedService.isValid(inflected)) {
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

    private Inflected complementCanonicalKeys(Inflected inflected){
        for (CanonicalKey ck: inflected.getCanonicalKeys()){
            ck.setCanonicalWord(inflected.getCanonicalHindi());
        }
        return inflected;
    }

    @Inject
    private InflectedService inflectedService;

    @Inject
    private SongsService songsService;

}
