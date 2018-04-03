package com.murshid.services;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.GonzaloEntry;
import com.murshid.persistence.repo.GonzaloRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class GonzaloService {

    public GonzaloEntry save(GonzaloEntry prattsEntry){
        return gonzaloRepository.save(prattsEntry);
    }

    public boolean exists(DictionaryKey key){
        return findOne(key).isPresent();
    }

    public boolean exists(String hindiWord, int wordIndex){
        return exists(new DictionaryKey().setWord(hindiWord).setWordIndex(wordIndex));
    }

    public Optional<GonzaloEntry> findOne(DictionaryKey key){
        List<GonzaloEntry> result = gonzaloRepository.findByDictionaryKey(key);
        if (result.size() > 0){
            throw new RuntimeException("unexpected size of results by DictionaryKey in Gonzalo");
        }else if (result.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(result.get(0));
        }
    }

    @Inject
    private GonzaloRepository gonzaloRepository;
}
