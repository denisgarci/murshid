package com.murshid.persistence.domain;

import com.murshid.models.CanonicalKey;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.AccidenceColumnConverter;
import com.murshid.services.IDictionaryEntry;

import javax.persistence.*;
import java.util.List;

@Entity(name = "murshid")
public class MurshidEntry implements IDictionaryEntry {

    @EmbeddedId
    private DictionaryKey dictionaryKey;

    @Column(name = "urdu_word", nullable = true)
    private String urduWord;

    @Column
    @Convert (converter = AccidenceColumnConverter.class)
    private List<Accidence> accidence;

    private String meaning;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = true)
    private PartOfSpeech partOfSpeech;

    public CanonicalKey getCanonicalKey() {
        return canonicalKey;
    }

    public MurshidEntry setCanonicalKey(CanonicalKey canonicalKey) {
        this.canonicalKey = canonicalKey;
        return this;
    }

    @Embedded
    private CanonicalKey canonicalKey;

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public MurshidEntry setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public DictionaryKey getDictionaryKey() {
        return dictionaryKey;
    }

    public MurshidEntry setDictionaryKey(DictionaryKey dictionaryKey) {
        this.dictionaryKey = dictionaryKey;
        return this;
    }

    public String getUrduWord() {
        return urduWord;
    }

    public MurshidEntry setUrduWord(String urduWord) {
        this.urduWord = urduWord;
        return this;
    }

    public List<Accidence> getAccidence() {
        return accidence;
    }

    public MurshidEntry setAccidence(List<Accidence> accidence) {
        this.accidence = accidence;
        return this;
    }

    public String getMeaning() {
        return meaning;
    }

    public MurshidEntry setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public String getHindiWord(){
        return this.dictionaryKey.hindiWord;
    }

    @Override
    public int getWordIndex() {
        return this.dictionaryKey.wordIndex;
    }

    @Override
    public DictionarySource getDictionarySource() {
        return DictionarySource.MURSHID;
    }
}
