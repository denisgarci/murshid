package com.murshid.mysql.repo;

import com.murshid.mysql.domain.DictionaryKey;
import com.murshid.models.WikitionaryEntry;
import org.springframework.data.repository.CrudRepository;

public interface WikitionaryRepository extends CrudRepository<WikitionaryEntry, DictionaryKey> {
}
