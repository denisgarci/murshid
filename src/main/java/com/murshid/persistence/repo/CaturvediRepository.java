package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.CaturvediEntry;
import com.murshid.persistence.domain.MurshidEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CaturvediRepository extends CrudRepository<CaturvediEntry, DictionaryKey> {

    List<CaturvediEntry> findByDictionaryKey(DictionaryKey dictionaryKey);

    List<CaturvediEntry> findByDictionaryKeyHindiWord(String hindiWord);

    List<CaturvediEntry> findByMeaningLikeOrExtraMeaningLike(String meaningString, String extraMeaningString);
}
