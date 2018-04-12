package com.murshid.controllers;

import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.repo.RekhtaRepository;
import com.murshid.services.RekhtaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("rekhta")
public class RekhtaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RekhtaController.class);

    @PostMapping
    public ResponseEntity<String> persist(@RequestBody RekhtaEntry rekhtaEntry) {
        if (isValid(rekhtaEntry)) {
            rekhtaService.save(rekhtaEntry);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/findByHindiWord")
    public @ResponseBody
    List<RekhtaEntry> findByWord(@RequestParam(name="hindiWord") String hindiWord) {
        return rekhtaService.findByHindiWord(hindiWord);
    }

    private boolean isValid(RekhtaEntry prattsEntry) {
        if (prattsEntry.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (prattsEntry.getDictionaryKey() == null) {
            LOGGER.info("dictionary key cannot be null");
            return false;
        } else {
            if (prattsEntry.getDictionaryKey().hindiWord == null) {
                LOGGER.info("dictionary entry key cannot be null");
                return false;
            }
        }

        if (prattsEntry.getUrduWord() == null) {
            LOGGER.info("urduWord cannot be null");
            return false;
        }

        if (prattsEntry.getMeaning() == null) {
            LOGGER.info("meaning cannot be null");
            return false;
        }

        return true;
    }

    @Inject
    private RekhtaService rekhtaService;

    @Inject
    private RekhtaRepository rekhtaRepository;

}
