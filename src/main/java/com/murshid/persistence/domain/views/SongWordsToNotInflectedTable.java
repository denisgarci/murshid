package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class SongWordsToNotInflectedTable {

    private static Gson gson = new Gson();

    @SerializedName("song_word_indices")
    List<String> indices;


    @SerializedName("not_inflected_key")
    NotInflectedKey notInflectedKey;


    @JsonProperty("song_word_indices")
    public List<String> getIndices() {
        return indices;
    }

    public SongWordsToNotInflectedTable setIndices(List<String> indices) {
        this.indices = indices;
        return this;
    }


    @JsonProperty("not_inflected_key")
    public NotInflectedKey getNotInflectedKey() {
        return notInflectedKey;
    }

    public SongWordsToNotInflectedTable setNotInflectedKey(NotInflectedKey notInflectedKey) {
        this.notInflectedKey = notInflectedKey;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof SongWordsToNotInflectedTable)) { return false; }

        SongWordsToNotInflectedTable that = (SongWordsToNotInflectedTable) o;

        return new EqualsBuilder()
                .append(getIndices(), that.getIndices())
                .append(getNotInflectedKey(), that.getNotInflectedKey())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getIndices())
                .append(getNotInflectedKey())
                .toHashCode();
    }

    public String toString(){
        return gson.toJson(this);
    }
}
