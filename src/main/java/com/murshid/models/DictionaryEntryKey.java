package com.murshid.models;


import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.MasterDictionary;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class DictionaryEntryKey implements Serializable{

    @ManyToOne
    @JoinColumns({
            @JoinColumn(
                    name = "master_hindi_word",
                    referencedColumnName = "hindi_word"),
            @JoinColumn(
                    name = "master_word_index",
                    referencedColumnName = "word_index")
    })
    private MasterDictionary masterDictionary;

    @Column(name ="word_index")
    private int wordIndex;

    @Column(name ="dictionary_source")
    @Enumerated(EnumType.STRING)
    private DictionarySource dictionarySource;

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

    public MasterDictionary getMasterDictionary() {
        return masterDictionary;
    }

    public void setMasterDictionary(MasterDictionary masterDictionary) {
        this.masterDictionary = masterDictionary;
    }

}
