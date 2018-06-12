package com.murshid.persistence.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.models.DictionaryRelationKey;
import com.murshid.models.enums.DictionarySource;

import javax.persistence.*;

@Entity(name = "dictionary_relations")
public class DictionaryRelations {

    @EmbeddedId
    private DictionaryRelationKey dictionaryRelationKey;

    @Column(name = "hindi_word_to")
    private String hindiWordTo;

    @Column(name = "hindi_word_index_to")
    private int hindiWordIndexTo;

    public int getHindiWordIndexTo() {
        return hindiWordIndexTo;
    }

    public void setHindiWordIndexTo(int hindiWordIndexTo) {
        this.hindiWordIndexTo = hindiWordIndexTo;
    }

    public DictionaryRelationKey getDictionaryRelationKey() {
        return dictionaryRelationKey;
    }

    public DictionaryRelations setDictionaryRelationKey(DictionaryRelationKey dictionaryRelationKey) {
        this.dictionaryRelationKey = dictionaryRelationKey;
        return this;
    }

    public String getHindiWordTo() {
        return hindiWordTo;
    }

    public DictionaryRelations setHindiWordTo(String hindiWordTo) {
        this.hindiWordTo = hindiWordTo;
        return this;
    }



    public String getHindiWord(){
        return this.dictionaryRelationKey.hindiWord;
    }

}
