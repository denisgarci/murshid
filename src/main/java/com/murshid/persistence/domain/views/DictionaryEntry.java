package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;

/**
 * A dictionary entry in a format easily displayable as part of a Json/ Javascript object
 */
public class DictionaryEntry {

    private DictionarySource dictionarySource;

    private PartOfSpeech partOfSpeech;

    private String meaning;

    private String hindiWord;

    private int wordIndex;

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }

    @JsonProperty("dictionary_source")
    public DictionaryEntry setDictionarySource(DictionarySource dictionarySource) {
        this.dictionarySource = dictionarySource;
        return this;
    }

    @JsonProperty("part_of_speech")
    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public DictionaryEntry setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    @JsonProperty("meaning")
    public String getMeaning() {
        return meaning;
    }

    @JsonProperty("hindi_word")
    public String getHindiWord() {
        return hindiWord;
    }


    @JsonProperty("word_index")
    public int getWordIndex() {
        return wordIndex;
    }

    public DictionaryEntry setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public DictionaryEntry setHindiWord(String hindiWord) {
        this.hindiWord = hindiWord;
        return this;
    }

    public DictionaryEntry setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
        return this;
    }

}
