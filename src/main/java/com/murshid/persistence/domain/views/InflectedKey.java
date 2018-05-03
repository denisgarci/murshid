package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class InflectedKey {


    @SerializedName("inflected_hindi")
    String inflectedHindi;


    @SerializedName("inflected_hindi_index")
    int inflectedHindiIndex;


    @JsonProperty("inflected_hindi")
    public String getInflectedHindi() {
        return inflectedHindi;
    }

    public InflectedKey setInflectedHindi(String inflectedHindi) {
        this.inflectedHindi = inflectedHindi;
        return this;
    }


    @JsonProperty("inflected_hindi_index")
    public int getInflectedHindiIndex() {
        return inflectedHindiIndex;
    }

    public InflectedKey setInflectedHindiIndex(int inflectedHindiIndex) {
        this.inflectedHindiIndex = inflectedHindiIndex;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof InflectedKey)) { return false; }

        InflectedKey inflectedKey = (InflectedKey) o;

        return new EqualsBuilder()
                .append(getInflectedHindiIndex(), inflectedKey.getInflectedHindiIndex())
                .append(getInflectedHindi(), inflectedKey.getInflectedHindi())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getInflectedHindi())
                .append(getInflectedHindiIndex())
                .toHashCode();
    }
}
