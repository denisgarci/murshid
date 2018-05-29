package com.murshid.controllers;

import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.NotInflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.models.CanonicalKey;
import com.murshid.services.NotInflectedService;
import com.murshid.services.SongsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;

@Controller
@RequestMapping("notinflected")
public class NotInflectedController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotInflectedController.class);

    @GetMapping("/findWord")
    public @ResponseBody
    List<NotInflected> findInKeyAndBody(@RequestParam(name = "hindiWord") String word) {
        List<NotInflected> result = notInflectedService.getByHindi(word);
        return result;
    }

    @GetMapping("/generateNotInflectedEntriesInSong")
    public @ResponseBody
    Map<String, Object> findAllInSongJs(@RequestParam(name = "songLatinName") String songLatinName) {
        Optional<Song> song = songsService.findByLatinTitle(songLatinName);
        if (song.isPresent()){
            return notInflectedService.generateNotInflectedEntries(song.get());
        }else{
            LOGGER.info("no song found with name={}", songLatinName);
            return Collections.emptyMap();
        }
    }

    @PostMapping("/insertNew")
    public ResponseEntity<String> insertNew(@RequestBody NotInflected inflected) {
        complementCanonicalKeys(inflected);
        inflected.setHindiIndex(notInflectedService.suggestNewIndex(inflected.getHindi()));
        if (notInflectedService.isValid(inflected)) {
            if (notInflectedService.exists(inflected.getHindi(), inflected.getHindiIndex())){
                LOGGER.info("inflected hindi word {} index {} already exists in inflected table in DynamoDB", inflected.getHindi(), inflected.getHindiIndex());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            boolean success = notInflectedService.save(inflected);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/upsert")
    public ResponseEntity<String> upsert(@RequestBody NotInflected inflected) {
        complementCanonicalKeys(inflected);
        if (notInflectedService.isValid(inflected)) {
            boolean success = notInflectedService.save(inflected);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private NotInflected complementCanonicalKeys(NotInflected notInflected){
        for (CanonicalKey ck: notInflected.getCanonicalKeys()){
            ck.setCanonicalWord(notInflected.getHindi());
        }
        return notInflected;
    }

    @Inject
    private NotInflectedService notInflectedService;

    @Inject
    private SongsService songsService;

}
