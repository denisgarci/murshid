package com.murshid.controllers;

import com.murshid.dynamo.domain.Song;
import com.murshid.persistence.domain.views.SongWordsToInflectedTable;
import com.murshid.persistence.domain.views.SongWordsToNotInflectedTable;
import com.murshid.services.SongsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Optional;

@Controller
@RequestMapping("songs")
public class SongsController {

    private SongsService songsService;

    @PostMapping("/reingest")
    public ResponseEntity<String> insertNew(@RequestParam(name = "songLatinName") String songLatinName, @RequestParam(name = "songFile") String songFile) {

        boolean result = songsService.ingestSong(songLatinName, songFile);
        if (result){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/ingestEnglishTranslation")
    public ResponseEntity<String> ingestEnglishTranslation(@RequestParam(name = "songLatinName") String songLatinName, @RequestParam(name = "songFile") String songFile) {

        boolean result = songsService.ingestEnglishTranslation(songLatinName, songFile);
        if (result){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/sortBySpanId")
    public ResponseEntity<String> sortBySpanId(@RequestParam(name = "songLatinName") String songLatinName) {
        songsService.sortBySpanId(songLatinName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/resequenceInflected")
    public ResponseEntity<String> resequenceInflected(@RequestParam(name = "songLatinName") String songLatinName) {
        songsService.resequenceSongWordsToInflected(songLatinName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/generateSpans")
    public ResponseEntity<String> generateSoans(@RequestParam(name = "songLatinName") String songLatinName) {
        songsService.generateSpans(songLatinName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/generateEnglishTranslationSpans")
    public ResponseEntity<String> generateEnglishTranslationSpans(@RequestParam(name = "songLatinName") String songLatinName) {
        songsService.generateEnglishTranslationSpans(songLatinName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/findByLatinName")
    @ResponseBody
    public Song findByLatinName(@RequestParam(name = "songLatinName") String songLatinName) {
        Optional<Song> song = songsService.findByLatinTitle(songLatinName);
        return song.orElse(null);
    }

    @PostMapping("/appendToInflected")
    public ResponseEntity<String> appendToINnflected(@RequestParam(name = "songLatinName") String songLatinName, @RequestBody SongWordsToInflectedTable songWordsToInflectedTable) {
        if (!songsService.validate(songLatinName, songWordsToInflectedTable)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else {
            songsService.appendToInflected(songLatinName, songWordsToInflectedTable);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @PostMapping("/appendToNotInflected")
    public ResponseEntity<String> appendToNotInflected(@RequestParam(name = "songLatinName") String songLatinName, @RequestBody SongWordsToNotInflectedTable notInflectedToAppend) {
        if (!songsService.validate(songLatinName, notInflectedToAppend)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else {
            songsService.appendNotInflected(songLatinName, notInflectedToAppend);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @Inject
    public void setSongsService(SongsService songsService) {
        this.songsService = songsService;
    }

}
