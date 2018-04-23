package com.murshid.controllers;

import com.murshid.persistence.domain.RekhtaEntry;
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
        if (rekhtaService.isValid(rekhtaEntry)) {
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



    @Inject
    private RekhtaService rekhtaService;

}
