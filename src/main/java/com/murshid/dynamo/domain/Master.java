package com.murshid.dynamo.domain;

import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;

import javax.persistence.Column;
import java.util.List;

public class Master {

    @Column(name = "hindi_word")
    private String hindiWord;

    @Column(name = "word_index")
    private int wordIndex;

    private String urduSpelling;

    private PartOfSpeech partOfSpeech;

    private List<Accidence> accidence;

    private List<CanonicalKey> canonicalKeys;

    public String getHindiWord() {
        return hindiWord;
    }

    public Master setHindiWord(String hindiWord) {
        this.hindiWord = hindiWord;
        return this;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public Master setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
        return this;
    }

    public String getUrduSpelling() {
        return urduSpelling;
    }

    public Master setUrduSpelling(String urduSpelling) {
        this.urduSpelling = urduSpelling;
        return this;
    }

    public List<Accidence> getAccidence() {
        return accidence;
    }

    public Master setAccidence(List<Accidence> accidence) {
        this.accidence = accidence;
        return this;
    }

    public List<CanonicalKey> getCanonicalKeys() {
        return canonicalKeys;
    }

    public Master setCanonicalKeys(List<CanonicalKey> canonicalKeys) {
        this.canonicalKeys = canonicalKeys;
        return this;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public Master setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }
}
