package com.murshid.controllers;

import com.murshid.dynamo.domain.Master;
import com.murshid.services.MasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("master")
public class MasterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterController.class);


    @GetMapping("/findWord")
    public @ResponseBody
    List findInKeyAndBody(@RequestParam(name = "word") String word) {
        List result = masterService.getWords(word);
        return result;
    }

    @PostMapping
    public ResponseEntity<String> persistPratts(@RequestBody Master masterEntry) {
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
            LOGGER.info("hindiWord cannot be null");
            return false;
        }

        if (master.getUrduSpelling() == null) {
            LOGGER.info("urduWord spelling cannot be null");
            return false;
        }

        if (master.getPartOfSpeech() == null) {
            LOGGER.info("part of speech cannot be null");
            return false;
        }

        return true;
    }

    @Inject
    private MasterService masterService;


}
