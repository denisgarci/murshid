package com.murshid.models;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
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
        canonicalKey.setWordIndex(safeIntFromAvMap(avMap, "word_index"));
        return canonicalKey;
    }

    public static <T extends Map> DictionaryKey  fromMap(T map){
        Preconditions.checkArgument(map.containsKey("hindi_word"), String.format("The dictionary key %s should contain hindi_word ", map));
        Preconditions.checkArgument(map.containsKey("word_index"), String.format("The dictionary key %s should contain word_index ", map));
        DictionaryKey canonicalKey = new DictionaryKey();
        canonicalKey.setHindiWord(map.get("hindi_word").toString());
        canonicalKey.setWordIndex(safeIntFromMap(map, "word_index"));
        return canonicalKey;
    }

    private static int safeIntFromAvMap(Map<String, AttributeValue> map, String fieldName){
        AttributeValue value = map.get(fieldName);
        if (value.getN() != null) {
            return Integer.valueOf(value.getN());
        }else  if (value.getS() != null){
            return Integer.valueOf(value.getS());
        }else{
            throw new IllegalArgumentException("Field name " + fieldName + " has a value " + value + " whose format is not supported");
        }
    }

    private static int safeIntFromMap(Map map, String fieldName){
        Object value = map.get(fieldName);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }else  if (value instanceof String){
            return Integer.valueOf((String)value);
        }else{
            throw new IllegalArgumentException("Field name " + fieldName + " has a value " + value + " whose format is not supported");
        }
    }

    public Map<String, Object>  toMap(){
        return ImmutableMap.of("hindi_word", hindiWord,
                "word_index", Integer.toString(wordIndex));
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
