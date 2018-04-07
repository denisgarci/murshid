package com.murshid.services;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.PrattsEntry;
import com.murshid.persistence.repo.PrattsRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class PrattsService {

    public List<PrattsEntry> findAnywhere(String word){
        List<PrattsEntry> inHindiOrUrdu = prattsRepository.findByDictionaryKeyWordOrUrduWord(word, word);
        if (inHindiOrUrdu.isEmpty()){
            String searchString = "%" + word + "%";
            return prattsRepository.findByBodyLikeOrKeystringLike(searchString, searchString);
        }else{
            return inHindiOrUrdu;
        }
    }

    public List<PrattsEntry> findByHindiWord(String hindiWord){
        return prattsRepository.findByDictionaryKeyWord(hindiWord);
    }

    public PrattsEntry save(PrattsEntry prattsEntry){
        prattsEntry.setBody(prattsEntry.getBody().replace("\n", ""));
        return prattsRepository.save(prattsEntry);
    }

    public boolean exists(DictionaryKey key){
        return findOne(key).isPresent();
    }

    public boolean exists(String hindiWord, int wordIndex){
        return exists(new DictionaryKey().setWord(hindiWord).setWordIndex(wordIndex));
    }

    public Optional<PrattsEntry> findOne(DictionaryKey key){
        List<PrattsEntry> result = prattsRepository.findByDictionaryKey(key);
        if (result.size() > 1){
            throw new RuntimeException("unexpected size of results (" + result.size() + ") by DictionaryKey in Pratts hindiWOrd=" + key.word + " wordIndex=" +  key.wordIndex);
        }else if (result.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(result.get(0));
        }
    }

    @Inject
    private PrattsRepository prattsRepository;
}
