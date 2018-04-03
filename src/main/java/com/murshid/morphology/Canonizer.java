package com.murshid.morphology;

import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;

import java.util.HashSet;
import java.util.Set;

public class Canonizer {

    public static Set<CanonicalResult> process(String inflectedForm){
        Set<CanonicalResult> results = new HashSet<>();

        if (inflectedForm.endsWith("े")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ा");
            Set<PartOfSpeech> nounAdjetive = Sets.newHashSet(PartOfSpeech.NOUN, PartOfSpeech.ADJECTIVE);
            results.add(new CanonicalResult().setPossiblePOS(nounAdjetive)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SINGULAR, Accidence.OBLIQUE)));
            results.add(new CanonicalResult().setPossiblePOS(nounAdjetive)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SINGULAR, Accidence.VOCATIVE)));
            results.add(new CanonicalResult().setPossiblePOS(nounAdjetive)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.DIRECT)));
        }

        return results;
    }
}
