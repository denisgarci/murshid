package com.murshid.persistence.domain;

import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.services.HasDictionaryKey;

import javax.persistence.*;

@Entity(name = "platts")
public class PlattsEntry implements HasDictionaryKey {

    @EmbeddedId
    private DictionaryKey dictionaryKey;

    private int section;

    private int entry;

    @Column(name = "keystring", nullable = false)
    private String keystring;

    @Column(name = "head", nullable = false)
    private String head;

    @Column(name = "meaning", nullable = false)
    private String meaning;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "latin", nullable = true)
    private String latinWord;

    @Column(name = "urdu", nullable = true)
    private String urduWord;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = true)
    private PartOfSpeech partOfSpeech;

    public int getSection() {
        return section;
    }

    public PlattsEntry setSection(int section) {
        this.section = section;
        return this;
    }

    public int getEntry() {
        return entry;
    }

    public PlattsEntry setEntry(int entry) {
        this.entry = entry;
        return this;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public PlattsEntry setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public PlattsEntry setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public String getLatinWord() {
        return latinWord;
    }

    public PlattsEntry setLatinWord(String latinWord) {
        this.latinWord = latinWord;
        return this;
    }

    public String getUrduWord() {
        return urduWord;
    }

    public PlattsEntry setUrduWord(String urduWord) {
        this.urduWord = urduWord;
        return this;
    }

    public String getKeystring() {
        return keystring;
    }

    public PlattsEntry setKeystring(String keystring) {
        this.keystring = keystring;
        return this;
    }

    public String getHead() {
        return head;
    }

    public PlattsEntry setHead(String head) {
        this.head = head;
        return this;
    }

    public DictionaryKey getDictionaryKey() {
        return dictionaryKey;
    }

    public PlattsEntry setDictionaryKey(DictionaryKey dictionaryKey) {
        this.dictionaryKey = dictionaryKey;
        return this;
    }

    public String getOrigin() {
        return origin;
    }

    public PlattsEntry setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    @Override
    public String getStringKey() {
        return DictionarySource.PLATTS.name()
                .concat("_")
                .concat(getHindiWord())
                .concat("_")
                .concat(Integer.toString(getWordIndex()));
    }


    @Override
    public String getHindiWord() {
        return dictionaryKey.hindiWord;
    }

    @Override
    public String getMeaning() {
        return meaning;
    }

    @Override
    public int getWordIndex() {
        return dictionaryKey.wordIndex;
    }
}
