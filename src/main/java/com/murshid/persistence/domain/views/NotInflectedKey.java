package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NotInflectedKey {


    @SerializedName("hindi")
    String hindi;


    @SerializedName("hindi_index")
    int hindiIndex;


    @JsonProperty("hindi")
    public String getHindi() {
        return hindi;
    }

    public NotInflectedKey setHindi(String hindi) {
        this.hindi = hindi;
        return this;
    }


    @JsonProperty("hindi_index")
    public int getHindiIndex() {
        return hindiIndex;
    }

    public NotInflectedKey setHindiIndex(int hindiIndex) {
        this.hindiIndex = hindiIndex;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof NotInflectedKey)) { return false; }

        NotInflectedKey inflectedKey = (NotInflectedKey) o;

        return new EqualsBuilder()
                .append(getHindiIndex(), inflectedKey.getHindiIndex())
                .append(getHindi(), inflectedKey.getHindi())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getHindi())
                .append(getHindiIndex())
                .toHashCode();
    }
}
