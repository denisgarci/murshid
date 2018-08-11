package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.Inflected;
import com.murshid.persistence.domain.MurshidEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InflectedRepositoryDB extends CrudRepository< com.murshid.persistence.domain.Inflected, com.murshid.persistence.domain.Inflected.InflectedKey> {

    List<Inflected> findByInflectedKey_InflectedHindi(String inflectedHindi);

    Optional<Inflected> findByInflectedKey_InflectedHindiAndInflectedKey_InflectedHindiIndex(String hindiWord, int wordIndex);

    List<Inflected> findByMasterDictionaryId(int masterDictionaryId);

}
