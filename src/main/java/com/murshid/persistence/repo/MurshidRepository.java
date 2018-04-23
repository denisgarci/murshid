package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.MurshidEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MurshidRepository extends CrudRepository<MurshidEntry, DictionaryKey> {

    List<MurshidEntry> findByDictionaryKey(DictionaryKey dictionaryKey);

    List<MurshidEntry> findByDictionaryKeyHindiWord(String hindiWord);
}
