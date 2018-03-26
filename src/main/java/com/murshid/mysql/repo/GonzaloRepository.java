package com.murshid.mysql.repo;

import com.murshid.mysql.domain.GonzaloEntry;
import org.springframework.data.repository.CrudRepository;

public interface GonzaloRepository extends CrudRepository<GonzaloEntry, String> {
}
