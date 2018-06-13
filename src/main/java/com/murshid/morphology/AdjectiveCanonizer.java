package com.murshid.morphology;

import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;

import java.util.HashSet;
import java.util.Set;

public class AdjectiveCanonizer {

    /**
     * Given an inflected form, offers all possible adjective canonical forms it can come from (excepting itself)
     * @param inflectedForm     the inflected form
     * @return                  a Set of possible canonical forms to look in the dictionaries, in addition to the given form.
     *                          From the CanonicalConstruct, only the tentative form matters for the moment, so it would be
     *                          convenient to merge all resulting results by canonical form only.
     */
     static Set<CanonicalResult> process(String inflectedForm){
        Set<CanonicalResult> results = new HashSet<>();

        //masculine in -A
        if (inflectedForm.endsWith("े")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ा");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.ADJECTIVE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SINGULAR, Accidence.OBLIQUE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.ADJECTIVE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SINGULAR, Accidence.VOCATIVE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.ADJECTIVE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.DIRECT)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.ADJECTIVE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.OBLIQUE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.ADJECTIVE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.VOCATIVE)));
        }

        //feminine adjectives in -I don't need reverse lookup
        //invariable adjectives don't need reverse lookip

        return results;
    }
}
