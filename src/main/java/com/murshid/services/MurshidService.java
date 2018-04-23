package com.murshid.services;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.MurshidEntry;
import com.murshid.persistence.repo.MurshidRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class MurshidService {

    public MurshidEntry save(MurshidEntry murshidEntry){
        return murshidRepository.save(murshidEntry);
    }

    public boolean exists(DictionaryKey key){
        return findOne(key).isPresent();
    }

    public boolean exists(String hindiWord, int wordIndex){
        return exists(new DictionaryKey().setHindiWord(hindiWord).setWordIndex(wordIndex));
    }

    public List<MurshidEntry> findByHindiWord(String hindiWord){
        return murshidRepository.findByDictionaryKeyHindiWord(hindiWord);
    }

    public Optional<MurshidEntry> findOne(DictionaryKey key){
        List<MurshidEntry> result = murshidRepository.findByDictionaryKey(key);
        if (result.size() > 1){
            throw new RuntimeException("unexpected size of results by DictionaryKey in Murshid");
        }else if (result.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(result.get(0));
        }
    }

    public Optional<MurshidEntry> findOne(String hindiWord, int index){
        DictionaryKey dictionaryKey = new DictionaryKey().setHindiWord(hindiWord).setWordIndex(index);
        return findOne(dictionaryKey);
    }

    @Inject
    private MurshidRepository murshidRepository;
}
