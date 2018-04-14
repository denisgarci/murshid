package com.murshid.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "spell_check")
public class SpellCheckEntry {

    @Id
    @Column(name ="hindi_word")
    private String hindiWord;

    @Column(name ="created_at")
    private Timestamp createdAt;

    @Column(name ="updated_at")
    private Timestamp updatedAt;

    private boolean active;

    private char initial;

    @Column(name ="urdu_word")
    private String urduWord;

    public String getHindiWord() {
        return hindiWord;
    }

    public SpellCheckEntry setHindiWord(String hindiWord) {
        this.hindiWord = hindiWord;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public SpellCheckEntry setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public SpellCheckEntry setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public SpellCheckEntry setActive(boolean active) {
        this.active = active;
        return this;
    }

    public char getInitial() {
        return initial;
    }

    public SpellCheckEntry setInitial(char initial) {
        this.initial = initial;
        return this;
    }


    public String getUrduWord() {
        return urduWord;
    }

    public SpellCheckEntry setUrduWord(String urduWord) {
        this.urduWord = urduWord;
        return this;
    }
}
