package com.murshid.persistence.repo;

import com.murshid.persistence.domain.Inflected;
import com.murshid.persistence.domain.NotInflected;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface NotInflectedRepositoryDB extends CrudRepository<NotInflected, NotInflected.NotInflectedKey> {

    List<NotInflected> findByNotInflectedKey_Hindi(String inflectedHindi);

    Optional<NotInflected> findByNotInflectedKey_HindiAndNotInflectedKey_HindiIndex(String hindi, int index);

    List<NotInflected> findByMasterDictionaryId(int masterDictionaryId);

}
