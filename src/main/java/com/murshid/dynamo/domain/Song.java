package com.murshid.dynamo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.persistence.domain.views.WordListMasterEntry;

import javax.persistence.Column;
import java.util.List;
import java.util.Map;

public class Song {

    @JsonProperty("title_hindi")
    @Column(name = "title_hindi")
    private String titleHindi;

    @JsonProperty("title_latin")
    @Column(name = "title_latin")
    private String titleLatin;

    @JsonProperty("author")
    @Column(name = "author")
    private String author;

    @JsonProperty("media")
    @Column(name = "media")
    private List<String> media;

    @JsonProperty("song")
    @Column(name = "song")
    private String song;

    @JsonProperty("word_list")
    @Column(name = "word_list")
    private Map<String, String> wordList;

    @JsonProperty("word_list_master")
    @Column(name = "word_list_master")
    private List<WordListMasterEntry> wordListMaster;


    public String getTitleHindi() {
        return titleHindi;
    }

    public Song setTitleHindi(String titleHindi) {
        this.titleHindi = titleHindi;
        return this;
    }


    public String getTitleLatin() {
        return titleLatin;
    }

    public Song setTitleLatin(String titleLatin) {
        this.titleLatin = titleLatin;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Song setAuthor(String author) {
        this.author = author;
        return this;
    }

    public List<String> getMedia() {
        return media;
    }

    public Song setMedia(List<String> media) {
        this.media = media;
        return this;
    }

    public String getSong() {
        return song;
    }

    public Song setSong(String song) {
        this.song = song;
        return this;
    }

    public Map<String, String> getWordList() {
        return wordList;
    }

    public Song setWordList(Map<String, String> wordList) {
        this.wordList = wordList;
        return this;
    }

    public List<WordListMasterEntry> getWordListMaster() {
        return wordListMaster;
    }

    public Song setWordListMaster(List<WordListMasterEntry> wordListMaster) {
        this.wordListMaster = wordListMaster;
        return this;
    }

}

