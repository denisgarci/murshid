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
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("platts")
public class PlattsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlattsController.class);


    @GetMapping("/findAnywhere")
    public @ResponseBody
    List<PlattsEntry> findAnywhere(@RequestParam(name = "word") String word) {
        List list = Lists.newArrayList(plattsService.findAnywhere(word));
        return list;
    }

    //@GetMapping("/allSpaces")
    public @ResponseBody
    void allSpaces() {
        Iterable<PlattsEntry> iterable = plattsService.findAll();
        Iterator<PlattsEntry> iterator = iterable.iterator();
        while(iterator.hasNext()){
            PlattsEntry pe = iterator.next();
            pe.setMeaning(pe.getMeaning().replace("\n", " ").replace("\r", " "));
            if (pe.getExtraMeaning() != null) {
                pe.setExtraMeaning(pe.getExtraMeaning().replace("\n", " ").replace("\r", " "));
            }
            plattsService.save(pe);
        }
    }


    @GetMapping("/replaceNukta")
    public @ResponseBody
    List<PlattsEntry> replaceNukta() {
        return plattsService.replaceNuktas();
    }

    @PostMapping
    public ResponseEntity<String> persistPlatts(@RequestBody PlattsEntry plattsEntry) {
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
                LOGGER.info("hindi hindiWordIndex cannot be null");
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
