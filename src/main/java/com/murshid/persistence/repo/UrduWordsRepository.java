package com.murshid.persistence.repo;

import com.murshid.persistence.domain.UrduWord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UrduWordsRepository extends CrudRepository<UrduWord, String> {

    @Query(value = "SELECT * FROM urdu_words WHERE active = true and initial = ?1 AND  word not in (select entry from attempts where source= ?2  and successful = 1) ", nativeQuery = true)
    List<UrduWord> selectByInitialExceptSuccessful(String initial, String dictionarySource);

    @Query(value = "SELECT * FROM urdu_words WHERE active = true and initial = ?1 AND  word not in (select entry from attempts where source= ?2 ) ", nativeQuery = true)
    List<UrduWord> selectByInitialExceptAllTried(String initial, String dictionarySource);


    @Query(value = "SELECT distinct h.initial FROM urdu_words h")
    List<Character> selectDistinctInitials();

}
