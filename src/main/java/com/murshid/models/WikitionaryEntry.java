package com.murshid.models;

import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.AccidenceColumnConverter;
import com.murshid.persistence.domain.DictionaryKey;

import javax.persistence.*;
import java.util.List;

@Entity(name = "wikitionary")
public class WikitionaryEntry {

    @EmbeddedId
    private DictionaryKey dictionaryKey;

    @Column(name = "urdu_spelling", nullable = true)
    private String urduSpelling;

    @Column
    @Convert (converter = AccidenceColumnConverter.class)
    private List<Accidence> accidence;

    private String meaning;

    private String etymology;

    @Column(name = "ipa_pronunciation", nullable = true)
    private String ipaPronunciation;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = true)
    private PartOfSpeech partOfSpeech;

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public WikitionaryEntry setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public DictionaryKey getDictionaryKey() {
        return dictionaryKey;
    }

    public WikitionaryEntry setDictionaryKey(DictionaryKey dictionaryKey) {
        this.dictionaryKey = dictionaryKey;
        return this;
    }


    public String getUrduSpelling() {
        return urduSpelling;
    }

    public WikitionaryEntry setUrduSpelling(String urduSpelling) {
        this.urduSpelling = urduSpelling;
        return this;
    }

    public String getIpaPronunciation() {
        return ipaPronunciation;
    }

    public WikitionaryEntry setIpaPronunciation(String ipaPronunciation) {
        this.ipaPronunciation = ipaPronunciation;
        return this;
    }

    public List<Accidence> getAccidence() {
        return accidence;
    }

    public WikitionaryEntry setAccidence(List<Accidence> accidence) {
        this.accidence = accidence;
        return this;
    }

    public String getMeaning() {
        return meaning;
    }

    public WikitionaryEntry setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public String getEtymology() {
        return etymology;
    }

    public WikitionaryEntry setEtymology(String etymology) {
        this.etymology = etymology;
        return this;
    }

}
