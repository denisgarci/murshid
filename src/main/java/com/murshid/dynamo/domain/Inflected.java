package com.murshid.dynamo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Inflected implements Cloneable {

    @JsonProperty("master_dictionary_key")
    @Column(name = "master_dictionary_key")
    private DictionaryKey masterDictionaryKey;

    @JsonProperty("inflected_hindi")
    @Column(name = "inflected_hindi")
    private String inflectedHindi;

    @JsonProperty("canonical_hindi")
    @Column(name = "canonical_hindi")
    private String canonicalHindi;

    @JsonProperty("inflected_urdu")
    @Column(name = "inflected_urdu")
    private String inflectedUrdu;

    @JsonProperty("inflected_hindi_index")
    @Column(name = "inflected_hindi_index")
    private int inflectedHindiIndex;

    @JsonProperty("master_dictionary_id")
    @Column(name = "master_dictionary_id")
    private int masterDictionaryId;

    @JsonProperty("own_meaning")
    @Column(name = "own_meaning")
    private boolean ownMeaning;


    @JsonProperty("part_of_speech")
    @Column(name = "part_of_speech")
    private PartOfSpeech partOfSpeech;

    private TreeSet<Accidence> accidence;

    public Set<Accidence> getAccidence() {
        return accidence;
    }

    public Inflected setAccidence(Set<Accidence> accidence) {
        if (accidence != null){
            TreeSet<Accidence> treeSet = Sets.newTreeSet();
            treeSet.addAll(accidence);
            this.accidence = treeSet;
        }
        return this;
    }

    public DictionaryKey getMasterDictionaryKey() {
        return masterDictionaryKey;
    }

    public Inflected setMasterDictionaryKey(DictionaryKey masterDictionaryKey) {
        this.masterDictionaryKey = masterDictionaryKey;
        return this;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public Inflected setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public int getInflectedHindiIndex() {
        return inflectedHindiIndex;
    }

    public Inflected setInflectedHindiIndex(int inflectedHindiIndex) {
        this.inflectedHindiIndex = inflectedHindiIndex;
        return this;
    }

    public int getMasterDictionaryId() {
        return masterDictionaryId;
    }

    public Inflected setMasterDictionaryId(int masterDictionaryId) {
        this.masterDictionaryId = masterDictionaryId;
        return this;
    }



    public String getInflectedHindi() {
        return inflectedHindi;
    }

    public Inflected setInflectedHindi(String inflectedHindi) {
        this.inflectedHindi = inflectedHindi;
        return this;
    }

    public String getInflectedUrdu() {
        return inflectedUrdu;
    }

    public Inflected setInflectedUrdu(String inflectedUrdu) {
        this.inflectedUrdu = inflectedUrdu;
        return this;
    }

    public String getCanonicalHindi() {
        return canonicalHindi;
    }

    public Inflected setCanonicalHindi(String canonicalHindi) {
        this.canonicalHindi = canonicalHindi;
        return this;
    }

    public boolean isOwnMeaning() {
        return ownMeaning;
    }

    public void setOwnMeaning(boolean ownMeaning) {
        this.ownMeaning = ownMeaning;
    }

    @Override
    public Object clone() {
        try {
            Inflected master = (Inflected) super.clone();
            master
                    .setInflectedHindi(this.inflectedHindi)
                    .setInflectedUrdu(this.inflectedUrdu)
                    .setAccidence(new HashSet<>(this.accidence))
                    .setPartOfSpeech(this.getPartOfSpeech())
                    .setMasterDictionaryKey(this.masterDictionaryKey)
                    .setInflectedHindiIndex(this.getInflectedHindiIndex() + 1);
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

        return new EqualsBuilder()
                .append(getMasterDictionaryKey(), master.getMasterDictionaryKey())
                .append(getInflectedHindi(), master.getInflectedHindi())
                .append(getPartOfSpeech(), master.getPartOfSpeech())
                .append(getAccidence(), master.getAccidence())
                //.append(getInflectedHindiIndex(), master.getInflectedHindiIndex())
                .isEquals();
    }

    @Override
    public String toString() {
        return "Inflected{" +
                "  masterDictonaryKey='" + masterDictionaryKey + '\'' +
                "  inflectedHindi='" + inflectedHindi + '\'' +
                ", inflectedUrdu='" + inflectedUrdu + '\'' +
                ", inflectedHindiIndex=" + inflectedHindiIndex +
                ", partOfSpeech=" + partOfSpeech +
                ", accidence=" + accidence +
                '}';
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getMasterDictionaryKey())
                .append(getInflectedHindi())
                .append(getPartOfSpeech())
                .append(getAccidence())
                .toHashCode();
    }

    public String getKey(){
        return getInflectedHindi().concat("_").concat(Integer.toString(getInflectedHindiIndex()));
    }
}
