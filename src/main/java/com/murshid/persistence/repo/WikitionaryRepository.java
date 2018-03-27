package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.models.WikitionaryEntry;
import org.springframework.data.repository.CrudRepository;

public interface WikitionaryRepository extends CrudRepository<WikitionaryEntry, DictionaryKey> {
}
