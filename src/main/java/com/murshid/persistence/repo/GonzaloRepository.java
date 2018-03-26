package com.murshid.persistence.repo;

import com.murshid.persistence.domain.GonzaloEntry;
import org.springframework.data.repository.CrudRepository;

public interface GonzaloRepository extends CrudRepository<GonzaloEntry, String> {
}
