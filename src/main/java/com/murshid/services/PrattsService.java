package com.murshid.services;

import com.murshid.persistence.domain.PrattsEntry;
import com.murshid.persistence.repo.PrattsRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class PrattsService {

    public List<PrattsEntry> findAnywhere(String word){
        List<PrattsEntry> inHindiOrUrdu = prattsRepository.findByHindiWordOrUrduWord(word, word);
        if (inHindiOrUrdu.isEmpty()){
            String searchString = "%" + word + "%";
            return prattsRepository.findByBodyLikeOrKeystringLike(searchString, searchString);
        }else{
            return inHindiOrUrdu;
        }
    }

    public PrattsEntry save(PrattsEntry prattsEntry){
        prattsEntry.setBody(prattsEntry.getBody().replace("\n", ""));
        return prattsRepository.save(prattsEntry);
    }

    @Inject
    private PrattsRepository prattsRepository;
}
