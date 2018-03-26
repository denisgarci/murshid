package com.murshid.persistence.repo;

import com.murshid.persistence.domain.DictionaryKey;
import com.murshid.models.WikitionaryEntry;
import org.springframework.data.repository.CrudRepository;

public interface WikitionaryRepository extends CrudRepository<WikitionaryEntry, DictionaryKey> {
}
