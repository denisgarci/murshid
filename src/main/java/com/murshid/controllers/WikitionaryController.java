package com.murshid.controllers;

import com.murshid.services.WikitionaryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

@Controller
public class WikitionaryController {

    @GetMapping("/processAllLetters")
    public String greeting(@RequestParam(name="retryFailed", required=false, defaultValue="false") boolean retryFailed) {
        wikitionaryService.processAllLetters(retryFailed);
        return "processing ...";
    }

    @Inject
    private WikitionaryService wikitionaryService;
}
