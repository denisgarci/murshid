package com.murshid.models;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableMap;
import com.murshid.models.enums.DictionarySource;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Embeddable
public class DictionaryKey implements Serializable{

    @JsonProperty("hindi_word")
    @Column(name = "hindi_word", nullable = false)
    public String hindiWord;

    @JsonProperty("word_index")
    @Column(name = "word_index", nullable = false)
    public int wordIndex;

    public DictionaryKey setHindiWord(String hindiWord) {
        this.hindiWord = hindiWord;
        return this;
    }

    public DictionaryKey setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
        return this;
    }

    public String toString() {
        return hindiWord.concat("_").concat(Integer.toString(wordIndex));

    }

    public static DictionaryKey  fromAvMap(Map<String, AttributeValue> avMap){
        DictionaryKey canonicalKey = new DictionaryKey();
        canonicalKey.setHindiWord(avMap.get("hindi_word").getS());
        canonicalKey.setWordIndex(Integer.valueOf(avMap.get("word_index").getN()));
        return canonicalKey;
    }

    public static DictionaryKey  fromMap(Map<String, Object> map){
        DictionaryKey canonicalKey = new DictionaryKey();
        canonicalKey.setHindiWord(map.get("hindi_word").toString());
        canonicalKey.setWordIndex(((Double)(map.get("word_index"))).intValue());
        return canonicalKey;
    }

    public Map<String, Object>  toMap(){
        return ImmutableMap.of("hindi_word", hindiWord,
                "word_index", wordIndex);
    }

    public static Map<String, AttributeValue>  toAvMap(DictionaryKey dictionaryKey ){
        return ImmutableMap.of("hindi_word", new AttributeValue().withS(dictionaryKey.hindiWord),
                "word_index", new AttributeValue().withN(Integer.toString(dictionaryKey.wordIndex)));
    }


    public String getHindiWord() {
        return hindiWord;
    }

    public int getWordIndex() {
        return wordIndex;
    }


}
