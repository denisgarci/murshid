package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.Inflected;
import com.murshid.persistence.domain.MurshidEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InflectedRepositoryDB extends CrudRepository<Inflected, Inflected.InflectedKey> {

    List<Inflected> findByInflectedKey_InflectedHindi(String inflectedHindi);

    @Query("select i from inflected i where i.inflectedKey.inflectedHindi = :hindiWord and i.inflectedKey.inflectedHindiIndex = :hindiWordIndex")
    Optional<Inflected> findOne(@Param("hindiWord") String hindiWord, @Param("hindiWordIndex") int hindiWordIndex);

    List<Inflected> findByMasterDictionaryId(int masterDictionaryId);

}
