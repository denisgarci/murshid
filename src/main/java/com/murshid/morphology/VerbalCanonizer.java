package com.murshid.morphology;

import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;

import java.util.HashSet;
import java.util.Set;

public class VerbalCanonizer {

    /**
     * Given an inflected form, offers all possible adjective verbal canonical forms it can come from (excepting itself)
     * @param inflectedForm     the inflected form
     * @return                  a Set of possible canonical forms to look in the dictionaries, in addition to the given form.
     *                          From the CanonicalConstruct, only the tentative form matters for the moment, so it would be
     *                          convenient to merge all resulting results by canonical form only.
     */
    public static Set<CanonicalResult> process(String inflectedForm){
        Set<CanonicalResult> results = new HashSet<>();
        Set<PartOfSpeech> verbSet  = Sets.newHashSet(PartOfSpeech.VERB);

        //imperative
        if (inflectedForm.endsWith("ो")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.FAMILIAR)));
        }
        if (inflectedForm.endsWith("िए")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.FORMAL)));
        }
        if (inflectedForm.endsWith("िये")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.FORMAL)));
        }

        {
            String canonicalForm = inflectedForm.concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm)
                                .setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.INTIMATE)));
        }

        //absolutive
        {
            String canonicalForm = inflectedForm.concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.ABSOLUTIVE)));
        }
        if (inflectedForm.endsWith("कर") || inflectedForm.endsWith("के")){
                String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
                results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                    .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.ABSOLUTIVE)));
        }

        //verbal noun
        if (inflectedForm.endsWith("नेवला") || inflectedForm.endsWith("के")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-5).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.ABSOLUTIVE)));
        }

        //subjunctive
        if (inflectedForm.endsWith("ऊँ") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.SINGULAR, Accidence._1ST, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.SINGULAR, Accidence._1ST, Accidence.FEMININE)));
        }

        if (inflectedForm.endsWith("ए") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.SINGULAR, Accidence._2ND)));
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.SINGULAR, Accidence._3RD, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.SINGULAR, Accidence._3RD, Accidence.FEMININE)));
        }

        if (inflectedForm.endsWith("ओ") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.PLURAL, Accidence._2ND)));
        }

        //future
        if (inflectedForm.endsWith("ूँगा") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._1ST, Accidence.MASCULINE)));
        }

        if (inflectedForm.endsWith("ेगा") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._2ND, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._3RD, Accidence.MASCULINE)));
        }

        if (inflectedForm.endsWith("ेंगे") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._1ST, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._3RD, Accidence.MASCULINE)));
        }

        if (inflectedForm.endsWith("ोगे") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._2ND, Accidence.MASCULINE)));
        }


        if (inflectedForm.endsWith("ूँगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._1ST, Accidence.FEMININE)));
        }


        if (inflectedForm.endsWith("ेगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._2ND, Accidence.FEMININE)));
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._3RD, Accidence.FEMININE)));
        }

        if (inflectedForm.endsWith("ेंगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._1ST, Accidence.FEMININE)));
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._3RD, Accidence.FEMININE)));
        }

        if (inflectedForm.endsWith("ोगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(verbSet)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._2ND, Accidence.FEMININE)));
        }


        return results;
    }
}
