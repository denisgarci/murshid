package com.murshid.services;

import com.murshid.models.enums.DictionarySource;
import com.murshid.morphology.AllCanonizers;
import com.murshid.persistence.domain.views.CanonicalWrapper;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Named
public class CanonicalsService {


    public List<CanonicalWrapper> findDictionaryEntries(@Nonnull String hindiWord){

        List<CanonicalWrapper> result = new ArrayList<>();
        result.addAll( wikitionaryService.findByHindiWord(hindiWord).stream().map(we -> new CanonicalWrapper(DictionarySource.WIKITIONARY, we)).collect(Collectors.toList()));
        result.addAll( prattsService.findByHindiWord(hindiWord).stream().map(we -> new CanonicalWrapper(DictionarySource.PRATTS, we)).collect(Collectors.toList()));
        result.addAll( rekhtaService.findByHindiWord(hindiWord).stream().map(we -> new CanonicalWrapper(DictionarySource.REKHTA, we)).collect(Collectors.toList()));
        result.addAll( gonzaloService.findByHindiWord(hindiWord).stream().map(we -> new CanonicalWrapper(DictionarySource.GONZALO, we)).collect(Collectors.toList()));

        return result;
    }


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

    @Inject
    protected WikitionaryService wikitionaryService;
    @Inject
    protected PrattsService prattsService;
    @Inject
    protected RekhtaService rekhtaService;
    @Inject
    protected GonzaloService gonzaloService;


}
