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

    private WikitionaryService wikitionaryService;
    private PlattsService plattsService;
    private RekhtaService rekhtaService;
    private MurshidService murshidService;
    private CaturvediService caturvediService;


    public List<CanonicalWrapper> suggestCanonicals(@Nonnull String inflected){
        Set<String> possibleCanonicalWords = AllCanonizers.allCanonicals(inflected);
        List<CanonicalWrapper> result = new ArrayList<>();

        for(String cw : possibleCanonicalWords) {
            result.addAll( wikitionaryService.findByHindiWord(cw).stream().map(we -> new CanonicalWrapper(DictionarySource.WIKITIONARY, we)).collect(Collectors.toList()));
            result.addAll(plattsService.findByHindiWord(cw).stream().map(we -> new CanonicalWrapper(DictionarySource.PLATTS, we)).collect(Collectors.toList()));
            result.addAll( rekhtaService.findByHindiWord(cw).stream().map(we -> new CanonicalWrapper(DictionarySource.REKHTA, we)).collect(Collectors.toList()));
            result.addAll(murshidService.findByHindiWord(cw).stream().map(we -> new CanonicalWrapper(DictionarySource.MURSHID, we)).collect(Collectors.toList()));
            result.addAll(caturvediService.findByHindiWord(cw).stream().map(we -> new CanonicalWrapper(DictionarySource.CATURVEDI, we)).collect(Collectors.toList()));
        }

        return result;
    }


    @Inject
    public void setWikitionaryService(WikitionaryService wikitionaryService) {
        this.wikitionaryService = wikitionaryService;
    }

    @Inject
    public void setPlattsService(PlattsService plattsService) {
        this.plattsService = plattsService;
    }

    @Inject
    public void setRekhtaService(RekhtaService rekhtaService) {
        this.rekhtaService = rekhtaService;
    }

    @Inject
    public void setMurshidService(MurshidService murshidService) {
        this.murshidService = murshidService;
    }

    @Inject
    public void setCaturvediService(CaturvediService caturvediService) {
        this.caturvediService = caturvediService;
    }

}
