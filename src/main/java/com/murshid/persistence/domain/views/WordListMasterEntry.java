package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class WordListMasterEntry {

    private static Gson gson = new Gson();

    @SerializedName("indices")
    List<String> indices;


    @SerializedName("master_key")
    MasterKey masterKey;


    @JsonProperty("indices")
    public List<String> getIndices() {
        return indices;
    }

    public WordListMasterEntry setIndices(List<String> indices) {
        this.indices = indices;
        return this;
    }


    @JsonProperty("master_key")
    public MasterKey getMasterKey() {
        return masterKey;
    }

    public WordListMasterEntry setMasterKey(MasterKey masterKey) {
        this.masterKey = masterKey;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof WordListMasterEntry)) { return false; }

        WordListMasterEntry that = (WordListMasterEntry) o;

        return new EqualsBuilder()
                .append(getIndices(), that.getIndices())
                .append(getMasterKey(), that.getMasterKey())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getIndices())
                .append(getMasterKey())
                .toHashCode();
    }

    public String toString(){
        return gson.toJson(this);
    }
}
