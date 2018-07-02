package com.murshid.persistence.domain;

import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.AccidenceColumnConverter;
import com.murshid.services.IDictionaryEntry;

import javax.persistence.*;
import java.util.List;

@Entity(name = "caturvedi")
public class CaturvediEntry implements IDictionaryEntry {

    @EmbeddedId
    private DictionaryKey dictionaryKey;

    private int section;

    private int entry;

    @Column(name = "meaning", nullable = false)
    private String meaning;

    @Column(name = "extra_meaning", nullable = false)
    private String extraMeaning;

    @Column(name = "latin_word", nullable = true)
    private String latinWord;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = true)
    private PartOfSpeech partOfSpeech;

    @Column
    @Convert (converter = AccidenceColumnConverter.class)
    private List<Accidence> accidence;

    @Transient
    private String canonicalHindi;

    public int getSection() {
        return section;
    }

    public CaturvediEntry setSection(int section) {
        this.section = section;
        return this;
    }

    public int getEntry() {
        return entry;
    }

    public CaturvediEntry setEntry(int entry) {
        this.entry = entry;
        return this;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public CaturvediEntry setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public CaturvediEntry setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public String getLatinWord() {
        return latinWord;
    }

    public CaturvediEntry setLatinWord(String latinWord) {
        this.latinWord = latinWord;
        return this;
    }

    public DictionaryKey getDictionaryKey() {
        return dictionaryKey;
    }

    public CaturvediEntry setDictionaryKey(DictionaryKey dictionaryKey) {
        this.dictionaryKey = dictionaryKey;
        return this;
    }

    public String getExtraMeaning() {
        return extraMeaning;
    }

    public CaturvediEntry setExtraMeaning(String extraMeaning) {
        this.extraMeaning = extraMeaning;
        return this;
    }

    public List<Accidence> getAccidence() {
        return accidence;
    }

    public void setAccidence(List<Accidence> accidence) {
        this.accidence = accidence;
    }

    @Override
    public String getCanonicalHindi() {
        return canonicalHindi;
    }

    public void setCanonicalHindi(String canonicalHindi) {
        this.canonicalHindi = canonicalHindi;
    }

    @Override
    public DictionarySource getDictionarySource() {
        return DictionarySource.PLATTS;
    }

    @Override
    public String getHindiWord() {
        return dictionaryKey.hindiWord;
    }

    @Override
    public String getMeaning() {
        return meaning;
    }

    @Override
    public int getWordIndex() {
        return dictionaryKey.wordIndex;
    }
}
