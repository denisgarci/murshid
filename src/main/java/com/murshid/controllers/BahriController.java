package com.murshid.controllers;

import com.google.common.collect.Lists;
import com.murshid.persistence.domain.BahriEntry;
import com.murshid.persistence.domain.CaturvediEntry;
import com.murshid.services.BahriService;
import com.murshid.services.CaturvediService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("bahri")
public class BahriController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BahriController.class);
    private BahriService caturvediService;

    @GetMapping("/findAnywhere")
    public @ResponseBody
    List<BahriEntry> findAnywhere(@RequestParam(name = "word") String word) {
        return Lists.newArrayList(caturvediService.findAnywhere(word));
    }

    @PostMapping
    public ResponseEntity<String> persist(@RequestBody BahriEntry bahriEntry) {
        if (isValid(bahriEntry)) {
            caturvediService.save(bahriEntry);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean isValid(BahriEntry bahriEntry) {
        if (bahriEntry.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (bahriEntry.getDictionaryKey() == null) {
            LOGGER.info("dictionary key cannot be null");
            return false;
        } else {
            if (bahriEntry.getDictionaryKey().hindiWord == null) {
                LOGGER.info("hindi word index cannot be null");
                return false;
            }
        }

        if (bahriEntry.getMeaning() == null) {
            LOGGER.info("body cannot be null");
            return false;
        }

        return true;
    }

    @Inject
    public void setBahriService(BahriService bahriService) {
        this.caturvediService = bahriService;
    }
}
