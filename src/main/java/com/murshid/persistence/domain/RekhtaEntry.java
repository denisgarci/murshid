package com.murshid.persistence.domain;

import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.services.HasDictionaryKey;

import javax.persistence.*;

@Entity(name = "rekhta")
public class RekhtaEntry implements HasDictionaryKey{

    @EmbeddedId
    private DictionaryKey dictionaryKey;

    @Column(name = "latin", nullable = true)
    private String latinWord;

    @Column(name = "urdu", nullable = true)
    private String urduWord;

    @Column(name = "meaning", nullable = false)
    private String meaning;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = true)
    private PartOfSpeech partOfSpeech;

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public RekhtaEntry setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public String getMeaning() {
        return meaning;
    }

    public RekhtaEntry setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public DictionaryKey getDictionaryKey() {
        return dictionaryKey;
    }

    public RekhtaEntry setDictionaryKey(DictionaryKey dictionaryKey) {
        this.dictionaryKey = dictionaryKey;
        return this;
    }

    public String getLatinWord() {
        return latinWord;
    }

    public RekhtaEntry setLatinWord(String latinWord) {
        this.latinWord = latinWord;
        return this;
    }

    public String getUrduWord() {
        return urduWord;
    }

    public RekhtaEntry setUrduWord(String urduWord) {
        this.urduWord = urduWord;
        return this;
    }

    @Override
    public String getHindiWord() {
        return dictionaryKey.hindiWord;
    }

    @Override
    public int getWordIndex() {
        return dictionaryKey.wordIndex;
    }
}
