package com.murshid.services;

import com.google.common.collect.Lists;
import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.PlattsEntry;
import com.murshid.persistence.repo.PlattsRepository;
import com.murshid.utils.WordUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

import static com.murshid.utils.WordUtils.KA_NUKTA;

@Named
public class PlattsService {

    private PlattsRepository plattsRepository;

    public List<PlattsEntry> findAnywhere(String word){
        List<PlattsEntry> inHindiOrUrdu = plattsRepository.findByDictionaryKeyHindiWordOrUrduWord(word, word);
        if (inHindiOrUrdu.isEmpty()){
            String searchString = "%" + word + "%";
            return plattsRepository.findByMeaningLikeOrKeystringLikeOrExtraMeaningLike(searchString, searchString, searchString);
        }else{
            return inHindiOrUrdu;
        }
    }

    public List<PlattsEntry> findByHindiWord(String hindiWord){
        return plattsRepository.findByDictionaryKeyHindiWord(hindiWord);
    }

    private List<PlattsEntry> findByHindiWordLike(String hindiWord){
        return plattsRepository.findByDictionaryKeyHindiWordLike(hindiWord);
    }


    /**
     * Replaces all hindi_word occurrences of
     *
     * क	devanagari letter ka	04425	2325	0x915	&#2325;
     * ़	devanagari sign nukta	04474	2364	0x93C	&#2364;
     *
     * with the single letter "qa with nukta"
     *
     * क़	devanagari letter qa	04530	2392	0x958	&#2392;
     *
     * to parse, use https://unicodelookup.com/?#%E0%A5%98/1
     *
     * Attention: since this process changes part of the primary keys, entries have to be deleted, then reinserted
     *
     * @return   the list of changed and persisted PlattsEntries
     */
    public List<PlattsEntry> replaceNuktas(){
        List<PlattsEntry> list = Lists.newArrayList(findByHindiWordLike("%".concat(KA_NUKTA).concat("%")));

        for (PlattsEntry pe : list){
            plattsRepository.delete(pe);
            pe.getDictionaryKey().setHindiWord(WordUtils.replace2CharsWithNukta(pe.getHindiWord()));
            save(pe);
        }
        return list;
    }

    public PlattsEntry save(PlattsEntry plattsEntry){
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
            throw new RuntimeException("unexpected size of results (" + result.size() + ") by DictionaryKey in Pratts hindiWOrd=" + key.hindiWord + " hindiWordIndex=" + key.wordIndex);
        }else if (result.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(result.get(0));
        }
    }

    @Inject
    public void setPlattsRepository(PlattsRepository plattsRepository) {
        this.plattsRepository = plattsRepository;
    }
}
