package com.murshid.services;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.CaturvediEntry;
import com.murshid.persistence.domain.MurshidEntry;
import com.murshid.persistence.domain.PlattsEntry;
import com.murshid.persistence.repo.CaturvediRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class CaturvediService {

    private CaturvediRepository caturvediRepository;

    public CaturvediEntry save(CaturvediEntry caturvediEntry){
        return caturvediRepository.save(caturvediEntry);
    }

    public boolean exists(DictionaryKey key){
        return findOne(key).isPresent();
    }

    public boolean exists(String hindiWord, int wordIndex){
        return exists(new DictionaryKey().setHindiWord(hindiWord).setWordIndex(wordIndex));
    }

    public List<CaturvediEntry> findByHindiWord(String hindiWord){
        return caturvediRepository.findByDictionaryKeyHindiWord(hindiWord);
    }

    public Optional<CaturvediEntry> findOne(DictionaryKey key){
        List<CaturvediEntry> result = caturvediRepository.findByDictionaryKey(key);
        if (result.size() > 1){
            throw new RuntimeException("unexpected size of results by DictionaryKey in Caturvedi");
        }else if (result.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(result.get(0));
        }
    }

    public Optional<CaturvediEntry> findOne(String hindiWord, int index){
        DictionaryKey dictionaryKey = new DictionaryKey().setHindiWord(hindiWord).setWordIndex(index);
        return findOne(dictionaryKey);
    }

    public List<CaturvediEntry> findAnywhere(String word){
        List<CaturvediEntry> inHindiOrUrdu = caturvediRepository.findByDictionaryKeyHindiWord(word);
        if (inHindiOrUrdu.isEmpty()){
            String searchString = "%" + word + "%";
            return caturvediRepository.findByMeaningLikeOrExtraMeaningLike(searchString, searchString);
        }else{
            return inHindiOrUrdu;
        }
    }

    @Inject
    public void setCaturvediRepository(CaturvediRepository caturvediRepository) {
        this.caturvediRepository = caturvediRepository;
    }


}
