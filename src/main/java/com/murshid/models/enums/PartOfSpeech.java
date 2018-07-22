package com.murshid.models.enums;

public enum PartOfSpeech {

    NOUN("Noun"), ADJECTIVE("Adjective"), VERB("Verb"), POSTPOSITION("Postposition"), ADVERB("Adverb"),
    CONJUNCTION("Conjunction"), PRONOUN("Pronoun"), INTERJECTION("Interjection"), LETTER("Letter"),
    NUMERAL("Numeral"), ORDINAL("Ordinal"),
    RELATIVE_PRONOUN("Relative pronoun"), PERSONAL_PRONOUN("Personal pronoun"), POSSESSIVE_PRONOUN("Possessive pronoun"), DEMONSTRATIVE_PRONOUN("Demonstrative pronoun"),
    PROPER_NOUN("Proper noun"),
    INFINITIVE("Infinitive"), PARTICIPLE("Participle"), VERBAL_NOUN("Verbal noun"),
    SUFFIX("Suffix"), PREFIX("Prefix"),
    ACRONYM("Acronym"), DETERMINER("Determiner"), PROVERB("Proverb"), VERB_FORM("Verb form"), PARTICLE("Particle"),
    DIACRITIC("Diacritical mark"), PREPOSITION("Preposition"), ADVERBIAL_PHRASE("Adverbial phrase"), ADJECTIVAL_PHRASE("Adjectival phrase"), NOMINAL_PHRASE("Nominal phrase"),
    PHRASE("Phrase"),
    COMPOUND_POSTPOSITION("Compound postposition");

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
