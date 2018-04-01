package com.murshid.controllers;

import com.murshid.services.MasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("master")
public class MasterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterController.class);


        @GetMapping("/findWord")
        public @ResponseBody List findInKeyAndBody(@RequestParam(name="word") String word) {
            List result =  masterService.getWords(word);
            return result;
        }





    @Inject
    private MasterService masterService;



}
