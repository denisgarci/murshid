package com.murshid.morphology;

import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.Set;

public class CanonicalResult {

    String canonicalForm;

    Set<PartOfSpeech> possiblePOS;

    Set<Accidence> accidence = new HashSet<>();

    public Set<Accidence> getAccidence() {
        return accidence;
    }

    public CanonicalResult setAccidence(Set<Accidence> accidence) {
        this.accidence = accidence;
        return this;
    }

    public Set<PartOfSpeech> getPossiblePOS() {
        return possiblePOS;
    }

    public CanonicalResult setPossiblePOS(Set<PartOfSpeech> possiblePOS) {
        this.possiblePOS = possiblePOS;
        return this;
    }

    public String getCanonicalForm() {
        return canonicalForm;
    }

    public CanonicalResult setCanonicalForm(String canonicalForm) {
        this.canonicalForm = canonicalForm;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof CanonicalResult)) { return false; }

        CanonicalResult that = (CanonicalResult) o;

        return new EqualsBuilder()
                .append(getCanonicalForm(), that.getCanonicalForm())
                .append(getPossiblePOS(), that.getPossiblePOS())
                .append(getAccidence(), that.getAccidence())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getCanonicalForm())
                .append(getPossiblePOS())
                .append(getAccidence())
                .toHashCode();
    }
}
