package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.persistence.domain.MurshidEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MasterDictionaryRepository extends CrudRepository<MasterDictionary, Integer> {

    List<MasterDictionary> findByHindiWord(String hindiWord);

    MasterDictionary findByHindiWordAndWordIndex(String hindiWord, int wordIndex);

}
