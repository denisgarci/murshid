package com.murshid.morphology;

import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.Set;

public class CanonicalResult {

    String canonicalForm;

    PartOfSpeech possiblePOS;

    Set<Accidence> accidence = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof CanonicalResult)) { return false; }

        CanonicalResult that = (CanonicalResult) o;

        if (!getCanonicalForm().equals(that.canonicalForm)){
            return false;
        }else if (!getPossiblePOS().equals(that.possiblePOS)){
            return false;
        }else if (!getAccidence().equals(that.accidence)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getCanonicalForm())
                .append(getPossiblePOS())
                .append(getAccidence())
                .toHashCode();
    }

    public String getCanonicalForm() {
        return canonicalForm;
    }

    public CanonicalResult setCanonicalForm(String canonicalForm) {
        this.canonicalForm = canonicalForm;
        return this;
    }

    public PartOfSpeech getPossiblePOS() {
        return possiblePOS;
    }

    public CanonicalResult setPossiblePOS(PartOfSpeech possiblePOS) {
        this.possiblePOS = possiblePOS;
        return this;
    }

    public Set<Accidence> getAccidence() {
        return accidence;
    }

    public CanonicalResult setAccidence(Set<Accidence> accidence) {
        this.accidence = accidence;
        return this;
    }


}
