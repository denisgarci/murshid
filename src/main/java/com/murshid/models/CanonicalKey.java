package com.murshid.models;


import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.murshid.models.enums.DictionarySource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
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
        if (avMap.containsKey("canonical_word"))
        canonicalKey.setCanonicalWord(avMap.get("canonical_word").getS());
        canonicalKey.setCanonicalIndex(Integer.valueOf(avMap.get("canonical_index").getN()));
        canonicalKey.setDictionarySource(DictionarySource.valueOf(avMap.get("dictionary_source").getS()));
        return canonicalKey;
    }

    public static CanonicalKey  fromMap(Map<String, Object> avMap){
        CanonicalKey canonicalKey = new CanonicalKey();
        canonicalKey.setCanonicalWord((String)avMap.get("canonical_word"));
        BigDecimal bdCi = (BigDecimal) avMap.get("canonical_index");
        canonicalKey.setCanonicalIndex(bdCi.intValue());
        canonicalKey.setDictionarySource(DictionarySource.valueOf((String)avMap.get("dictionary_source")));
        return canonicalKey;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getCanonicalWord())
                .append(getCanonicalIndex())
                .append(getDictionarySource())
                .toHashCode();
    }

    public String toKey(){
        return this.dictionarySource.name().concat("_")
                .concat(this.getCanonicalWord()).concat("_")
                .concat(Integer.toString(this.canonicalIndex));
    }
}
