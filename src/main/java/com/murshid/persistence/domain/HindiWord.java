package com.murshid.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "hindi_words")
public class HindiWord {

    @Id
    private String word;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean active;

    private char initial;

    public String getWord() {
        return word;
    }

    public HindiWord setWord(String word) {
        this.word = word;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public HindiWord setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public HindiWord setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public HindiWord setActive(boolean active) {
        this.active = active;
        return this;
    }

    public char getInitial() {
        return initial;
    }

    public HindiWord setInitial(char initial) {
        this.initial = initial;
        return this;
    }
}
