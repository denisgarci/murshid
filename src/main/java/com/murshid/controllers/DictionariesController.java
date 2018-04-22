package com.murshid.controllers;

import com.murshid.services.DictionaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Map;

@Controller
@RequestMapping("dictionaries")
public class DictionariesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictionariesController.class);

    @GetMapping("/createDictionaryEntries")
    public @ResponseBody
    Map<String, Object> createDictionaryEntries(@RequestParam(name = "songName") String songName) {
        return dictionaryService.createDictionaryEntries(songName);
    }

    @Inject
    private DictionaryService dictionaryService;


}
