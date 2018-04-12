package com.murshid.services;

import com.murshid.persistence.domain.HindiWord;
import com.murshid.persistence.repo.HindiWordsRepository;
import com.murshid.utils.WordUtils;

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

    public boolean exists(String hindiWord){
        if (!hindiWordsRepository.exists(hindiWord)){
            String anuReplaced = WordUtils.replaceAnusvaara(hindiWord);
            return hindiWordsRepository.exists(anuReplaced);
        }else{
            return true;
        }
    }

    @Inject
    private HindiWordsRepository hindiWordsRepository;
}
