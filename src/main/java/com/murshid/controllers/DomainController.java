package com.murshid.controllers;

import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("domain")
public class DomainController {

    /**
     * returns a map of NOUN => "Noun"
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/partsOfSpeech")
    public @ResponseBody
    Map<String, String> partsOfSpeech() {
        return Arrays.stream(PartOfSpeech.values()).collect(Collectors.toMap(PartOfSpeech::name, PartOfSpeech::getLabel));
    }

    /**
     * returns a map of MASCULINE => "Masculine"
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/accidenceTypes")
    public @ResponseBody
    Map<String, String> accidenceTypes() {
        return Arrays.stream(Accidence.values()).collect(Collectors.toMap(Accidence::name, Accidence::getLabel));
    }

}
