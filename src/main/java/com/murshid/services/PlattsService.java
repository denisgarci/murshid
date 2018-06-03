package com.murshid.services;

import com.google.common.collect.Lists;
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
            return plattsRepository.findByMeaningLikeOrKeystringLikeOrExtraMeaningLike(searchString, searchString, searchString);
        }else{
            return inHindiOrUrdu;
        }
    }

    public Iterable<PlattsEntry> findAll(){
        return plattsRepository.findAll();
    }

    public List<PlattsEntry> findByHindiWord(String hindiWord){
        return plattsRepository.findByDictionaryKeyHindiWord(hindiWord);
    }

    public List<PlattsEntry> findByHindiWordLike(String hindiWord){
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
        char ka = 0x915;
        char nukta = 0x93C;
        String kaNukta = new StringBuilder().append(ka).append(nukta).toString();

        StringBuilder sbLike = new StringBuilder();
        sbLike.append("%").append(kaNukta).append("%");

        List<PlattsEntry> list = Lists.newArrayList(findByHindiWordLike(sbLike.toString()));

        char qa = 0x958;
        String qaStr = new String(new char[]{qa});

        for (int i=0; i<list.size(); i++){
            PlattsEntry pe = list.get(i);
            plattsRepository.delete(pe);
            String newhindiword = pe.getHindiWord().replace(kaNukta, qaStr);
            pe.getDictionaryKey().setHindiWord(newhindiword);
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
    private PlattsRepository plattsRepository;
}
