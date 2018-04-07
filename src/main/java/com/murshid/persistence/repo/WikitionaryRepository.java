package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.WikitionaryEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WikitionaryRepository extends CrudRepository<WikitionaryEntry, DictionaryKey> {

    List<WikitionaryEntry> findByDictionaryKeyWord(String hindiWord);
}
