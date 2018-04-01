package com.murshid.controllers;

import com.murshid.ingestor.wikitionary.WikitionaryCaller;
import com.murshid.services.WikitionaryService;
import com.murshid.services.WikitionaryWordProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

@Controller
@RequestMapping("wikitionary")
public class WikitionaryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryController.class);

    @GetMapping("/processAllLetters")
    public String greeting(@RequestParam(name="retryFailed", required=false, defaultValue="false") boolean retryFailed) {
        wikitionaryService.processAllLetters(retryFailed);
        return "processing ...";
    }

    @GetMapping("/processWord/{hindiWord}")
    public String processWord(@PathVariable(name="hindiWord", required=true) String hindiWord) {
        WikitionaryCaller wikitionaryCaller = new WikitionaryCaller();
        wikitionaryWordProcessor.processWord(wikitionaryCaller, hindiWord);
        LOGGER.info("received word " + hindiWord);
        return "processWord/" + hindiWord;
    }

    @Inject
    private WikitionaryService wikitionaryService;

    @Inject
    private WikitionaryWordProcessor wikitionaryWordProcessor;

}
