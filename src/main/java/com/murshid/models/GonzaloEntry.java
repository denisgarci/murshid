package com.murshid.models;

import com.murshid.DictionarySource;

import javax.persistence.*;

@Entity(name = "gonzalo")
public class GonzaloEntry {

    @EmbeddedId
    private DictionaryKey dictionaryKey;

    @Column(name = "urdu_word", nullable = true)
    private String urduWord;

    private String accidence;

    private String meaning;

    @Column(name = "canonical_word", nullable = true)
    private String canonicalWord;

    @Column(name = "canonical_word_index", nullable = true)
    private int canonicalWordIndex;


    @Enumerated
    @Column(name ="dictionary_source", nullable = true)
    private DictionarySource dictionarySource;

    public DictionaryKey getDictionaryKey() {
        return dictionaryKey;
    }

    public GonzaloEntry setDictionaryKey(DictionaryKey dictionaryKey) {
        this.dictionaryKey = dictionaryKey;
        return this;
    }

    public String getUrduWord() {
        return urduWord;
    }

    public GonzaloEntry setUrduWord(String urduWord) {
        this.urduWord = urduWord;
        return this;
    }

    public String getAccidence() {
        return accidence;
    }

    public GonzaloEntry setAccidence(String accidence) {
        this.accidence = accidence;
        return this;
    }

    public String getMeaning() {
        return meaning;
    }

    public GonzaloEntry setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public String getCanonicalWord() {
        return canonicalWord;
    }

    public GonzaloEntry setCanonicalWord(String canonicalWord) {
        this.canonicalWord = canonicalWord;
        return this;
    }

    public int getCanonicalWordIndex() {
        return canonicalWordIndex;
    }

    public GonzaloEntry setCanonicalWordIndex(int canonicalWordIndex) {
        this.canonicalWordIndex = canonicalWordIndex;
        return this;
    }

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }

    public GonzaloEntry setDictionarySource(DictionarySource dictionarySource) {
        this.dictionarySource = dictionarySource;
        return this;
    }
}
