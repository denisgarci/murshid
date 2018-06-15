package com.murshid.services;

import com.murshid.models.DictionaryRelationKey;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.*;
import com.murshid.persistence.repo.DictionaryEntryRepository;
import com.murshid.persistence.repo.DictionaryRelationsRepository;
import com.murshid.persistence.repo.MasterDictionaryRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

@Named
public class DictionaryRelationsService {

    private DictionaryRelationsRepository dictionaryRelationsRepository;
    private DictionaryEntryRepository dictionaryEntryRepository;
    private MasterDictionaryRepository masterDictionaryRepository;

    List<DictionaryEntry> getRelatedDictionaryEntries(String masterHindiWord, int masterWordIndex, DictionarySource dictionarySource) {

        return dictionaryRelationsRepository.findByDictionaryRelationKey(new DictionaryRelationKey().setHindiWord(masterHindiWord).setHindiWordIndex(masterWordIndex))
                .stream().map(dir -> masterDictionaryRepository.findByHindiWordAndWordIndex(dir.getHindiWordTo(), dir.getHindiWordIndexTo()))
                .map(mad ->  dictionaryEntryRepository.findByMasterDictionary(mad))
                .flatMap(List::stream)
                .filter(de -> de.getDictionarySource() == dictionarySource)
                .collect(Collectors.toList());
    }

    @Inject
    public void setDictionaryRelationsRepository(DictionaryRelationsRepository dictionaryRelationsRepository) {
        this.dictionaryRelationsRepository = dictionaryRelationsRepository;
    }

    @Inject
    public void setDictionaryEntryRepository(DictionaryEntryRepository dictionaryEntryRepository) {
        this.dictionaryEntryRepository = dictionaryEntryRepository;
    }

    @Inject
    public void setMasterDictionaryRepository(MasterDictionaryRepository masterDictionaryRepository) {
        this.masterDictionaryRepository = masterDictionaryRepository;
    }

}
