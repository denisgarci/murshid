package com.murshid.models;


import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.ImmutableMap;
import com.murshid.models.enums.DictionarySource;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Map;

@Embeddable
public class CanonicalKey implements Serializable{

    public String hindiWord;

    public int wordIndex;

    @Enumerated
    public DictionarySource dictionarySource;

    public CanonicalKey setHindiWord(String hindiWord) {
        this.hindiWord = hindiWord;
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

    public String getHindiWord() {
        return hindiWord;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }

    public Map<String, Object>  toMap(){
        return ImmutableMap.of("hindiWord", hindiWord,
                               "wordIndex", wordIndex,
                               "dictionarySource", dictionarySource.name());
    }

    public static Map<String, AttributeValue>  toAvMap(CanonicalKey canonicalKey ){
        return ImmutableMap.of("hindiWord", new AttributeValue().withS(canonicalKey.hindiWord),
                               "wordIndex", new AttributeValue().withN(Integer.toString(canonicalKey.wordIndex)),
                               "dictionarySource", new AttributeValue().withS(canonicalKey.dictionarySource.name()));
    }

    public static CanonicalKey  fromAvMap(Map<String, AttributeValue> avMap){
        CanonicalKey canonicalKey = new CanonicalKey();
        canonicalKey.setHindiWord(avMap.get("hindiWord").getS());
        canonicalKey.setWordIndex(Integer.valueOf(avMap.get("wordIndex").getN()));
        canonicalKey.setDictionarySource(DictionarySource.valueOf(avMap.get("dictionarySource").getS()));
        return canonicalKey;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
