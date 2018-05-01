package com.murshid.controllers;

import com.murshid.dynamo.domain.Song;
import com.murshid.persistence.domain.views.DictionaryEntry;
import com.murshid.services.DictionaryService;
import com.murshid.services.SongsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("dictionaries")
public class DictionariesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictionariesController.class);

    /**
     * presents the JS object that will be written in the corresponding Song.
     * It contains all dictionary entries necessary for the words in the song.
     *
     * It uses as a starting point the song's masterEntries
     *
     * As a side-effect, it recreates this Json object as a string in the song.
     *
     * @param songLatinName         e.g. "Alvida"
     * @return                      a Map of "key" -> DictionaryEntry apt to be transformed into Json
     */
    @GetMapping("/createDictionaryEntries")
    public @ResponseBody
    Map<String, DictionaryEntry> createDictionaryEntries(@RequestParam(name = "songLatinName") String songLatinName) {
        Optional<Song> song = songsService.findByLatinTitle(songLatinName);
        if (song.isPresent()) {
            return dictionaryService.createDictionaryEntries(song.get());
        }else{
            return null;
        }
    }

    @Inject
    private DictionaryService dictionaryService;

    @Inject
    private SongsService songsService;

}
