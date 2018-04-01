package com.murshid.models;


import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.ImmutableMap;
import com.murshid.models.enums.DictionarySource;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Map;

@Embeddable
public class CanonicalKey implements Serializable{
    @Column(name = "canonical_word", nullable = true)
    public String word;

    @Column(name = "canonical_word_index", nullable = true)
    public int wordIndex;


    @Enumerated
    @Column(name ="dictionary_source", nullable = true)
    public DictionarySource dictionarySource;

    public CanonicalKey setWord(String word) {
        this.word = word;
        return this;
    }

    public CanonicalKey setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
        return this;
    }

    public CanonicalKey setDictionarySource(DictionarySource dictionarySource) {
        this.dictionarySource = dictionarySource;
        return this;
    }

    public String getWord() {
        return word;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }

    public Map<String, Object>  toMap(){
        return ImmutableMap.of("canonical_word", word,
                               "canonical_index", wordIndex,
                               "dictionary_source", dictionarySource.name());
    }

    public static Map<String, AttributeValue>  toAvMap(CanonicalKey canonicalKey ){
        return ImmutableMap.of("canonical_word", new AttributeValue().withS(canonicalKey.word),
                               "canonical_index", new AttributeValue().withN(Integer.toString(canonicalKey.wordIndex)),
                               "dictionary_source", new AttributeValue().withS(canonicalKey.dictionarySource.name()));
    }

    public static CanonicalKey  fromAvMap(Map<String, AttributeValue> avMap){
        CanonicalKey canonicalKey = new CanonicalKey();
        canonicalKey.setWord(avMap.get("canonical_word").getS());
        canonicalKey.setWordIndex(Integer.valueOf(avMap.get("canonical_index").getN()));
        canonicalKey.setDictionarySource(DictionarySource.valueOf(avMap.get("dictionary_source").getS()));
        return canonicalKey;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
