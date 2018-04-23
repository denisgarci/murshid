package com.murshid.controllers;

import com.murshid.dynamo.domain.Master;
import com.murshid.models.CanonicalKey;
import com.murshid.services.MasterService;
import com.murshid.services.SongsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("master")
public class MasterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterController.class);


    @GetMapping("/tokensNotInMaster")
    public @ResponseBody
    Set<String> tokensNotInMaster(@RequestParam(name = "songName") String songName) {
        return songsService.wordTokensNotInMaster(songName);
    }

    @GetMapping("/tokensNotInSpellChecker")
    public @ResponseBody
    Set<String> tokensNotInSpellChecker(@RequestParam(name = "songName") String songName) {
        return songsService.newWordsInSong(songName);
    }

    @GetMapping("/createMasterEntries")
    public @ResponseBody
    Map<String, Object> createMasterEntries(@RequestParam(name = "songName") String songName) {

       return  masterService.createMasterEntries(songName);
    }

    @GetMapping("/findWord")
    public @ResponseBody
    List findInKeyAndBody(@RequestParam(name = "hindiWord") String word) {
        List result = masterService.getByInflectedWord(word);
        return result;
    }

    @GetMapping("/findByCanonicalWord")
    public @ResponseBody
    List findInByCanonicalWord(@RequestParam(name = "canonicalWord") String canonicalWord) {
        List result = masterService.findByCanonicalWord(canonicalWord);
        return result;
    }

    @PostMapping("/insertNew")
    public ResponseEntity<String> insertNew(@RequestBody Master masterEntry) {
        complementCanonicalKeys(masterEntry);
        if (masterService.isValid(masterEntry)) {
            if (masterService.exists(masterEntry.getHindiWord(), masterEntry.getWordIndex())){
                LOGGER.info("canonicalWord {} index {} already exists in master", masterEntry.getHindiWord(), masterEntry.getWordIndex());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            boolean success = masterService.save(masterEntry);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/insertNewWithExplode")
    public ResponseEntity<String> insertNewWithExplode(@RequestBody Master masterEntry) {
        complementCanonicalKeys(masterEntry);
        if (!masterService.isValid(masterEntry)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<Master> exploded = masterService.explode(masterEntry);

        //first validate them all
        for (Master master: exploded) {
            if (masterService.isValid(master)) {
                if (masterService.exists(master.getHindiWord(), master.getWordIndex())) {
                    LOGGER.info("canonicalWord {} index {} already exists in master", master.getHindiWord(),
                                master.getWordIndex());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        //then write
        for (Master master: exploded) {
            boolean success = masterService.save(master);
            if (!success) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody Master masterEntry) {
        complementCanonicalKeys(masterEntry);
        if (masterService.isValid(masterEntry)) {
            boolean success = masterService.save(masterEntry);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private Master complementCanonicalKeys(Master master){
        for (CanonicalKey ck: master.getCanonicalKeys()){
            ck.setCanonicalWord(master.getCanonicalWord());
        }
        return master;
    }

    @Inject
    private MasterService masterService;

    @Inject
    private SongsService songsService;

}
