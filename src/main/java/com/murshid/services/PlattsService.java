package com.murshid.services;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.PlattsEntry;
import com.murshid.persistence.repo.PlattsRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class PlattsService {

    public List<PlattsEntry> findAnywhere(String word){
        List<PlattsEntry> inHindiOrUrdu = plattsRepository.findByDictionaryKeyHindiWordOrUrduWord(word, word);
        if (inHindiOrUrdu.isEmpty()){
            String searchString = "%" + word + "%";
            return plattsRepository.findByMeaningLikeOrKeystringLike(searchString, searchString);
        }else{
            return inHindiOrUrdu;
        }
    }

    public List<PlattsEntry> findByHindiWord(String hindiWord){
        return plattsRepository.findByDictionaryKeyHindiWord(hindiWord);
    }

    public PlattsEntry save(PlattsEntry plattsEntry){
        String meaning = plattsEntry.getMeaning().replace("\n", " ").replace("\r", " ");
        plattsEntry.setMeaning(meaning);
        return plattsRepository.save(plattsEntry);
    }

    public boolean exists(DictionaryKey key){
        return findOne(key).isPresent();
    }

    public boolean exists(String hindiWord, int wordIndex){
        return exists(new DictionaryKey().setHindiWord(hindiWord).setWordIndex(wordIndex));
    }

    public Optional<PlattsEntry> findOne(String hindiWord, int index){
        DictionaryKey dictionaryKey = new DictionaryKey().setHindiWord(hindiWord).setWordIndex(index);
        return findOne(dictionaryKey);
    }



    public Optional<PlattsEntry> findOne(DictionaryKey key){
        List<PlattsEntry> result = plattsRepository.findByDictionaryKey(key);
        if (result.size() > 1){
            throw new RuntimeException("unexpected size of results (" + result.size() + ") by DictionaryKey in Pratts hindiWOrd=" + key.hindiWord + " canonicalIndex=" + key.wordIndex);
        }else if (result.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(result.get(0));
        }
    }

    @Inject
    private PlattsRepository plattsRepository;
}
