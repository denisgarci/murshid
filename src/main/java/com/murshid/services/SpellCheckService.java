package com.murshid.services;

import com.murshid.persistence.domain.SpellCheckEntry;
import com.murshid.persistence.repo.SpellCheckRepository;
import com.murshid.utils.WordUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SpellCheckService {

    public SpellCheckEntry upsert(@Nonnull String hindiWord, @Nonnull String urduWord){
        SpellCheckEntry spellCheckEntry = spellCheckRepository.findOne(hindiWord);
        if (spellCheckEntry == null){
            spellCheckEntry = new SpellCheckEntry().setHindiWord(hindiWord).setUrduWord(urduWord).setActive(true).setInitial(hindiWord.charAt(0));
            spellCheckEntry = spellCheckRepository.save(spellCheckEntry);
        }
        return spellCheckEntry;
    }

    public boolean exists(String hindiWord){
        if (!spellCheckRepository.exists(hindiWord)){
            String anuReplaced = WordUtils.replaceAnusvaara(hindiWord);
            return spellCheckRepository.exists(anuReplaced);
        }else{
            return true;
        }
    }

    public String getUrduSpelling(String hindiWord){
        SpellCheckEntry spellCheckEntry = spellCheckRepository.findByHindiWord(hindiWord);
        if (spellCheckEntry!= null){
            return spellCheckEntry.getUrduWord();
        }else{
            return null;
        }
    }

    @Inject
    private SpellCheckRepository spellCheckRepository;
}
