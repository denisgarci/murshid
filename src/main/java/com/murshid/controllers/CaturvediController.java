package com.murshid.controllers;

import com.google.common.collect.Lists;
import com.murshid.persistence.domain.CaturvediEntry;
import com.murshid.persistence.domain.PlattsEntry;
import com.murshid.services.CaturvediService;
import com.murshid.services.PlattsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("caturvedi")
public class CaturvediController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaturvediController.class);
    private CaturvediService caturvediService;

    @GetMapping("/findAnywhere")
    public @ResponseBody
    List<CaturvediEntry> findAnywhere(@RequestParam(name = "word") String word) {
        return Lists.newArrayList(caturvediService.findAnywhere(word));
    }

    @PostMapping
    public ResponseEntity<String> persist(@RequestBody CaturvediEntry caturvediEntry) {
        if (isValid(caturvediEntry)) {
            caturvediService.save(caturvediEntry);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean isValid(CaturvediEntry caturvediEntry) {
        if (caturvediEntry.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (caturvediEntry.getDictionaryKey() == null) {
            LOGGER.info("dictionary key cannot be null");
            return false;
        } else {
            if (caturvediEntry.getDictionaryKey().hindiWord == null) {
                LOGGER.info("hindi word index cannot be null");
                return false;
            }
        }

        if (caturvediEntry.getMeaning() == null) {
            LOGGER.info("body cannot be null");
            return false;
        }

        return true;
    }

    @Inject
    public void setCaturvediService(CaturvediService caturvediService) {
        this.caturvediService = caturvediService;
    }
}
