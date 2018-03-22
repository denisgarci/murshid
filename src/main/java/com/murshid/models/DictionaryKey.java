package com.murshid.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DictionaryKey implements Serializable{
    @Column(name = "hindi_word", nullable = false)
    public String word;

    @Column(name = "word_index", nullable = false)
    public int wordIndex;
}
