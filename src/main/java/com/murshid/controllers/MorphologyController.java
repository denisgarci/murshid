package com.murshid.controllers;

import com.murshid.services.CanonicalsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("morphology")
public class MorphologyController {

    private CanonicalsService canonicalsService;

    @GetMapping("/suggestCanonicals")
    public @ResponseBody
    List findInKeyAndBody(@RequestParam(name = "hindiWord") String hindiWord) {
        return canonicalsService.suggestCanonicals(hindiWord);
    }

    @Inject
    public void setCanonicalsService(CanonicalsService canonicalsService) {
        this.canonicalsService = canonicalsService;
    }
}
