package com.murshid.persistence.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.models.DictionaryEntryKey;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.MasterDictionary;

import javax.persistence.*;

@Entity(name = "dictionary_entries")
public class DictionaryEntry {

    @Id
    @Column(name ="id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "master_dictionary_id")
    public MasterDictionary masterDictionary;

    @Column(name ="word_index")
    private int wordIndex;

    @Column(name ="dictionary_source")
    @Enumerated(EnumType.STRING)
    private DictionarySource dictionarySource;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MasterDictionary getMasterDictionary() {
        return masterDictionary;
    }

    public void setMasterDictionary(MasterDictionary masterDictionary) {
        this.masterDictionary = masterDictionary;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public void setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }

    public void setDictionarySource(DictionarySource dictionarySource) {
        this.dictionarySource = dictionarySource;
    }

    public String toString(){
        return dictionarySource.name().concat("_")
                .concat(masterDictionary.getHindiWord()
                .concat("_")
                .concat(Integer.toString(wordIndex)));
    }



}
