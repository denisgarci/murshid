package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.PlattsEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlattsRepository extends CrudRepository<PlattsEntry, Integer> {

    List<PlattsEntry> findByMeaningLikeOrKeystringLike(String username, String aa);

    List<PlattsEntry> findByDictionaryKeyHindiWordOrUrduWord(String hindiWord, String urduWord);

    List<PlattsEntry> findByDictionaryKeyHindiWord(String hindiWord);

    List<PlattsEntry> findByDictionaryKey(DictionaryKey dictionaryKey);

}
