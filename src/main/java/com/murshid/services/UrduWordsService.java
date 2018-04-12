package com.murshid.services;

import com.murshid.persistence.domain.UrduWord;
import com.murshid.persistence.repo.UrduWordsRepository;
import com.murshid.utils.WordUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;

@Named
public class UrduWordsService {

    public UrduWord upsert(@Nonnull String urduWord){
        UrduWord hindiWordEntry = urduWordsRepository.findOne(urduWord);
        if (hindiWordEntry == null){
            hindiWordEntry = new UrduWord().setWord(urduWord).setActive(true).setInitial(urduWord.charAt(0));
            hindiWordEntry = urduWordsRepository.save(hindiWordEntry);
        }
        return hindiWordEntry;
    }

    public boolean exists(String urduWord){
        Set<String> allHehs = WordUtils.explodeHehs(urduWord);
        for (String uw: allHehs){
            if (urduWordsRepository.exists(uw)){
                return true;
            }
        }
        return false;
    }

    @Inject
    private UrduWordsRepository urduWordsRepository;
}
