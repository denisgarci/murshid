package com.murshid.dynamo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.PartOfSpeech;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;

public class NotInflected {
    @JsonProperty("master_dictionary_key")
    @Column(name = "master_dictionary_key")
    private DictionaryKey masterDictionaryKey;


    @JsonProperty("hindi")
    @Column(name = "hindi")
    private String hindi;

    @JsonProperty("urdu")
    @Column(name = "urdu")
    private String urdu;

    @JsonProperty("hindi_index")
    @Column(name = "hindi_index")
    private int hindiIndex;

    @JsonProperty("part_of_speech")
    @Column(name = "part_of_speech")
    private PartOfSpeech partOfSpeech;


    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public NotInflected setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public int getHindiIndex() {
        return hindiIndex;
    }

    public NotInflected setHindiIndex(int hindiIndex) {
        this.hindiIndex = hindiIndex;
        return this;
    }

    public String getHindi() {
        return hindi;
    }

    public NotInflected setHindi(String hindi) {
        this.hindi = hindi;
        return this;
    }

    public String getUrdu() {
        return urdu;
    }

    public NotInflected setUrdu(String urdu) {
        this.urdu = urdu;
        return this;
    }


    public DictionaryKey getMasterDictionaryKey() {
        return masterDictionaryKey;
    }

    public void setMasterDictionaryKey(DictionaryKey masterDictionaryKey) {
        this.masterDictionaryKey = masterDictionaryKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof NotInflected)) { return false; }

        NotInflected master = (NotInflected) o;

        return new EqualsBuilder()
                .append(getMasterDictionaryKey(), master.getMasterDictionaryKey())
                .append(getHindi(), master.getHindi())
                .append(getPartOfSpeech(), master.getPartOfSpeech())
                .append(getHindiIndex(), master.getHindiIndex())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getMasterDictionaryKey())
                .append(getHindi())
                .append(getPartOfSpeech())
                .append(getHindiIndex())
                .toHashCode();
    }

    public String getKey(){
        return getHindi().concat("_").concat(Integer.toString(getHindiIndex()));
    }
}
