package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.domain.WikitionaryEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RekhtaRepository extends CrudRepository<RekhtaEntry, DictionaryKey> {

    List<RekhtaEntry> findByDictionaryKeyHindiWord(String hindiWord);

    List<RekhtaEntry> findByDictionaryKeyHindiWordLike(String hindiWord);


}
