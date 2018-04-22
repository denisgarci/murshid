package com.murshid.controllers;

import com.murshid.persistence.domain.views.CanonicalWrapper;
import com.murshid.persistence.domain.views.StringListWrapper;
import com.murshid.services.CanonicalsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("morphology")
public class MorphologyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MorphologyController.class);


    @GetMapping("/canonicals")
    public @ResponseBody
    List findInKeyAndBody(@RequestParam(name = "hindiWord") String hindiWord) {
        List result = canonicalsService.suggestCanonicals(hindiWord);
        return result;
    }

    @PostMapping("/canonicals2")
    public @ResponseBody
    Map<String, Map<String, String>> returnMap(@RequestBody StringListWrapper stringListWrapper) {

        Map<String, Map<String, String>> result = new HashMap<>();

        stringListWrapper.strings.forEach(string -> {
            List<CanonicalWrapper> canonicalWrappers = canonicalsService.findDictionaryEntries(string);
            Map<String, Map<String, String>> partial = canonicalWrappers.stream()
                    .collect(Collectors.toMap(CanonicalWrapper::getKey, a ->{
                        Map<String, String> body = new HashMap<>();
                        body.put("dictionarySource", a.getDictionarySource().name());
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
    private CanonicalsService canonicalsService;


}
