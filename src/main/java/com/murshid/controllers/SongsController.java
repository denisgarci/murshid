package com.murshid.controllers;

import com.murshid.dynamo.domain.Song;
import com.murshid.persistence.domain.views.SongWordsToInflectedTable;
import com.murshid.services.SongsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Optional;

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

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/findByLatinName")
    @ResponseBody
    public Song findByLatinName(@RequestParam(name = "songLatinName") String songLatinName) {
        Optional<Song> song = songsService.findByLatinTitle(songLatinName);
        return song.orElse(null);
    }

    @PostMapping("/addEntryToWordListMaster")
    public ResponseEntity<String> insertNew(@RequestParam(name = "songLatinName") String songLatinName, @RequestBody SongWordsToInflectedTable songWordsToInflectedTable) {
        if (!songsService.validate(songLatinName, songWordsToInflectedTable)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else {
            songsService.addEntryToWordListMaster(songLatinName, songWordsToInflectedTable);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @Inject
    private SongsService songsService;


}
