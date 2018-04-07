package com.murshid.models.enums;

public enum PartOfSpeech {

    NOUN("Noun"), ADJECTIVE("Adjective"), VERB("Verb"), POSTPOSITION("Postposition"), ADVERB("Adverb"),
    CONJUNCTION("Conjunction"), PRONOUN("Pronoun"), INTERJECTION("Interjection"), LETTER("Letter"),
    NUMERAL("Numeral"), ORDINAL("Ordinal"),
    RELATIVE_PRONOUN("Relative pronoun"), PERSONAL_PRONOUN("Personal pronoun"),
    PROPER_NOUN("Proper noun"),
    INFINITIVE("Infinitive"), PARTICIPLE("Participle"), ABSOLUTIVE("Absolutive"), VERBAL_NOUN("Verbal noun"),
    SUFFIX("Suffix"), PREFIX("Prefix"),
    ACRONYM("Acronym"), DETERMINER("Determiner"), PROVERB("Proverb"), VERB_FORM("Verb form"), PARTICLE("Particle"),
    DIACRITIC("Diacritical mark"), PREPOSITION("Preposition"), ADVERBIAL_PHRASE("Adverbial phrase"),
    COMPOUND_POSTPOSITON("Compound postposition");

    private String label;

    PartOfSpeech(String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static PartOfSpeech fromLabel(String label){
        for (PartOfSpeech accidence : PartOfSpeech.values()){
            if (accidence.label.equals(label)){
                return accidence;
            }
        }
        throw new IllegalArgumentException("label " + label + " not supported");
    }
}
