package com.murshid.services;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.repo.RekhtaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class RekhtaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RekhtaService.class);

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

    public boolean isValid(RekhtaEntry rekhtaEntry) {
        if (rekhtaEntry.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (rekhtaEntry.getDictionaryKey() == null) {
            LOGGER.info("dictionary key cannot be null");
            return false;
        } else {
            if (rekhtaEntry.getDictionaryKey().hindiWord == null) {
                LOGGER.info("dictionary entry key cannot be null");
                return false;
            }
        }

        if (rekhtaEntry.getUrduWord() == null) {
            LOGGER.info("urduWord cannot be null");
            return false;
        }

        if (rekhtaEntry.getMeaning() == null) {
            LOGGER.info("meaning cannot be null");
            return false;
        }

        return true;
    }

    @Inject
    private RekhtaRepository rekhtaRepository;
}
