package com.murshid.persistence.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.AccidenceColumnConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity(name = "inflected")
public class Inflected implements Serializable, HasInflectedHindi<Inflected>, Cloneable{

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
    private List<Accidence> accidence = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = false)
    private PartOfSpeech partOfSpeech;

    @Column(name = "inflected_urdu", nullable = false)
    private String inflectedUrdu;

    @Column(name = "own_canonical")
    private String ownCanonical;

    public Inflected setOwnCanonical(String ownCanonical) {
        this.ownCanonical = ownCanonical;
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

    public String getOwnCanonical() {
        return ownCanonical;
    }

    public Inflected setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    @Override
    public String getHindi() {
        return inflectedKey.inflectedHindi;
    }

    public String getInflectedUrdu() {
        return inflectedUrdu;
    }

    public Inflected setUrdu(String urdu) {
        this.inflectedUrdu = urdu;
        return this;
    }

    @Override
    public Object clone() {
        try {
            Inflected master = (Inflected) super.clone();
            InflectedKey inflectedKey = new InflectedKey();
            inflectedKey.setInflectedHindi(this.inflectedKey.inflectedHindi);
            inflectedKey.setInflectedHindiIndex(this.inflectedKey.inflectedHindiIndex + 1);
            master
                    .setInflectedKey(inflectedKey)
                    .setAccidence(this.accidence)
                    .setPartOfSpeech(this.getPartOfSpeech())
                    .setMasterDictionary(this.getMasterDictionary())
                    .setUrdu(this.inflectedUrdu);
            return master;
        }catch (CloneNotSupportedException ex){
            return null;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof Inflected)) { return false; }

        Inflected master = (Inflected) o;

        Set<Accidence> thisSet = null;
        if (getAccidence() != null){
            thisSet = Sets.newHashSet(getAccidence());
        }

        Set<Accidence> thatSet = null;
        if (master.getAccidence() != null){
            thatSet = Sets.newHashSet(master.getAccidence());
        }



        return new EqualsBuilder()
                .append(getInflectedKey().getInflectedHindi(), master.getInflectedKey().inflectedHindi)
                .append(thisSet, thatSet)
                .append(getPartOfSpeech(), master.getPartOfSpeech())
                .append(getMasterDictionary(), getMasterDictionary())
                .isEquals();
    }

    @Override
    public String toString() {
        return "InflectedRepositoryDB{" +
                "  masterDictonaryKey='" + getMasterDictionary() + '\'' +
                "  inflectedHindi='" + inflectedKey.getInflectedHindi() + '\'' +
                ", inflectedUrdu='" + inflectedUrdu + '\'' +
                ", inflectedHindiIndex=" + inflectedKey.inflectedHindiIndex +
                ", partOfSpeech=" + partOfSpeech +
                ", accidence=" + accidence +
                '}';
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getInflectedKey().getInflectedHindi())
                .append(getAccidence())
                .append(getPartOfSpeech())
                .append(getMasterDictionary())
                .toHashCode();
    }

    public String getKey(){
        return getInflectedKey().inflectedHindi.concat("_").concat(Integer.toString(getInflectedKey().inflectedHindiIndex));
    }

    @Override
    public FluentModel self() {
        return this;
    }
}
