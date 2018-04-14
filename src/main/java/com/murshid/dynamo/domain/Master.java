package com.murshid.dynamo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Master {

    @JsonProperty("hindi_word")
    @Column(name = "hindi_word")
    private String hindiWord;

    @JsonProperty("word_index")
    @Column(name = "word_index")
    private int wordIndex;

    @JsonProperty("part_of_speech")
    @Column(name = "part_of_speech")
    private PartOfSpeech partOfSpeech;

    private Set<Accidence> accidence;

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

    public Set<Accidence> getAccidence() {
        return accidence;
    }

    public Master setAccidence(Set<Accidence> accidence) {
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

    @Override
    public Object clone() {
          Master master = new Master();
          master.setAccidence(new HashSet<>(this.accidence));
          master.setCanonicalKeys(this.canonicalKeys);
          master.setPartOfSpeech(this.getPartOfSpeech());
          master.setWordIndex(this.getWordIndex()+ 1);
          return master;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof Master)) { return false; }

        Master master = (Master) o;

        return new EqualsBuilder()
                .append(getHindiWord(), master.getHindiWord())
                .append(getPartOfSpeech(), master.getPartOfSpeech())
                .append(getAccidence(), master.getAccidence())
                .append(getCanonicalKeys(), master.getCanonicalKeys())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getHindiWord())
                .append(getPartOfSpeech())
                .append(getAccidence())
                .append(getCanonicalKeys())
                .toHashCode();
    }
}
