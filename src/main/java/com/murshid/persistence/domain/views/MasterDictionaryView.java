package com.murshid.persistence.domain.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;

import java.util.ArrayList;
import java.util.List;

public class MasterDictionaryView {

    @JsonProperty("hindi")
    private String hindi;

    @JsonProperty("index")
    private int index;

    @JsonProperty("part_of_speech")
    private PartOfSpeech partOfSpeech;

    @JsonProperty("dictionaries")
    private List<ConcreteDictionary> concreteDictionaries = Lists.newArrayList(
            new ConcreteDictionary().setDictionaryIndex(-1).setDictionarySource(DictionarySource.BAHRI),
            new ConcreteDictionary().setDictionaryIndex(-1).setDictionarySource(DictionarySource.CATURVEDI),
            new ConcreteDictionary().setDictionaryIndex(-1).setDictionarySource(DictionarySource.MURSHID),
            new ConcreteDictionary().setDictionaryIndex(-1).setDictionarySource(DictionarySource.PLATTS),
            new ConcreteDictionary().setDictionaryIndex(-1).setDictionarySource(DictionarySource.REKHTA),
            new ConcreteDictionary().setDictionaryIndex(-1).setDictionarySource(DictionarySource.WIKITIONARY)
    );

    public static class ConcreteDictionary{

        @JsonProperty("dictionary_source")
        private DictionarySource dictionarySource;

        @JsonProperty("dictionary_index")
        private int dictionaryIndex;

        public DictionarySource getDictionarySource() {
            return dictionarySource;
        }

        public ConcreteDictionary setDictionarySource(DictionarySource dictionarySource) {
            this.dictionarySource = dictionarySource;
            return this;
        }

        public int getDictionaryIndex() {
            return dictionaryIndex;
        }

        public ConcreteDictionary setDictionaryIndex(int dictionaryIndex) {
            this.dictionaryIndex = dictionaryIndex;
            return this;
        }

    }

    public String getHindi() {
        return hindi;
    }

    public void setHindi(String hindi) {
        this.hindi = hindi;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public List<ConcreteDictionary> getConcreteDictionaries() {
        return concreteDictionaries;
    }

    public void setConcreteDictionaries(List<ConcreteDictionary> concreteDictionaries) {
        this.concreteDictionaries = concreteDictionaries;
    }

}
