package com.murshid.dynamo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class NotInflected {

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

    @JsonProperty("canonical_keys")
    @Column(name = "canonical_keys")
    private Set<CanonicalKey> canonicalKeys;

    public Set<CanonicalKey> getCanonicalKeys() {
        return canonicalKeys;
    }

    public NotInflected setCanonicalKeys(Set<CanonicalKey> canonicalKeys) {
        this.canonicalKeys = canonicalKeys;
        return this;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof NotInflected)) { return false; }

        NotInflected master = (NotInflected) o;

        return new EqualsBuilder()
                .append(getHindi(), master.getHindi())
                .append(getPartOfSpeech(), master.getPartOfSpeech())
                .append(getCanonicalKeys(), master.getCanonicalKeys())
                .append(getHindiIndex(), master.getHindiIndex())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getHindi())
                .append(getPartOfSpeech())
                .append(getCanonicalKeys())
                .append(getHindiIndex())
                .toHashCode();
    }

    public String getKey(){
        return getHindi().concat("_").concat(Integer.toString(getHindiIndex()));
    }
}
