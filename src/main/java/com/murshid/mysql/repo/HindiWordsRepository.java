package com.murshid.mysql.repo;

import com.murshid.models.enums.DictionarySource;
import com.murshid.mysql.domain.HindiWord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HindiWordsRepository extends CrudRepository<HindiWord, String> {

    @Query(value = "SELECT * FROM hindi_words WHERE active = true and initial = ?1 AND  word not in (select entry from attempts where source= ?2  and successful = 1) ", nativeQuery = true)
    List<HindiWord> selectByInitialExceptSuccessful(String initial, DictionarySource dictionarySource);

    @Query(value = "SELECT * FROM hindi_words WHERE active = true and initial = ?1 AND  word not in (select entry from attempts where source= ?2 ) ", nativeQuery = true)
    List<HindiWord> selectByInitialExceptAllTried(String initial, DictionarySource dictionarySource);


    @Query(value = "SELECT distinct h.initial FROM hindi_words h")
    List<Character> selectDistinctInitials();

}
