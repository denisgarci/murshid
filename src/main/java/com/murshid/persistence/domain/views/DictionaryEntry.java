package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;

/**
 * A dictionary entry in a format easily displayable as part of a Json/ Javascript object
 */
public class DictionaryEntry {

    @SerializedName("dictionary_source")
    private DictionarySource dictionarySource;

    @SerializedName("part_of_speech")
    private PartOfSpeech partOfSpeech;

    @SerializedName("part_of_speech_label")
    private String partOfSpeechLabel;

    private String meaning;

    @SerializedName("hindi_word")
    private String hindiWord;

    @SerializedName("word_index")
    private int wordIndex;

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }

    @JsonProperty("dictionary_source")
    public DictionaryEntry setDictionarySource(DictionarySource dictionarySource) {
        this.dictionarySource = dictionarySource;
        return this;
    }

    @JsonProperty("part_of_speech_label")
    public String getPartOfSpeechLabel() {
        return partOfSpeechLabel;
    }

    @JsonProperty("part_of_speech")
    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public DictionaryEntry setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        if (partOfSpeech != null) {
            this.partOfSpeechLabel = partOfSpeech.getLabel();
        }
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
