package com.murshid.persistence.repo;

import com.murshid.models.DictionaryEntryKey;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.DictionaryEntry;
import com.murshid.persistence.domain.MasterDictionary;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DictionaryEntryRepository extends CrudRepository<DictionaryEntry, Integer> {

    List<DictionaryEntry> findByMasterDictionary(MasterDictionary masterDictionary);

    Optional<DictionaryEntry> findByMasterDictionaryAndDictionarySource(MasterDictionary masterDictionary, DictionarySource dictionarySource);

}
