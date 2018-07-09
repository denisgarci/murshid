package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.BahriEntry;
import com.murshid.persistence.domain.CaturvediEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BahriRepository extends CrudRepository<BahriEntry, DictionaryKey> {

    List<BahriEntry> findByDictionaryKey(DictionaryKey dictionaryKey);

    List<BahriEntry> findByDictionaryKeyHindiWord(String hindiWord);

    List<BahriEntry> findByMeaningLikeOrExtraMeaningLike(String meaningString, String extraMeaningString);
}
