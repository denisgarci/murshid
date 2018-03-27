package com.murshid.dynamo.domain;

import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;

import java.util.List;

public class Master {

    private String hindiWord;

    private int wordIndex;

    private String urduSpelling;

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


}
