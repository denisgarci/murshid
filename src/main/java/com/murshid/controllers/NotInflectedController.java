package com.murshid.controllers;

import com.murshid.dynamo.domain.Song;
import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.persistence.domain.NotInflected;
import com.murshid.persistence.domain.views.NotInflectedView;
import com.murshid.services.MasterDictionaryService;
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

    private NotInflectedService notInflectedService;
    private MasterDictionaryService masterDictionaryService;
    private SongsService songsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotInflectedController.class);

    @GetMapping("/findWord")
    public @ResponseBody
    List<NotInflected> findInKeyAndBody(@RequestParam(name = "hindiWord") String word) {
        return notInflectedService.getByHindi(word);
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
    public ResponseEntity<String> insertNew(@RequestBody NotInflectedView notInflectedView) {
        //notInflectedView.se(notInflectedService.suggestNewIndex(notInflectedView.getHindi()));
        if (notInflectedService.isValid(notInflectedView)) {

            if (notInflectedService.exists(notInflectedView.getHindi(), notInflectedView.getInflectedHindiIndex())){
                LOGGER.info("inflected hindi word {} index {} already exists in inflected table in DynamoDB", notInflectedView.getHindi(), notInflectedView.getHindi());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Optional<MasterDictionary> masterDictionary = masterDictionaryService.findByHindiWordAndWordIndex(notInflectedView.getMasterDictionaryKey().hindiWord, notInflectedView.getMasterDictionaryKey().wordIndex);
            if (!masterDictionary.isPresent()){
                LOGGER.info("master dictionary word {} index {} does not exist", notInflectedView.getMasterDictionaryKey().hindiWord, notInflectedView.getMasterDictionaryKey().wordIndex);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            NotInflected notInflected = notInflectedService.fromView(notInflectedView, masterDictionary.get());

            boolean success = notInflectedService.save(notInflected);
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
    public ResponseEntity<String> upsert(@RequestBody NotInflectedView notInflectedView) {

        Optional<MasterDictionary> masterDictionary = masterDictionaryService.findByHindiWordAndWordIndex(notInflectedView.getMasterDictionaryKey().hindiWord, notInflectedView.getMasterDictionaryKey().wordIndex);
        if (!masterDictionary.isPresent()){
            LOGGER.info("master dictionary word {} index {} does not exist", notInflectedView.getMasterDictionaryKey().hindiWord, notInflectedView.getMasterDictionaryKey().wordIndex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (notInflectedService.isValid(notInflectedView)) {

            NotInflected notInflected = notInflectedService.fromView(notInflectedView, masterDictionary.get());

            boolean success = notInflectedService.save(notInflected);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Inject
    public void setNotInflectedService(NotInflectedService notInflectedService) {
        this.notInflectedService = notInflectedService;
    }

    @Inject
    public void setSongsService(SongsService songsService) {
        this.songsService = songsService;
    }

    @Inject
    public void setMasterDictionaryService(MasterDictionaryService masterDictionaryService) {
        this.masterDictionaryService = masterDictionaryService;
    }


}
