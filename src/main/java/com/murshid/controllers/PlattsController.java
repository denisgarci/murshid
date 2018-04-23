package com.murshid.controllers;

import com.google.common.collect.Lists;
import com.murshid.persistence.domain.PlattsEntry;
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
@RequestMapping("platts")
public class PlattsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlattsController.class);


    @GetMapping("/findAnywhere")
    public @ResponseBody
    List<PlattsEntry> findInKeyAndBody(@RequestParam(name = "word") String word) {
        List list = Lists.newArrayList(plattsService.findAnywhere(word));
        return list;
    }

    @PostMapping
    public ResponseEntity<String> persistPratts(@RequestBody PlattsEntry plattsEntry) {
        if (isValid(plattsEntry)) {
            plattsService.save(plattsEntry);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean isValid(PlattsEntry plattsEntry) {
        if (plattsEntry.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (plattsEntry.getDictionaryKey() == null) {
            LOGGER.info("dictionary key cannot be null");
            return false;
        } else {
            if (plattsEntry.getDictionaryKey().hindiWord == null) {
                LOGGER.info("hindi canonicalWord cannot be null");
                return false;
            }
        }

        if (plattsEntry.getUrduWord() == null) {
            LOGGER.info("urduWord cannot be null");
            return false;
        }

        if (plattsEntry.getMeaning() == null) {
            LOGGER.info("body cannot be null");
            return false;
        }

        return true;
    }

    @Inject
    private PlattsService plattsService;

}
