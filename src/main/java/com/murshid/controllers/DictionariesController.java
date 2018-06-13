package com.murshid.controllers;

import com.murshid.dynamo.domain.Song;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.views.CanonicalWrapper;
import com.murshid.persistence.domain.views.DictionaryEntryView;
import com.murshid.persistence.domain.views.StringListWrapper;
import com.murshid.services.DictionaryService;
import com.murshid.services.SongsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("dictionaries")
public class DictionariesController {

    private DictionaryService dictionaryService;
    private SongsService songsService;

    /**
     * presents the JS object that will be written in the corresponding Song.
     * It contains all dictionary entries necessary for the words in the song.
     *
     * It uses as a starting point the song's not-inflected entries
     *
     * As a side-effect, it recreates this Json object as a string in the song.
     *
     * @param songLatinName         e.g. "Alvida"
     * @return                      a Map of "key" -> List<DictionaryEntryView> apt to be transformed into Json
     */
    @GetMapping("/createDictionaryEntriesForNotInflected")
    public @ResponseBody
    Map<String, Map<DictionarySource, List<DictionaryEntryView>>> createDictionaryEntriesForNotInflected(@RequestParam(name = "songLatinName") String songLatinName) {
        Optional<Song> song = songsService.findByLatinTitle(songLatinName);
        return song.map( s -> dictionaryService.createDictionaryEntriesForNotInflected(s))
                .orElse(null);
    }

    /**
     * presents the JS object that will be written in the corresponding Song.
     * It contains all dictionary entries necessary for the words in the song.
     *
     * It uses as a starting point the song's inflected entries
     *
     * As a side-effect, it recreates this Json object as a string in the song.
     *
     * @param songLatinName         e.g. "Alvida"
     * @return                      a Map of "key" -> List<DictionaryEntryView> apt to be transformed into Json
     */
    @GetMapping("/createDictionaryEntriesForInflected")
    public @ResponseBody
    Map<String, Map<DictionarySource, List<DictionaryEntryView>>> createDictionaryEntriesForInflected(@RequestParam(name = "songLatinName") String songLatinName) {
        Optional<Song> song = songsService.findByLatinTitle(songLatinName);
        return song.map(dictionaryService::createDictionaryEntriesForInflected)
                .orElse(null);
    }

    @PostMapping("/findInDictionaries")
    public @ResponseBody
    Map<String, Map<String, String>> returnMap(@RequestBody StringListWrapper stringListWrapper) {

        Map<String, Map<String, String>> result = new HashMap<>();

        stringListWrapper.strings.forEach(string -> {
            List<CanonicalWrapper> canonicalWrappers = dictionaryService.findDictionaryEntries(string);
            Map<String, Map<String, String>> partial = canonicalWrappers.stream()
                    .collect(Collectors.toMap(CanonicalWrapper::getKey, a ->{
                        Map<String, String> body = new HashMap<>();
                        body.put("dictionarySourceTo", a.getDictionarySource().name());
                        body.put("meaning", a.getEntry().getMeaning());
                        //result.put("partOfSpeech", a.getEntry().getPartOfSpeech().name());
                        body.put("hindiWord", a.getEntry().getHindiWord());
                        return body;
                    }));
            result.putAll(partial);
        });
        return result;
    }

    @Inject
    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @Inject
    public void setSongsService(SongsService songsService) {
        this.songsService = songsService;
    }


}
