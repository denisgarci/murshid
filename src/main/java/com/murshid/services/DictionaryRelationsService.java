package com.murshid.services;

import com.murshid.models.DictionaryRelationKey;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.*;
import com.murshid.persistence.domain.views.DictionaryEntryView;
import com.murshid.persistence.repo.DictionaryRelationsRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Named
public class DictionaryRelationsService {

    private List<DictionaryRelations> find(String hindiWord, int hindiWordIndex, DictionarySource dictionarySource){

        DictionaryRelationKey dictionaryRelationKey = new DictionaryRelationKey().setHindiWord(hindiWord)
                .setHindiWordIndex(hindiWordIndex).setDictionarySource(dictionarySource);
        return dictionaryRelationsRepository.findByDictionaryRelationKey(dictionaryRelationKey);
    }

    private <T extends IDictionaryEntry> DictionaryEntryView convert(T origin){
        return new DictionaryEntryView()
                .setDictionarySource(origin.getDictionarySource())
                .setHindiWord(origin.getHindiWord())
                .setMeaning(origin.getMeaning())
                .setPartOfSpeech(origin.getPartOfSpeech())
                .setWordIndex(origin.getWordIndex());
    }

    public  List<DictionaryEntryView> find(DictionaryEntryView sourceDictionaryEntryView){

        List<DictionaryRelations> dirs = find(sourceDictionaryEntryView.getHindiWord(), sourceDictionaryEntryView.getWordIndex(), sourceDictionaryEntryView.getDictionarySource());
        List<DictionaryEntryView> result = new ArrayList<>();

        dirs.forEach(de -> {
            switch (de.dictionarySourceTo){
                case PLATTS:
                    Optional<PlattsEntry> plattsEntry = plattsService.findOne(de.getHindiWordTo(), de.getHindiWordIndexTo());
                    if (plattsEntry.isPresent()){
                        result.add(convert(plattsEntry.get()));
                    }
                    break;
                case WIKITIONARY:
                    Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(de.getHindiWordTo(), de.getHindiWordIndexTo());
                    if (wikitionaryEntry.isPresent()){
                        result.add(convert(wikitionaryEntry.get()));
                    }
                    break;
                case REKHTA:
                    Optional<RekhtaEntry> rekhtaEntry = rekhtaService.findOne(de.getHindiWordTo(), de.getHindiWordIndexTo());
                    if (rekhtaEntry.isPresent()){
                        result.add(convert(rekhtaEntry.get()));
                    }
                    break;
                case MURSHID:
                    Optional<MurshidEntry> murshidEntry = murshidService.findOne(de.getHindiWordTo(), de.getHindiWordIndexTo());
                    if (murshidEntry.isPresent()){
                        result.add(convert(murshidEntry.get()));
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("dictionary source value " + de.dictionarySourceTo + " not supported");
            }

        });

        return result;
    }

    @Inject
    private MurshidService murshidService;

    @Inject
    private RekhtaService rekhtaService;

    @Inject
    private DictionaryRelationsRepository dictionaryRelationsRepository;

    @Inject
    private PlattsService plattsService;

    @Inject
    private WikitionaryService wikitionaryService;


}
