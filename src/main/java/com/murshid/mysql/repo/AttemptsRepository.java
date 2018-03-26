package com.murshid.mysql.repo;

import com.murshid.mysql.domain.Attempt;
import com.murshid.mysql.domain.AttemptKey;
import org.springframework.data.repository.CrudRepository;

public interface AttemptsRepository extends CrudRepository<Attempt, AttemptKey> {
}
