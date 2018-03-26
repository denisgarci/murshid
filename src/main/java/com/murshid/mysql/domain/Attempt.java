package com.murshid.mysql.domain;

import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.Language;

import javax.persistence.*;

@Entity(name = "attempts")
public class Attempt {

    @EmbeddedId
    private AttemptKey attemptKey;

    @Enumerated(EnumType.STRING)
    @Column(name="language")
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(name="source")
    private DictionarySource dictionarySource;

    private boolean successful;

    public AttemptKey getAttemptKey() {
        return attemptKey;
    }

    public Attempt setAttemptKey(AttemptKey attemptKey) {
        this.attemptKey = attemptKey;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public Attempt setLanguage(Language language) {
        this.language = language;
        return this;
    }

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }

    public Attempt setDictionarySource(DictionarySource dictionarySource) {
        this.dictionarySource = dictionarySource;
        return this;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Attempt setSuccessful(boolean successful) {
        this.successful = successful;
        return this;
    }
}
