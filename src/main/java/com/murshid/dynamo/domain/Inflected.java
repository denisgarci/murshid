package com.murshid.dynamo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Inflected {

    @JsonProperty("inflected_hindi")
    @Column(name = "inflected_hindi")
    private String inflectedHindi;

    @JsonProperty("inflected_urdu")
    @Column(name = "inflected_urdu")
    private String inflectedUrdu;

    @JsonProperty("inflected_hindi_index")
    @Column(name = "inflected_hindi_index")
    private int inflectedHindiIndex;

    @JsonProperty("part_of_speech")
    @Column(name = "part_of_speech")
    private PartOfSpeech partOfSpeech;

    @JsonProperty("part_of_speech_label")
    private String partOfSpeechLabel;

    @JsonProperty("canonical_hindi")
    @Column(name = "canonical_hindi")
    private String canonicalHindi;

    @JsonProperty("canonical_urdu")
    @Column(name = "canonical_urdu")
    private String canonicalUrdu;

    private TreeSet<Accidence> accidence;

    @SerializedName("accidence_labels")
    @JsonProperty("accidence_labels")
    private Set<String> accidenceLabels;

    @JsonProperty("canonical_keys")
    @Column(name = "canonical_keys")
    private Set<CanonicalKey> canonicalKeys;

    @JsonProperty("part_of_speech_label")
    public String getpartOfSpeecLabel(){
        return partOfSpeech.getLabel();
    }

    public Set<Accidence> getAccidence() {
        return accidence;
    }

    public Inflected setAccidence(Set<Accidence> accidence) {
        if (accidence != null){
            TreeSet<Accidence> treeSet = Sets.newTreeSet();
            treeSet.addAll(accidence);
            this.accidence = treeSet;

            //labels
            Set<String> accidenceLabels = Sets.newLinkedHashSet();
            treeSet.forEach(acc -> accidenceLabels.add(acc.getLabel()));
            this.accidenceLabels = accidenceLabels;
        }
        return this;
    }

    public Set<CanonicalKey> getCanonicalKeys() {
        return canonicalKeys;
    }

    public Inflected setCanonicalKeys(Set<CanonicalKey> canonicalKeys) {
        this.canonicalKeys = canonicalKeys;
        return this;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public Inflected setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        this.partOfSpeechLabel = partOfSpeech.getLabel();
        return this;
    }

    public int getInflectedHindiIndex() {
        return inflectedHindiIndex;
    }

    public Inflected setInflectedHindiIndex(int inflectedHindiIndex) {
        this.inflectedHindiIndex = inflectedHindiIndex;
        return this;
    }

    public String getPartOfSpeechLabel() {
        return partOfSpeechLabel;
    }

    public Set<String> getAccidenceLabels() {
        return accidenceLabels;
    }

    public Inflected setAccidenceLabels(Set<String> accidenceLabels) {
        this.accidenceLabels = accidenceLabels;
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

    public String getCanonicalUrdu() {
        return canonicalUrdu;
    }

    public Inflected setCanonicalUrdu(String canonicalUrdu) {
        this.canonicalUrdu = canonicalUrdu;
        return this;
    }

    @Override
    public Object clone() {
          Inflected master = new Inflected();
          master
           .setInflectedHindi(this.inflectedHindi)
           .setInflectedUrdu(this.inflectedUrdu)
           .setAccidence(new HashSet<>(this.accidence))
           .setCanonicalHindi(this.canonicalHindi)
           .setCanonicalUrdu(this.canonicalUrdu)
           .setCanonicalKeys(this.canonicalKeys)
           .setPartOfSpeech(this.getPartOfSpeech())
           .setInflectedHindiIndex(this.getInflectedHindiIndex()+ 1);
          return master;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof Inflected)) { return false; }

        Inflected master = (Inflected) o;

        return new EqualsBuilder()
                .append(getInflectedHindi(), master.getInflectedHindi())
                .append(getCanonicalHindi(), master.getCanonicalHindi())
                .append(getPartOfSpeech(), master.getPartOfSpeech())
                .append(getAccidence(), master.getAccidence())
                .append(getCanonicalKeys(), master.getCanonicalKeys())
                .append(getInflectedHindiIndex(), master.getInflectedHindiIndex())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getInflectedHindi())
                .append(getPartOfSpeech())
                .append(getAccidence())
                .append(getCanonicalKeys())
                .append(getCanonicalHindi())
                .append(getInflectedHindiIndex())
                .toHashCode();
    }

    public String getKey(){
        return getInflectedHindi().concat("_").concat(Integer.toString(getInflectedHindiIndex()));
    }
}
