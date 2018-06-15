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
import java.math.BigDecimal;
import java.util.Map;

@Embeddable
public class DictionaryRelationKey implements Serializable{

    @JsonProperty("hindi_word")
    @Column(name ="hindi_word")
    public String hindiWord;

    @JsonProperty("hindi_word_index")
    @Column(name ="hindi_word_index")
    public int hindiWordIndex;

    public DictionaryRelationKey setHindiWord(String hindiWordIndex) {
        this.hindiWord = hindiWordIndex;
        return this;
    }

    public DictionaryRelationKey setHindiWordIndex(int hindiWordIndex) {
        this.hindiWordIndex = hindiWordIndex;
        return this;
    }

    public String getHindiWord() {
        return hindiWord;
    }

    public int getHindiWordIndex() {
        return hindiWordIndex;
    }

    public Map<String, Object>  toMap(){
        return ImmutableMap.of("hindi_word", hindiWord,
                               "hindi_word_index", hindiWordIndex);
    }

    public static Map<String, AttributeValue>  toAvMap(DictionaryRelationKey canonicalKey ){
        return ImmutableMap.of("hindi_word", new AttributeValue().withS(canonicalKey.hindiWord),
                               "hindi_wprd_index", new AttributeValue().withN(Integer.toString(canonicalKey.hindiWordIndex)));
    }

    public static DictionaryRelationKey fromAvMap(Map<String, AttributeValue> avMap){
        DictionaryRelationKey canonicalKey = new DictionaryRelationKey();
        if (avMap.containsKey("hindi_word"))
        canonicalKey.setHindiWord(avMap.get("hindi_word").getS());
        canonicalKey.setHindiWordIndex(Integer.valueOf(avMap.get("hindi_word_index").getN()));
        return canonicalKey;
    }

    public static DictionaryRelationKey fromMap(Map<String, Object> avMap){
        DictionaryRelationKey canonicalKey = new DictionaryRelationKey();
        canonicalKey.setHindiWord((String)avMap.get("hindi_word"));
        BigDecimal bdCi = (BigDecimal) avMap.get("hindi_word_index");
        canonicalKey.setHindiWordIndex(bdCi.intValue());
        return canonicalKey;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getHindiWord())
                .append(getHindiWordIndex())
                .toHashCode();
    }
}
