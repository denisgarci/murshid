package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.DictionaryEntry;

/**
 * A dictionary entry in a format easily displayable as part of a Json/ Javascript object
 */
public class DictionaryEntryView {

    @SerializedName("dictionary_source")
    private DictionarySource dictionarySource;

    @SerializedName("part_of_speech")
    private PartOfSpeech partOfSpeech;

    private String meaning;

    @SerializedName("hindi_word")
    private String hindiWord;

    @SerializedName("word_index")
    private int wordIndex;

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }

    @JsonProperty("dictionary_source")
    public DictionaryEntryView setDictionarySource(DictionarySource dictionarySource) {
        this.dictionarySource = dictionarySource;
        return this;
    }

    @JsonProperty("part_of_speech")
    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public DictionaryEntryView setPartOfSpeech(PartOfSpeech partOfSpeech) {
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

    public DictionaryEntryView setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public DictionaryEntryView setHindiWord(String hindiWord) {
        this.hindiWord = hindiWord;
        return this;
    }

    public DictionaryEntryView setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
        return this;
    }


}
