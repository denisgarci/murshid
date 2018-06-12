package com.murshid.models;


import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.murshid.models.enums.DictionarySource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    private String canonicalWord;

    @JsonProperty("canonical_index")
    @Column(name ="canonical_index")
    private int canonicalIndex;

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

    private String getCanonicalWord() {
        return canonicalWord;
    }

    private int getCanonicalIndex() {
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
        canonicalKey.setCanonicalIndex(Integer.valueOf((String)avMap.get("canonical_index")));
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
        Preconditions.checkNotNull(this.dictionarySource, "dictionary source cannot be null");
        Preconditions.checkNotNull(this.getCanonicalWord(), "canonical word cannot be null");

        return this.dictionarySource.name().concat("_")
                .concat(this.getCanonicalWord()).concat("_")
                .concat(Integer.toString(this.canonicalIndex));
    }
}
