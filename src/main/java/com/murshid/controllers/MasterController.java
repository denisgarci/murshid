package com.murshid.controllers;

import com.murshid.dynamo.domain.Master;
import com.murshid.services.HindiWordsService;
import com.murshid.services.MasterService;
import com.murshid.services.SongProcesspor;
import com.murshid.services.UrduWordsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("master")
public class MasterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterController.class);


    @GetMapping("/tokensNotInMaster")
    public @ResponseBody
    Set<String> tokensNotInMaster(@RequestParam(name = "songName") String songName) {
        return songProcesspor.wordTokensNotInMaster(songName);
    }

    @GetMapping("/findWord")
    public @ResponseBody
    List findInKeyAndBody(@RequestParam(name = "hindiWord") String word) {
        List result = masterService.getWords(word);
        return result;
    }

    @PostMapping("/insertNew")
    public ResponseEntity<String> insertNew(@RequestBody Master masterEntry) {
        if (isValid(masterEntry)) {
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

    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody Master masterEntry) {
        if (isValid(masterEntry)) {
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

    private boolean isValid(Master master) {
        if (master.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (master.getHindiWord() == null) {
            LOGGER.info("canonicalWord cannot be null");
            return false;
        }else  if (hindiWordsService.exists(master.getHindiWord())){
            LOGGER.info("the hindi word does not exists in hindi_words ");
            return false;
        }

        if (master.getUrduSpelling() == null) {
            LOGGER.info("urduWord spelling cannot be null");
            return false;
        }else if (urduWordsService.exists(master.getUrduSpelling())){
            LOGGER.info("the urdu spelling does not exists in urdu_words ");
            return false;
        }

        if (master.getPartOfSpeech() == null) {
            LOGGER.info("part of speech cannot be null");
            return false;
        }

        if (!masterService.validateCanonicalKeys(master)){
            LOGGER.info("some of the canonical keys are not present");
            return false;
        }

        if (!masterService.validateAccidence(master.getPartOfSpeech(), master.getAccidence())){
            LOGGER.info("inadequate accidence for the POS");
            return false;
        }

        return true;
    }

    @Inject
    private MasterService masterService;

    @Inject
    private SongProcesspor songProcesspor;

    @Inject
    private UrduWordsService urduWordsService;

    @Inject
    private HindiWordsService hindiWordsService;

}
