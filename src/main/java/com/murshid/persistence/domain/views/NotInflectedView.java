package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.FluentModel;
import com.murshid.persistence.domain.HasInflectedHindi;

import java.util.List;

public class NotInflectedView implements HasInflectedHindi {

    @JsonProperty("hindi")
    private String hindi;

    @JsonProperty("hindi_index")
    private int inflectedHindiIndex;

    private String urdu;

    @JsonProperty("part_of_speech")
    private PartOfSpeech partOfSpeech;

    @JsonProperty("master_dictionary_key")
    private DictionaryKey masterDictionaryKey;

    public int getInflectedHindiIndex() {
        return inflectedHindiIndex;
    }

    public void setInflectedHindiIndex(int inflectedHindiIndex) {
        this.inflectedHindiIndex = inflectedHindiIndex;
    }

    public String getHindi() {
        return hindi;
    }

    public NotInflectedView setHindi(String hindi) {
        this.hindi = hindi;
        return this;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public NotInflectedView setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public DictionaryKey getMasterDictionaryKey() {
        return masterDictionaryKey;
    }

    public NotInflectedView setMasterDictionaryKey(DictionaryKey masterDictionaryKey) {
        this.masterDictionaryKey = masterDictionaryKey;
        return this;
    }

    public String getUrdu() {
        return urdu;
    }

    public NotInflectedView setUrdu(String urdu) {
        this.urdu = urdu;
        return this;
    }

    @Override
    public FluentModel self() {
        return this;
    }
}
