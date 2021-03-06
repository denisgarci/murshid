package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.FluentModel;
import com.murshid.persistence.domain.HasInflectedHindi;

import java.util.List;

public class InflectedView implements HasInflectedHindi {

    @JsonProperty("inflected_hindi")
    private String inflectedHindi;

    @JsonProperty("inflected_hindi_index")
    private int inflectedHindiIndex;

    private String inflectedUrdu;

    @JsonProperty("part_of_speech")
    private PartOfSpeech partOfSpeech;

    @JsonProperty("master_dictionary_key")
    private DictionaryKey masterDictionaryKey;

    @JsonProperty("accidence")
    private List<Accidence> accidence;

    @JsonProperty("own_canonical")
    private String ownCanonical;

    public int getInflectedHindiIndex() {
        return inflectedHindiIndex;
    }

    public void setInflectedHindiIndex(int inflectedHindiIndex) {
        this.inflectedHindiIndex = inflectedHindiIndex;
    }

    public String getHindi() {
        return inflectedHindi;
    }

    public InflectedView setInflectedHindi(String inflectedHindi) {
        this.inflectedHindi = inflectedHindi;
        return this;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public InflectedView setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public DictionaryKey getMasterDictionaryKey() {
        return masterDictionaryKey;
    }

    public InflectedView setMasterDictionaryKey(DictionaryKey masterDictionaryKey) {
        this.masterDictionaryKey = masterDictionaryKey;
        return this;
    }

    public List<Accidence> getAccidence() {
        return accidence;
    }

    public InflectedView setAccidence(List<Accidence> accidence) {
        this.accidence = accidence;
        return this;
    }


    public String getInflectedUrdu() {
        return inflectedUrdu;
    }

    public InflectedView setUrdu(String urdu) {
        this.inflectedUrdu = urdu;
        return this;
    }

    public String getOwnCanonical() {
        return ownCanonical;
    }

    public void setOwnCanonical(String ownCanonical) {
        this.ownCanonical = ownCanonical;
    }

    @Override
    public FluentModel self() {
        return this;
    }
}
