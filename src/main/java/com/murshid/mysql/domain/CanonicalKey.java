package com.murshid.mysql.domain;

import com.murshid.models.enums.DictionarySource;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class CanonicalKey implements Serializable{
    @Column(name = "canonical_word", nullable = true)
    public String word;

    @Column(name = "canonical_word_index", nullable = true)
    public int wordIndex;


    @Enumerated(EnumType.STRING)
    @Column(name ="dictionary_source", nullable = true)
    public DictionarySource dictionarySource;

    public CanonicalKey setWord(String word) {
        this.word = word;
        return this;
    }

    public CanonicalKey setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
        return this;
    }

    public CanonicalKey setDictionarySource(DictionarySource dictionarySource) {
        this.dictionarySource = dictionarySource;
        return this;
    }

    public String getWord() {
        return word;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }
}
