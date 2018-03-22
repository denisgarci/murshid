package com.murshid.models;

import com.murshid.DictionarySource;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class CanonicalKey implements Serializable{
    @Column(name = "canonical_word", nullable = true)
    private String word;

    @Column(name = "canonical_word_index", nullable = true)
    private int wordIndex;


    @Enumerated
    @Column(name ="dictionary_source", nullable = true)
    private DictionarySource dictionarySource;

}
