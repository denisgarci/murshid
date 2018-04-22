package com.murshid.controllers;

import com.murshid.persistence.domain.views.WordListMasterEntry;
import com.murshid.services.SongsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

@Controller
@RequestMapping("songs")
public class SongsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SongsController.class);

    @PostMapping("/reingest")
    public ResponseEntity<String> insertNew(@RequestParam(name = "songLatinName") String songLatinName, @RequestParam(name = "songFile") String songFile) {

        boolean result = songsService.ingestSong(songLatinName, songFile);
        if (result){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/addEntryToWordListMaster")
    public ResponseEntity<String> insertNew(@RequestParam(name = "songLatinName") String songLatinName, @RequestBody WordListMasterEntry wordListMasterEntry) {
        if (!songsService.validate(songLatinName, wordListMasterEntry)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else {
            songsService.addEntryToWordListMaster(songLatinName, wordListMasterEntry);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @Inject
    private SongsService songsService;


}
