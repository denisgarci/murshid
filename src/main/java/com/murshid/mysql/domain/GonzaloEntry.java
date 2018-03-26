package com.murshid.mysql.domain;

import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.mysql.repo.AccidenceConverter;

import javax.persistence.*;
import java.util.List;

@Entity(name = "gonzalo")
public class GonzaloEntry {

    @EmbeddedId
    private DictionaryKey dictionaryKey;

    @Column(name = "urdu_word", nullable = true)
    private String urduWord;

    @Column
    @Convert (converter = AccidenceConverter.class)
    private List<Accidence> accidence;

    private String meaning;

    @Enumerated
    @Column(name ="part_of_speech", nullable = true)
    private PartOfSpeech partOfSpeech;

    public CanonicalKey getCanonicalKey() {
        return canonicalKey;
    }

    public GonzaloEntry setCanonicalKey(CanonicalKey canonicalKey) {
        this.canonicalKey = canonicalKey;
        return this;
    }

    @Embedded
    private CanonicalKey canonicalKey;

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public GonzaloEntry setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

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
}
