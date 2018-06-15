package com.murshid.services;

import com.murshid.persistence.domain.SpellCheckEntry;
import com.murshid.persistence.repo.SpellCheckRepository;
import com.murshid.utils.WordUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SpellCheckService {

    private SpellCheckRepository spellCheckRepository;

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

    public boolean wordsDontExist(String hindiWord){
        String[] tokens = hindiWord.split(" ");
        for (String hindi: tokens){
            if (!spellCheckRepository.exists(hindi)){
                return true;
            }
        }
        return false;
    }

    String passMultipleWordsToUrdu(String hindiWords){
        String[] tokens = hindiWords.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String hindi : tokens){
            sb.append(getUrduSpelling(hindi)).append(" ");
        }
        sb.delete(sb.length()-1, sb.length());
        return sb.toString();
    }


    private String getUrduSpelling(String hindiWord){
        SpellCheckEntry spellCheckEntry = spellCheckRepository.findByHindiWord(hindiWord);
        if (spellCheckEntry!= null){
            return spellCheckEntry.getUrduWord();
        }else{
            return null;
        }
    }

    @Inject
    public void setSpellCheckRepository(SpellCheckRepository spellCheckRepository) {
        this.spellCheckRepository = spellCheckRepository;
    }

}
