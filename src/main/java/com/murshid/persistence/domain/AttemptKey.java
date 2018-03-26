package com.murshid.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Timestamp;

@Embeddable
public class AttemptKey implements Serializable{

    @Column(name = "entry", nullable = false)
    public String entry;

    @Column(name = "attempted_at", nullable = false)
    public Timestamp attemptedAt;

    public String getEntry() {
        return entry;
    }

    public AttemptKey setEntry(String entry) {
        this.entry = entry;
        return this;
    }

    public Timestamp getAttemptedAt() {
        return attemptedAt;
    }

    public AttemptKey setAttemptedAt(Timestamp attemptedAt) {
        this.attemptedAt = attemptedAt;
        return this;
    }
}
