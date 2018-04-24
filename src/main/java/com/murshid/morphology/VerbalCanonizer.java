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

        //infinitive
        if (inflectedForm.endsWith("ना")){
            String canonicalForm = inflectedForm;
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.INFINITIVE).setCanonicalForm(canonicalForm));
        }

        //imperative
        if (inflectedForm.endsWith("ो")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.PLURAL, Accidence._2ND)));
        }
        if (inflectedForm.endsWith("िए")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.PLURAL, Accidence._3RD)));
        }
        if (inflectedForm.endsWith("िये")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.PLURAL, Accidence._3RD)));
        }

        {
            String canonicalForm = inflectedForm.concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm)
                                .setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.SINGULAR, Accidence._2ND)));
        }

        //absolutives
        {
            String canonicalForm = inflectedForm.concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.ABSOLUTIVE)
                                .setCanonicalForm(canonicalForm));
        }
        if (inflectedForm.endsWith("कर") || inflectedForm.endsWith("के")){
                String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
                results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.ABSOLUTIVE)
                                    .setCanonicalForm(canonicalForm));
        }

        //verbal nouns
        if (inflectedForm.endsWith("नेवला")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-5).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERBAL_NOUN)
                                .setCanonicalForm(canonicalForm));
        }

        if ( inflectedForm.endsWith("के")){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERBAL_NOUN)
                                .setCanonicalForm(canonicalForm));
        }

        //subjunctive
        if (inflectedForm.endsWith("ऊँ") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.SINGULAR, Accidence._1ST, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.SINGULAR, Accidence._1ST, Accidence.FEMININE)));
        }

        if (inflectedForm.endsWith("ए") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.SINGULAR, Accidence._2ND)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.SINGULAR, Accidence._3RD, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.SINGULAR, Accidence._3RD, Accidence.FEMININE)));
        }

        if (inflectedForm.endsWith("ओ") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.SUBJUNCTIVE, Accidence.PLURAL, Accidence._2ND)));
        }

        //future
        if (inflectedForm.endsWith("ूँगा") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._1ST, Accidence.MASCULINE)));
        }
       //finished in UUNA (छूना becomes छुऊँगा)
        if (inflectedForm.endsWith("ुऊँगा") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-5).concat("ूना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._1ST, Accidence.MASCULINE)));
        }
        //finished in IINA (पीना becomes पिऊँगा)
        if (inflectedForm.endsWith("िऊँगा") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-5).concat("ीना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._1ST, Accidence.MASCULINE)));
        }

        if (inflectedForm.endsWith("ेगा") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._2ND, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._3RD, Accidence.MASCULINE)));
        }
        //finished in UUNA (छूना becomes छुएगा)
        if (inflectedForm.endsWith("ुएगा") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ूना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._2ND, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._3RD, Accidence.MASCULINE)));
        }
        //finished in IINA (पीना becomes पिएगा)
        if (inflectedForm.endsWith("िएगा") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ीना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._2ND, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._3RD, Accidence.MASCULINE)));
        }


        if (inflectedForm.endsWith("ेंगे") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._1ST, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._3RD, Accidence.MASCULINE)));
        }
        //finished in UUNA (छूना becomes छुएँगे)
        if (inflectedForm.endsWith("ुएँगे") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-5).concat("ूना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._1ST, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._3RD, Accidence.MASCULINE)));
        }
        //finished in IINA (पीना becomes पिएँगे)
        if (inflectedForm.endsWith("िएँगे") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-5).concat("ीना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._1ST, Accidence.MASCULINE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._3RD, Accidence.MASCULINE)));
        }

        if (inflectedForm.endsWith("ोगे") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._2ND, Accidence.MASCULINE)));
        }
        //finished in UUNA (छूना becomes छुओगे)
        if (inflectedForm.endsWith("ुओगे") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ूना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._2ND, Accidence.MASCULINE)));
        }
        //finished in IINA (पीना becomes पिओगे)
        if (inflectedForm.endsWith("िओगे") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ीना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._2ND, Accidence.MASCULINE)));
        }


        if (inflectedForm.endsWith("ूँगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._1ST, Accidence.FEMININE)));
        }
        //finished in UUNA (छूना becomes छुऊँगी)
        if (inflectedForm.endsWith("ुऊँगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-5).concat("ूना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._1ST, Accidence.FEMININE)));
        }
        //finished in IINA (पीना becomes पिऊँगी)
        if (inflectedForm.endsWith("िऊँगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-5).concat("ीना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._1ST, Accidence.MASCULINE)));
        }


        if (inflectedForm.endsWith("ेगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._2ND, Accidence.FEMININE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._3RD, Accidence.FEMININE)));
        }
        //finished in UUNA (छूना becomes छुएगी)
        if (inflectedForm.endsWith("ुएगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ूना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._2ND, Accidence.FEMININE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._3RD, Accidence.FEMININE)));
        }
        //finished in IINA (पीना becomes पिएगी)
        if (inflectedForm.endsWith("िएगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ीना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._2ND, Accidence.FEMININE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR, Accidence._3RD, Accidence.FEMININE)));
        }

        if (inflectedForm.endsWith("ेंगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._1ST, Accidence.FEMININE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._3RD, Accidence.FEMININE)));
        }
        //finished in UUNA (छूना becomes छुएँगी)
        if (inflectedForm.endsWith("ुएँगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-5).concat("ूना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._1ST, Accidence.FEMININE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._3RD, Accidence.FEMININE)));
        }
        //finished in IINA (पीना becomes पिएँगी)
        if (inflectedForm.endsWith("िएँगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-5).concat("ीना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._1ST, Accidence.FEMININE)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._3RD, Accidence.FEMININE)));
        }

        if (inflectedForm.endsWith("ोगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._2ND, Accidence.FEMININE)));
        }
        //finished in UUNA (छूना becomes छुओगी)
        if (inflectedForm.endsWith("ुओगी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ूना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._2ND, Accidence.FEMININE)));
        }
        //finished in IINA (पीना becomes पिओगी)
        if (inflectedForm.endsWith("िओगी ") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-4).concat("ीना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL, Accidence._2ND, Accidence.FEMININE)));
        }

        //participle - imperfective
        if (inflectedForm.endsWith("ता") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERFECTIVE, Accidence.MASCULINE, Accidence.SINGULAR)));
        }

        if (inflectedForm.endsWith("ती") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERFECTIVE, Accidence.FEMININE, Accidence.SINGULAR)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERFECTIVE, Accidence.FEMININE, Accidence.PLURAL)));

        }

        if (inflectedForm.endsWith("ते") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERFECTIVE, Accidence.MASCULINE, Accidence.PLURAL)));
        }

        if (inflectedForm.endsWith("तीं") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-3).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.IMPERFECTIVE, Accidence.FEMININE, Accidence.PLURAL)));
        }

        //participle - perfective
        if (inflectedForm.endsWith("ा") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.MASCULINE, Accidence.SINGULAR)));
        }

        if (inflectedForm.endsWith("े") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.MASCULINE, Accidence.PLURAL)));
        }

        if (inflectedForm.endsWith("ी") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-1).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.FEMININE, Accidence.SINGULAR)));
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.FEMININE, Accidence.PLURAL)));
        }

        if (inflectedForm.endsWith("ीं") ){
            String canonicalForm = inflectedForm.substring(0, inflectedForm.length()-2).concat("ना");
            results.add(new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                                .setCanonicalForm(canonicalForm).setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.FEMININE, Accidence.PLURAL)));
        }

        return results;
    }
}
