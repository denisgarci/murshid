package com.murshid.persistence.domain;

import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.PartOfSpeech;

import javax.persistence.*;

@Entity(name = "pratts")
public class PrattsEntry {

    @Id @GeneratedValue
    @Column(name = "id", nullable = false)
    private int id;

    private int section;

    private int entry;

    @Column(name = "keystring", nullable = false)
    private String keystring;


    @Column(name = "head", nullable = false)
    private String head;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "latin", nullable = true)
    private String latinWord;

    @Column(name = "urdu", nullable = true)
    private String urduWord;

    @Embedded
    private DictionaryKey dictionaryKey;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = true)
    private PartOfSpeech partOfSpeech;

    public int getId() {
        return id;
    }

    public PrattsEntry setId(int id) {
        this.id = id;
        return this;
    }

    public int getSection() {
        return section;
    }

    public PrattsEntry setSection(int section) {
        this.section = section;
        return this;
    }

    public int getEntry() {
        return entry;
    }

    public PrattsEntry setEntry(int entry) {
        this.entry = entry;
        return this;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public PrattsEntry setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }

    public String getBody() {
        return body;
    }

    public PrattsEntry setBody(String body) {
        this.body = body;
        return this;
    }

    public String getLatinWord() {
        return latinWord;
    }

    public PrattsEntry setLatinWord(String latinWord) {
        this.latinWord = latinWord;
        return this;
    }

    public String getUrduWord() {
        return urduWord;
    }

    public PrattsEntry setUrduWord(String urduWord) {
        this.urduWord = urduWord;
        return this;
    }

    public String getKeystring() {
        return keystring;
    }

    public PrattsEntry setKeystring(String keystring) {
        this.keystring = keystring;
        return this;
    }

    public String getHead() {
        return head;
    }

    public PrattsEntry setHead(String head) {
        this.head = head;
        return this;
    }

    public DictionaryKey getDictionaryKey() {
        return dictionaryKey;
    }

    public PrattsEntry setDictionaryKey(DictionaryKey dictionaryKey) {
        this.dictionaryKey = dictionaryKey;
        return this;
    }

}
