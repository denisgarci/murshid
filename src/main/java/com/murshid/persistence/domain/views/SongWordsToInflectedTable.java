package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class SongWordsToInflectedTable {

    private static Gson gson = new Gson();

    @SerializedName("song_word_indices")
    List<String> indices;


    @SerializedName("inflected_key")
    InflectedKey inflectedKey;


    @JsonProperty("song_word_indices")
    public List<String> getIndices() {
        return indices;
    }

    public SongWordsToInflectedTable setIndices(List<String> indices) {
        this.indices = indices;
        return this;
    }


    @JsonProperty("inflected_key")
    public InflectedKey getInflectedKey() {
        return inflectedKey;
    }

    public SongWordsToInflectedTable setInflectedKey(InflectedKey inflectedKey) {
        this.inflectedKey = inflectedKey;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof SongWordsToInflectedTable)) { return false; }

        SongWordsToInflectedTable that = (SongWordsToInflectedTable) o;

        return new EqualsBuilder()
                .append(getIndices(), that.getIndices())
                .append(getInflectedKey(), that.getInflectedKey())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getIndices())
                .append(getInflectedKey())
                .toHashCode();
    }

    public String toString(){
        return gson.toJson(this);
    }
}
