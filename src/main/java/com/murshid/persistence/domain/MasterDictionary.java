package com.murshid.persistence.domain;

import com.murshid.models.CanonicalKey;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.AccidenceColumnConverter;
import com.murshid.services.IDictionaryEntry;

import javax.persistence.*;
import java.util.List;

@Entity(name = "master_dictionary")
public class MasterDictionary  {


    @Id
    @Column(name ="id")
    private int id;

    @Column(name ="hindi_word")
    private String hindiWord;

    @Column(name ="word_index")
    private int wordIndex;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = true)
    private PartOfSpeech partOfSpeech;

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public MasterDictionary setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
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

    public void setHindiWord(String hindiWord) {
        this.hindiWord = hindiWord;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public void setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }
}
