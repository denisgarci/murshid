package com.murshid.persistence.repo;

import com.murshid.persistence.domain.Attempt;
import com.murshid.persistence.domain.AttemptKey;
import org.springframework.data.repository.CrudRepository;

public interface AttemptsRepository extends CrudRepository<Attempt, AttemptKey> {
}
