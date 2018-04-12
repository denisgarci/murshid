package com.murshid.dynamo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;

import javax.persistence.Column;
import java.util.List;

public class Master {

    @JsonProperty("hindi_word")
    @Column(name = "hindi_word")
    private String hindiWord;

    @JsonProperty("word_index")
    @Column(name = "word_index")
    private int wordIndex;

    @JsonProperty("urdu_spelling")
    @Column(name = "urdu_spelling")
    private String urduSpelling;

    @JsonProperty("part_of_speech")
    @Column(name = "part_of_speech")
    private PartOfSpeech partOfSpeech;

    private List<Accidence> accidence;

    @JsonProperty("canonical_keys")
    @Column(name = "canonical_keys")
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
