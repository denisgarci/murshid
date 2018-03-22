package com.murshid.persistence;

import com.murshid.models.HindiWord;
import org.springframework.data.repository.CrudRepository;

public interface HindiWordsRepository extends CrudRepository<HindiWord, String> {
}
