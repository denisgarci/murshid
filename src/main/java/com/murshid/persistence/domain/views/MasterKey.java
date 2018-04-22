package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MasterKey {


    @SerializedName("hindi_word")
    String hindiWord;


    @SerializedName("word_index")
    int wordIndex;


    @JsonProperty("hindi_word")
    public String getHindiWord() {
        return hindiWord;
    }

    public MasterKey setHindiWord(String hindiWord) {
        this.hindiWord = hindiWord;
        return this;
    }


    @JsonProperty("word_index")
    public int getWordIndex() {
        return wordIndex;
    }

    public MasterKey setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof MasterKey)) { return false; }

        MasterKey masterKey = (MasterKey) o;

        return new EqualsBuilder()
                .append(getWordIndex(), masterKey.getWordIndex())
                .append(getHindiWord(), masterKey.getHindiWord())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getHindiWord())
                .append(getWordIndex())
                .toHashCode();
    }
}
