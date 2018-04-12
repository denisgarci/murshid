package com.murshid.models;


import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.murshid.models.enums.DictionarySource;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Map;

@Embeddable
public class CanonicalKey implements Serializable{

    @JsonProperty("canonical_word")
    @Column(name ="canonical_word")
    public String canonicalWord;

    @JsonProperty("canonical_index")
    @Column(name ="canonical_index")
    public int canonicalIndex;

    @JsonProperty("dictionary_source")
    @Column(name ="dictionary_source")
    @Enumerated(EnumType.STRING)
    public DictionarySource dictionarySource;

    public CanonicalKey setCanonicalWord(String canonicalWord) {
        this.canonicalWord = canonicalWord;
        return this;
    }

    public CanonicalKey setCanonicalIndex(int canonicalIndex) {
        this.canonicalIndex = canonicalIndex;
        return this;
    }

    public CanonicalKey setDictionarySource(DictionarySource dictionarySource) {
        this.dictionarySource = dictionarySource;
        return this;
    }

    public String getCanonicalWord() {
        return canonicalWord;
    }

    public int getCanonicalIndex() {
        return canonicalIndex;
    }

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }

    public Map<String, Object>  toMap(){
        return ImmutableMap.of("canonical_word", canonicalWord,
                               "canonical_index", canonicalIndex,
                               "dictionary_source", dictionarySource.name());
    }

    public static Map<String, AttributeValue>  toAvMap(CanonicalKey canonicalKey ){
        return ImmutableMap.of("canonical_word", new AttributeValue().withS(canonicalKey.canonicalWord),
                               "canonical_index", new AttributeValue().withN(Integer.toString(canonicalKey.canonicalIndex)),
                               "dictionary_source", new AttributeValue().withS(canonicalKey.dictionarySource.name()));
    }

    public static CanonicalKey  fromAvMap(Map<String, AttributeValue> avMap){
        CanonicalKey canonicalKey = new CanonicalKey();
        canonicalKey.setCanonicalWord(avMap.get("canonical_word").getS());
        canonicalKey.setCanonicalIndex(Integer.valueOf(avMap.get("canonical_index").getN()));
        canonicalKey.setDictionarySource(DictionarySource.valueOf(avMap.get("dictionary_source").getS()));
        return canonicalKey;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
