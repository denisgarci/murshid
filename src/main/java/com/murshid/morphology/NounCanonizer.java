package com.murshid.morphology;

import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;

import java.util.HashSet;
import java.util.Set;

class NounCanonizer {

    /**
     * Given an inflected form, offers all possible canonical forms it can come from 
     * @param inflectedForm     the inflected form
     * @return                  a Set of possible canonical forms to look in the dictionaries, in addition to the given form.
     *                          From the CanonicalConstruct, only the temptative form matters for the moment.
     *                          We are not including gender in the accidence, so that the discovories are useful for both genders. This implies that gemder can't be part of the 
     *                          comparison when selecting forms in the dictionary databases.
     */
     static Set<CanonicalResult> process(String inflectedForm){
        Set<CanonicalResult> results = new HashSet<>();

        //masculine in -A
        if (inflectedForm.endsWith("ा")){
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet(Accidence.SINGULAR, Accidence.DIRECT)));
        }

        if (inflectedForm.endsWith("े")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ा");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SINGULAR, Accidence.OBLIQUE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SINGULAR, Accidence.VOCATIVE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.DIRECT)));
        }

        if (inflectedForm.endsWith("ों")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ा");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.OBLIQUE)));
        }

        if (inflectedForm.endsWith("ो")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ा");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.VOCATIVE)));
        }

        //masculines in -UU shortened it to -UÕ
        if (inflectedForm.endsWith("ू")){
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet(Accidence.SINGULAR, Accidence.DIRECT)));
        }
        if (inflectedForm.endsWith("ुओं")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ू");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.OBLIQUE, Accidence.PLURAL)));
        }

        //feminines in -I
        if (inflectedForm.endsWith("ी")){
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet( Accidence.SINGULAR, Accidence.DIRECT)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet( Accidence.SINGULAR, Accidence.OBLIQUE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet( Accidence.SINGULAR, Accidence.VOCATIVE)));
        }

        if (inflectedForm.endsWith("ियाँ") || inflectedForm.endsWith("ियां")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ी");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet( Accidence.PLURAL, Accidence.DIRECT)));
        }

        if (inflectedForm.endsWith("ियों")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ी");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet( Accidence.PLURAL, Accidence.OBLIQUE)));
        }

        if (inflectedForm.endsWith("ियो")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ी");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet( Accidence.PLURAL, Accidence.VOCATIVE)));
        }

        //masculines not ending in -A
        if (!inflectedForm.endsWith("ा")){
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet( Accidence.SINGULAR, Accidence.DIRECT)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet( Accidence.SINGULAR, Accidence.OBLIQUE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet( Accidence.SINGULAR, Accidence.VOCATIVE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet( Accidence.PLURAL, Accidence.DIRECT)));

        }
        if (inflectedForm.endsWith("ों")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2);
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet( Accidence.PLURAL, Accidence.OBLIQUE)));
        }

        if (inflectedForm.endsWith("ो")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1);
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet( Accidence.PLURAL, Accidence.VOCATIVE)));
        }

        //feminines not ending in -I
        if (!inflectedForm.endsWith("ी")){
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet( Accidence.SINGULAR, Accidence.DIRECT)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet( Accidence.SINGULAR, Accidence.OBLIQUE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(inflectedForm).setAccidence(Sets.newHashSet( Accidence.SINGULAR, Accidence.VOCATIVE)));
        }
        if (inflectedForm.endsWith("ें")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2);
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet( Accidence.PLURAL, Accidence.DIRECT)));
        }

        if (inflectedForm.endsWith("ों")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2);
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet( Accidence.PLURAL, Accidence.OBLIQUE)));
        }

        if (inflectedForm.endsWith("ो")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1);
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.NOUN)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet( Accidence.PLURAL, Accidence.VOCATIVE)));
        }

        return results;
    }
}
