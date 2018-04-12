package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.GonzaloEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GonzaloRepository extends CrudRepository<GonzaloEntry, DictionaryKey> {

    List<GonzaloEntry> findByDictionaryKey(DictionaryKey dictionaryKey);

    List<GonzaloEntry> findByDictionaryKeyHindiWord(String hindiWord);
}
