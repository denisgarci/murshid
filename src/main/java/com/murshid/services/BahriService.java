package com.murshid.services;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.BahriEntry;
import com.murshid.persistence.repo.BahriRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class BahriService {

    private BahriRepository bahriRepository;

    public BahriEntry save(BahriEntry caturvediEntry){
        return bahriRepository.save(caturvediEntry);
    }

    public boolean exists(DictionaryKey key){
        return findOne(key).isPresent();
    }

    public boolean exists(String hindiWord, int wordIndex){
        return exists(new DictionaryKey().setHindiWord(hindiWord).setWordIndex(wordIndex));
    }

    public List<BahriEntry> findByHindiWord(String hindiWord){
        return bahriRepository.findByDictionaryKeyHindiWord(hindiWord);
    }

    public Optional<BahriEntry> findOne(DictionaryKey key){
        List<BahriEntry> result = bahriRepository.findByDictionaryKey(key);
        if (result.size() > 1){
            throw new RuntimeException("unexpected size of results by DictionaryKey in Bahri");
        }else if (result.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(result.get(0));
        }
    }

    public Optional<BahriEntry> findOne(String hindiWord, int index){
        DictionaryKey dictionaryKey = new DictionaryKey().setHindiWord(hindiWord).setWordIndex(index);
        return findOne(dictionaryKey);
    }

    public List<BahriEntry> findAnywhere(String word){
        List<BahriEntry> inHindiOrUrdu = bahriRepository.findByDictionaryKeyHindiWord(word);
        if (inHindiOrUrdu.isEmpty()){
            String searchString = "%" + word + "%";
            return bahriRepository.findByMeaningLikeOrExtraMeaningLike(searchString, searchString);
        }else{
            return inHindiOrUrdu;
        }
    }

    @Inject
    public void setBahriRepository(BahriRepository bahriRepository) {
        this.bahriRepository = bahriRepository;
    }


}
