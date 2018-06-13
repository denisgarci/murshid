package com.murshid.controllers;

import com.murshid.ingestor.wikitionary.WikitionaryCaller;
import com.murshid.persistence.domain.WikitionaryEntry;
import com.murshid.services.WikitionaryService;
import com.murshid.services.WikitionaryWordProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Controller
@RequestMapping("wikitionary")
public class WikitionaryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryController.class);

    private WikitionaryService wikitionaryService;
    private WikitionaryWordProcessor wikitionaryWordProcessor;

    @GetMapping("/processAllLetters")
    public String greeting(@RequestParam(name="retryFailed", required=false, defaultValue="false") boolean retryFailed) {
        wikitionaryService.processAllLetters(retryFailed);
        return "processing ...";
    }

    @GetMapping("/processWord")
    public String processWord(@RequestParam(name = "hindiWord") String hindiWord) {
        WikitionaryCaller wikitionaryCaller = new WikitionaryCaller();
        wikitionaryWordProcessor.processWord(wikitionaryCaller, hindiWord);
        LOGGER.info("received hindiWordIndex " + hindiWord);
        return "processWord/" + hindiWord;
    }

    @PostMapping
    public ResponseEntity<String> persist(@RequestBody WikitionaryEntry wikitionaryEntry) {
        if (isValid(wikitionaryEntry)) {
            wikitionaryService.save(wikitionaryEntry);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean isValid(WikitionaryEntry wikitionaryEntry) {
        if (wikitionaryEntry.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (wikitionaryEntry.getDictionaryKey() == null) {
            LOGGER.info("dictionary key cannot be null");
            return false;
        } else {
            if (wikitionaryEntry.getDictionaryKey().hindiWord == null) {
                LOGGER.info("dictionary entry key hindiWord cannot be null");
                return false;
            }
        }

        if (wikitionaryEntry.getMeaning() == null) {
            LOGGER.info("meaning cannot be null");
            return false;
        }

        return true;
    }

    @Inject
    public void setWikitionaryService(WikitionaryService wikitionaryService) {
        this.wikitionaryService = wikitionaryService;
    }

    @Inject
    public void setWikitionaryWordProcessor(WikitionaryWordProcessor wikitionaryWordProcessor) {
        this.wikitionaryWordProcessor = wikitionaryWordProcessor;
    }
}
