package com.murshid.persistence.repo;

import com.murshid.models.DictionaryRelationKey;
import com.murshid.persistence.domain.DictionaryRelations;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DictionaryRelationsRepository extends CrudRepository<DictionaryRelations, DictionaryRelationKey> {

    List<DictionaryRelations> findByDictionaryRelationKey(DictionaryRelationKey dictionaryRelationKey);

}
