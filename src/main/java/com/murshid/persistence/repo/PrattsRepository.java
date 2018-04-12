package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.PrattsEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PrattsRepository extends CrudRepository<PrattsEntry, Integer> {

    List<PrattsEntry> findByBodyLikeOrKeystringLike(String username, String aa);

    List<PrattsEntry> findByDictionaryKeyHindiWordOrUrduWord(String hindiWord, String urduWord);

    List<PrattsEntry> findByDictionaryKeyHindiWord(String hindiWord);

    List<PrattsEntry> findByDictionaryKey(DictionaryKey dictionaryKey);

}
