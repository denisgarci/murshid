package com.murshid.persistence.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.AccidenceColumnConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity(name = "inflected")
public class Inflected implements Serializable{

    @Embeddable
    public static class InflectedKey implements Serializable {

        @JsonProperty("inflected_hindi")
        @Column(name = "inflected_hindi")
        public String inflectedHindi;

        @JsonProperty("inflected_hindi_index")
        @Column(name = "inflected_hindi_index")
        public int inflectedHindiIndex;

        public String getInflectedHindi() {
            return inflectedHindi;
        }

        public InflectedKey setInflectedHindi(String inflectedHindi) {
            this.inflectedHindi = inflectedHindi;
            return this;
        }

        public int getInflectedHindiIndex() {
            return inflectedHindiIndex;
        }

        public InflectedKey setInflectedHindiIndex(int inflectedHindiIndex) {
            this.inflectedHindiIndex = inflectedHindiIndex;
            return this;
        }

    }

    @EmbeddedId
    private InflectedKey inflectedKey;

    @JoinColumn(name = "master_dictionary_id")
    @ManyToOne
    private MasterDictionary masterDictionary;

    @Column
    @Convert (converter = AccidenceColumnConverter.class)
    private List<Accidence> accidence;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = false)
    private PartOfSpeech partOfSpeech;

    @Column(name = "inflected_urdu", nullable = false)
    private String inflectedUrdu;

    @Column(name = "own_meaning", nullable = true)
    private boolean ownMeaning;

    public boolean isOwnMeaning() {
        return ownMeaning;
    }

    public Inflected setOwnMeaning(boolean ownMeaning) {
        this.ownMeaning = ownMeaning;
        return this;
    }

    public InflectedKey getInflectedKey() {
        return inflectedKey;
    }

    public Inflected setInflectedKey(InflectedKey inflectedKey) {
        this.inflectedKey = inflectedKey;
        return this;
    }

    public MasterDictionary getMasterDictionary() {
        return masterDictionary;
    }

    public Inflected setMasterDictionary(MasterDictionary masterDictionary) {
        this.masterDictionary = masterDictionary;
        return this;
    }


    public List<Accidence> getAccidence() {
        return accidence;
    }

    public Inflected setAccidence(List<Accidence> accidence) {
        this.accidence = accidence;
        return this;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public Inflected setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public String getInflectedUrdu() {
        return inflectedUrdu;
    }

    public Inflected setInflectedUrdu(String inflectedUrdu) {
        this.inflectedUrdu = inflectedUrdu;
        return this;
    }



}
