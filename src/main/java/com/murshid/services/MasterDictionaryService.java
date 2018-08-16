package com.murshid.services;

import com.google.common.collect.Lists;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.DictionaryEntry;
import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.domain.views.MasterDictionaryView;
import com.murshid.persistence.repo.DictionaryEntryRepository;
import com.murshid.persistence.repo.MasterDictionaryRepository;
import com.murshid.persistence.repo.RekhtaRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Named
public class MasterDictionaryService {

    private MasterDictionaryRepository masterDictionaryRepository;
    private DictionaryEntryRepository dictionaryEntryRepository;
    private RekhtaRepository rekhtaRepository;

    public List<MasterDictionary> findByHindiWord(String hindiWord){
        return masterDictionaryRepository.findByHindiWord(hindiWord);
    }

    public Optional<MasterDictionary> findOne(Integer key){
        MasterDictionary result = masterDictionaryRepository.findOne(key);
        return Optional.ofNullable(result);
    }

    public Optional<MasterDictionary> findByHindiWordAndWordIndex(String hindiWord, int wordIndex){
        MasterDictionary result = masterDictionaryRepository.findByHindiWordAndWordIndex(hindiWord, wordIndex);
        return Optional.ofNullable(result);
    }

    public MasterDictionary save(MasterDictionary masterDictionary){
        return masterDictionaryRepository.save(masterDictionary);
    }

    @Inject
    public void setMasterDictionaryRepository(MasterDictionaryRepository masterDictionaryRepository) {
        this.masterDictionaryRepository = masterDictionaryRepository;
    }

    //@Transactional
    public boolean persistMasterDictionaryAndEntries(MasterDictionaryView view){
        MasterDictionary masterDictionary = new MasterDictionary()
                .setHindiWord(view.getHindi())
                .setWordIndex(view.getIndex())
                .setPartOfSpeech(view.getPartOfSpeech());

        int rekhtaIndex = -1;

        for (MasterDictionaryView.ConcreteDictionary conDic: view.getConcreteDictionaries()){
            if (conDic.getDictionaryIndex() != -1){
                DictionaryEntry dictionaryEnttry = new DictionaryEntry();
                dictionaryEnttry.setMasterDictionary(masterDictionary);
                dictionaryEnttry.setDictionarySource(conDic.getDictionarySource());
                dictionaryEnttry.setWordIndex(conDic.getDictionaryIndex());
                masterDictionary.addDictionaryEntry(dictionaryEnttry);

                if (conDic.getDictionarySource() == DictionarySource.REKHTA){
                    rekhtaIndex = conDic.getDictionaryIndex();
                }
            }
        }

        masterDictionary = masterDictionaryRepository.save(masterDictionary);

        if (rekhtaIndex != -1) {
            supplementRekhtaPOS(masterDictionary.getHindiWord(), rekhtaIndex, masterDictionary.getPartOfSpeech());
        }

        return true;
    }

    public void supplementRekhtaPOS(String hindi, int index, PartOfSpeech partOfSpeech){
            Optional<RekhtaEntry> rekhtaEntryOpt = rekhtaRepository.findByDictionaryKey_HindiWordAndDictionaryKey_WordIndex(hindi, index);
            if (rekhtaEntryOpt.isPresent()){
                RekhtaEntry rekhtaEntry = rekhtaEntryOpt.get();
                rekhtaEntry.setPartOfSpeech(partOfSpeech);
                rekhtaRepository.save(rekhtaEntryOpt.get());
            }
    }

    @Inject
    public MasterDictionaryService setDictionaryEntryRepository(DictionaryEntryRepository dictionaryEntryRepository) {
        this.dictionaryEntryRepository = dictionaryEntryRepository;
        return this;
    }

    @Inject
    public MasterDictionaryService setRekhtaRepository(RekhtaRepository rekhtaRepository) {
        this.rekhtaRepository = rekhtaRepository;
        return this;
    }




}
