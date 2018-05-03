package com.murshid.persistence.repo;

import com.murshid.persistence.domain.HindiWord;
import com.murshid.persistence.domain.SpellCheckEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpellCheckRepository extends CrudRepository<SpellCheckEntry, String> {

    @Query(value = "SELECT * FROM spell_check WHERE active = true and initial = ?1 AND  hindi_word not in (select entry from attempts where source= ?2  and successful = 1) ", nativeQuery = true)
    List<HindiWord> selectByInitialExceptSuccessful(String initial, String dictionarySource);

    @Query(value = "SELECT * FROM spell_check WHERE active = true and initial = ?1 AND  hindi_word not in (select entry from attempts where source= ?2 ) ", nativeQuery = true)
    List<HindiWord> selectByInitialExceptAllTried(String initial, String dictionarySource);

    SpellCheckEntry findByHindiWord(String hindiWord);


    @Query(value = "SELECT distinct h.initial FROM spell_check h")
    List<Character> selectDistinctInitials();

}
