package com.murshid.models;

import com.murshid.models.enums.Accidence;
import com.murshid.persistence.AccidenceColumnConverter;

import javax.persistence.*;
import java.util.List;

@Entity(name = "gonzalo")
public class GonzaloEntry {

    @EmbeddedId
    private DictionaryKey dictionaryKey;

    @Column(name = "urdu_word", nullable = true)
    private String urduWord;

    @Column
    @Convert (converter = AccidenceColumnConverter.class)
    private List<Accidence> accidence;

    private String meaning;

    @Embedded
    private CanonicalKey canonicalKey;


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

    public List<Accidence> getAccidence() {
        return accidence;
    }

    public GonzaloEntry setAccidence(List<Accidence> accidence) {
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

    public CanonicalKey getCanonicalKey() {
        return canonicalKey;
    }

    public GonzaloEntry setCanonicalKey(CanonicalKey canonicalKey) {
        this.canonicalKey = canonicalKey;
        return this;
    }

}
