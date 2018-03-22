package com.murshid.persistence;

import com.murshid.models.GonzaloEntry;
import org.springframework.data.repository.CrudRepository;

public interface GonzaloRepository extends CrudRepository<GonzaloEntry, String> {
}
