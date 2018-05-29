package com.murshid.dynamo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.persistence.domain.views.SongWordsToInflectedTable;
import com.murshid.persistence.domain.views.SongWordsToNotInflectedTable;

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

    @JsonProperty("english_translation")
    @Column(name = "english_translation")
    private String englishTranslation;

    @JsonProperty("english_translation_html")
    @Column(name = "english_translation_html")
    private String englishTranslationHtml;


    @JsonProperty("word_list_master")
    @Column(name = "word_list_master")
    private List<SongWordsToInflectedTable> wordListMaster;

    @JsonProperty("word_list_not_inflected")
    @Column(name = "word_list_not_inflected")
    private List<SongWordsToNotInflectedTable> wordListNotInflected;


    @JsonProperty("html")
    @Column(name = "html")
    private String html;

    @JsonProperty("dictionary_entries_inflected")
    @Column(name = "dictionary_entries_inflected")
    private String dictionaryEntriesInflected;

    @JsonProperty("dictionary_entries_not_inflected")
    @Column(name = "dictionary_entries_not_inflected")
    private String dictionaryEntriesNotInflected;

    @JsonProperty("inflected_entries")
    @Column(name = "inflected_entries")
    private String inflectedEntries;

    @JsonProperty("not_inflected_entries")
    @Column(name = "not_inflected_entries")
    private String notInflectedEntries;

    public String getNotInflectedEntries() {
        return notInflectedEntries;
    }

    public void setNotInflectedEntries(String notInflectedEntries) {
        this.notInflectedEntries = notInflectedEntries;
    }

    public String getDictionaryEntriesNotInflected() {
        return dictionaryEntriesNotInflected;
    }

    public void setDictionaryEntriesNotInflected(String dictionaryEntriesNotInflected) {
        this.dictionaryEntriesNotInflected = dictionaryEntriesNotInflected;
    }

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

    public String getDictionaryEntriesInflected() {
        return dictionaryEntriesInflected;
    }

    public Song setDictionaryEntriesInflected(String dictionaryEntriesInflected) {
        this.dictionaryEntriesInflected = dictionaryEntriesInflected;
        return this;
    }

    public String getEnglishTranslation() {
        return englishTranslation;
    }

    public Song setEnglishTranslation(String englishTranslation) {
        this.englishTranslation = englishTranslation;
        return this;
    }

    public String getEnglishTranslationHtml() {
        return englishTranslationHtml;
    }

    public Song setEnglishTranslationHtml(String englishTranslationHtml) {
        this.englishTranslationHtml = englishTranslationHtml;
        return this;
    }

    public List<SongWordsToNotInflectedTable> getWordListNotInflected() {
        return wordListNotInflected;
    }

    public void setWordListNotInflected(List<SongWordsToNotInflectedTable> wordListNotInflected) {
        this.wordListNotInflected = wordListNotInflected;
    }

}

