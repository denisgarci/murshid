package com.murshid.persistence.domain.views;

import com.murshid.models.enums.DictionarySource;
import com.murshid.services.IDictionaryEntry;

public class CanonicalWrapper {
    private DictionarySource dictionarySource;
    private IDictionaryEntry entry;

    public CanonicalWrapper(DictionarySource dictionarySource, IDictionaryEntry entry){
        this.dictionarySource = dictionarySource;
        this.entry = entry;
    }

    public DictionarySource getDictionarySource() {
        return dictionarySource;
    }

    public CanonicalWrapper setDictionarySource(DictionarySource dictionarySource) {
        this.dictionarySource = dictionarySource;
        return this;
    }

    public IDictionaryEntry getEntry() {
        return entry;
    }

    public CanonicalWrapper setEntry(IDictionaryEntry entry) {
        this.entry = entry;
        return this;
    }

    public String getKey(){
        return dictionarySource.name().concat("_").concat(entry.getDictionaryKey().toString());
    }

}
