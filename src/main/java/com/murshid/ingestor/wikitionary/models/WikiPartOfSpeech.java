package com.murshid.ingestor.wikitionary.models;

public enum WikiPartOfSpeech {

    NOUN("Noun"), ADJECTIVE("Adjective"), VERB("Verb"), POSTPOSITION("Postposition"), ADVERB("Adverb"),
    CONJUNCTION("Conjunction"), PRONOUN("Pronoun"), INTERJECTION("Interjection"), LETTER("Letter"), NUMERAL("Numeral"),
    PROPER_NOUN("Proper noun"), PERFECT_PARTICIPLE("Perfect participle"), SUFFIX("Suffix"), PREFIX("Prefix"),
    ACRONYM("Acronym"), DETERMINER("Determiner"), PROVERB("Proverb"), VERB_FORM("Verb form"), PARTICLE("Particle"),
    DIACRITIC("Diacritical mark"), PREPOSITION("Preposition");

    private String label;

    WikiPartOfSpeech(String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static WikiPartOfSpeech fromLabel(String label){
        for (WikiPartOfSpeech accidence : WikiPartOfSpeech.values()){
            if (accidence.label.equals(label)){
                return accidence;
            }
        }
        throw new IllegalArgumentException("label " + label + " not supported");
    }
}
