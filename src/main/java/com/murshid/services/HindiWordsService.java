package com.murshid.services;

import com.murshid.mysql.domain.HindiWord;
import com.murshid.mysql.repo.HindiWordsRepository;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class HindiWordsService {

    public HindiWord upsert(@Nonnull String hindiWord){
        HindiWord hindiWordEntry = hindiWordsRepository.findOne(hindiWord);
        if (hindiWordEntry == null){
            hindiWordEntry = new HindiWord().setWord(hindiWord).setActive(true).setInitial(hindiWord.charAt(0));
            hindiWordEntry = hindiWordsRepository.save(hindiWordEntry);
        }
        return hindiWordEntry;
    }

    @Inject
    private HindiWordsRepository hindiWordsRepository;
}
