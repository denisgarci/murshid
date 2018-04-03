package com.murshid.controllers;

import com.google.common.collect.Lists;
import com.murshid.persistence.domain.PrattsEntry;
import com.murshid.services.PrattsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("pratts")
public class PrattsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrattsController.class);


    @GetMapping("/findAnywhere")
    public @ResponseBody
    List<PrattsEntry> findInKeyAndBody(@RequestParam(name = "word") String word) {
        List list = Lists.newArrayList(prattsService.findAnywhere(word));
        return list;
    }

    @PostMapping
    public ResponseEntity<String> persistPratts(@RequestBody PrattsEntry prattsEntry) {
        if (isValid(prattsEntry)) {
            prattsService.save(prattsEntry);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private boolean isValid(PrattsEntry prattsEntry) {
        if (prattsEntry.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (prattsEntry.getDictionaryKey() == null) {
            LOGGER.info("dictionary key cannot be null");
            return false;
        } else {
            if (prattsEntry.getDictionaryKey().word == null) {
                LOGGER.info("hindi word cannot be null");
                return false;
            }
        }

        if (prattsEntry.getUrduWord() == null) {
            LOGGER.info("urduWord cannot be null");
            return false;
        }

        if (prattsEntry.getBody() == null) {
            LOGGER.info("body cannot be null");
            return false;
        }

        return true;
    }

    @Inject
    private PrattsService prattsService;

}
