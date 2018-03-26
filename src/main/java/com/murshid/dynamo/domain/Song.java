package com.murshid.dynamo.domain;

import java.util.List;

public class Song {

    private String titleHindi;

    private String titleLatin;

    private String author;

    private List<String> media;

    private String song;

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

}

