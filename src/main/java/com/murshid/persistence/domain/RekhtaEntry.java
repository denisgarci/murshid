package com.murshid.persistence.domain;

import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.services.IDictionaryEntry;

import javax.persistence.*;

@Entity(name = "rekhta")
public class RekhtaEntry implements IDictionaryEntry {

    @EmbeddedId
    private DictionaryKey dictionaryKey;

    @Column(name = "latin")
    private String latinWord;

    @Column(name = "urdu")
    private String urduWord;

    @Column(name = "meaning")
    private String meaning;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech")
    private PartOfSpeech partOfSpeech;

    @Transient
    private String canonicalHindi;

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
    public String getCanonicalHindi() {
        return canonicalHindi;
    }

    public void setCanonicalHindi(String canonicalHindi) {
        this.canonicalHindi = canonicalHindi;
    }

    @Override
    public String getHindiWord() {
        return dictionaryKey.hindiWord;
    }

    @Override
    public int getWordIndex() {
        return dictionaryKey.wordIndex;
    }

    @Override
    public DictionarySource getDictionarySource() {
        return DictionarySource.REKHTA;
    }
}
