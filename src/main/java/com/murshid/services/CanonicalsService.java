package com.murshid.services;

import com.murshid.models.enums.DictionarySource;
import com.murshid.morphology.AllCanonizers;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Named
public class CanonicalsService {



    public List<CanonicalWrapper> suggestCanonicals(@Nonnull String inflected){
        Set<String> possibleCanonicalWords = AllCanonizers.allCanonicals(inflected);
        List<CanonicalWrapper> result = new ArrayList<>();

        for(String cw : possibleCanonicalWords) {
            result.addAll( wikitionaryService.findByHindiWord(cw).stream().map(we -> new CanonicalWrapper(DictionarySource.WIKITIONARY, we)).collect(Collectors.toList()));
            result.addAll( prattsService.findByHindiWord(cw).stream().map(we -> new CanonicalWrapper(DictionarySource.PRATTS, we)).collect(Collectors.toList()));
            result.addAll( rekhtaService.findByHindiWord(cw).stream().map(we -> new CanonicalWrapper(DictionarySource.REKHTA, we)).collect(Collectors.toList()));
            result.addAll( gonzaloService.findByHindiWord(cw).stream().map(we -> new CanonicalWrapper(DictionarySource.GONZALO, we)).collect(Collectors.toList()));
        }

        return result;
    }

    static class CanonicalWrapper{
        private DictionarySource dictionarySource;
        private Object entry;

        public CanonicalWrapper(DictionarySource dictionarySource, Object entry){
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

        public Object getEntry() {
            return entry;
        }

        public CanonicalWrapper setEntry(Object entry) {
            this.entry = entry;
            return this;
        }
    }


    @Inject
    protected WikitionaryService wikitionaryService;
    @Inject
    protected PrattsService prattsService;
    @Inject
    protected RekhtaService rekhtaService;
    @Inject
    protected GonzaloService gonzaloService;


}
