package com.murshid.persistence.repo;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.Inflected;
import com.murshid.persistence.domain.MurshidEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InflectedRepositoryDB extends CrudRepository< com.murshid.persistence.domain.Inflected, com.murshid.persistence.domain.Inflected.InflectedKey> {

}
