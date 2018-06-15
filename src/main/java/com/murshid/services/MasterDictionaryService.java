package com.murshid.services;

import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.persistence.repo.MasterDictionaryRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class MasterDictionaryService {

    private MasterDictionaryRepository masterDictionaryRepository;

    public List<MasterDictionary> findByHindiWord(String hindiWord){
        return masterDictionaryRepository.findByHindiWord(hindiWord);
    }

    public Optional<MasterDictionary> findOne(Integer key){
        MasterDictionary result = masterDictionaryRepository.findOne(key);
        return Optional.ofNullable(result);
    }

    public Optional<MasterDictionary> findByHindiWordAndWordIndex(String hindiWord, int wordIndex){
        MasterDictionary result = masterDictionaryRepository.findByHindiWordAndWordIndex(hindiWord, wordIndex);
        return Optional.ofNullable(result);
    }

    public MasterDictionary save(MasterDictionary masterDictionary){
        return masterDictionaryRepository.save(masterDictionary);
    }

    @Inject
    public void setMasterDictionaryRepository(MasterDictionaryRepository masterDictionaryRepository) {
        this.masterDictionaryRepository = masterDictionaryRepository;
    }


}
