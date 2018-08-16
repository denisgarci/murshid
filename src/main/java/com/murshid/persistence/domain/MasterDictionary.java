package com.murshid.persistence.domain;

import com.murshid.models.CanonicalKey;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.AccidenceColumnConverter;
import com.murshid.services.IDictionaryEntry;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "master_dictionary")
public class MasterDictionary  {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id")
    private int id;

    @Column(name ="hindi_word")
    private String hindiWord;

    @Column(name ="word_index")
    private int wordIndex;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = true)
    private PartOfSpeech partOfSpeech;

    @OneToMany(mappedBy = "masterDictionary", cascade = CascadeType.ALL)
    private List<DictionaryEntry> dictionaryEntries = new ArrayList<>();

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public MasterDictionary setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public MasterDictionary     addDictionaryEntry(DictionaryEntry dictionaryEntry) {
        dictionaryEntry.setMasterDictionary(this);
        dictionaryEntries.add(dictionaryEntry);
        return this;
    }

    public MasterDictionary removeDictionaryEntry(DictionaryEntry dictionaryEntry) {
        dictionaryEntry.setMasterDictionary(null);
        dictionaryEntries.remove(dictionaryEntry);
        return this;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHindiWord() {
        return hindiWord;
    }

    public MasterDictionary setHindiWord(String hindiWord) {
        this.hindiWord = hindiWord;
        return this;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public MasterDictionary setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
        return this;
    }
}
