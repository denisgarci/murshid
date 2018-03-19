package com.murshid.ingestor.shabdkosh;

public enum PartOfSpeech {
    ADJECTIVE("adjective"), ADVERB("adverb"), VERB("verb"), CONJUNCTION("conjunction"), PREPOSITION("preposition"),
    INTERJECTION("interjection"), PRONOUN("pronoun"), PHRASE("phrase"), NOUN("noun");

    private String descr;

    PartOfSpeech(String descr){
        this.descr = descr;
    }


    @Override
    public String toString() {
        return descr;
    }
}
