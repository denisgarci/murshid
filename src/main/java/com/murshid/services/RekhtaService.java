package com.murshid.services;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.repo.RekhtaRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class RekhtaService {

    public RekhtaEntry save(RekhtaEntry rekhtaEntry){
        return rekhtaRepository.save(rekhtaEntry);
    }

    public Optional<RekhtaEntry> findOne(DictionaryKey key){
        RekhtaEntry rekhtaEntry = rekhtaRepository.findOne(key);
        return Optional.ofNullable(rekhtaEntry);
    }

    public boolean exists(DictionaryKey key){
        return findOne(key).isPresent();
    }

    public boolean exists(String hindiWord, int index){
        DictionaryKey dictionaryKey = new DictionaryKey().setHindiWord(hindiWord).setWordIndex(index);
        return exists(dictionaryKey);
    }

    public List<RekhtaEntry> findByHindiWord(String hindiWord){
        return rekhtaRepository.findByDictionaryKeyHindiWord(hindiWord);
    }

    public Optional<RekhtaEntry> findOne(String hindiWord, int index){
        DictionaryKey dictionaryKey = new DictionaryKey().setHindiWord(hindiWord).setWordIndex(index);
        return findOne(dictionaryKey);
    }

    @Inject
    private RekhtaRepository rekhtaRepository;
}
