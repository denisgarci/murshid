package com.murshid.dynamo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.persistence.domain.views.SongWordsToInflectedTable;

import javax.persistence.Column;
import java.util.List;

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

    @JsonProperty("word_list_master")
    @Column(name = "word_list_master")
    private List<SongWordsToInflectedTable> wordListMaster;

    @JsonProperty("html")
    @Column(name = "html")
    private String html;

    @JsonProperty("dictionary_entries")
    @Column(name = "dictionary_entries")
    private String dictionaryEntries;



    @JsonProperty("inflected_entries")
    @Column(name = "inflected_entries")
    private String inflectedEntries;


    public String getInflectedEntries() {
        return inflectedEntries;
    }

    public Song setInflectedEntries(String inflectedEntries) {
        this.inflectedEntries = inflectedEntries;
        return this;
    }

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

    public List<SongWordsToInflectedTable> getWordListMaster() {
        return wordListMaster;
    }

    public Song setWordListMaster(List<SongWordsToInflectedTable> wordListMaster) {
        this.wordListMaster = wordListMaster;
        return this;
    }

    public String getHtml() {
        return html;
    }

    public Song setHtml(String html) {
        this.html = html;
        return this;
    }

    public String getDictionaryEntries() {
        return dictionaryEntries;
    }

    public Song setDictionaryEntries(String dictionaryEntries) {
        this.dictionaryEntries = dictionaryEntries;
        return this;
    }

}

