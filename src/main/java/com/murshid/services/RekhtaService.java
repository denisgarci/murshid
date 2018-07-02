package com.murshid.services;

import com.google.common.collect.Lists;
import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.PlattsEntry;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.repo.RekhtaRepository;
import com.murshid.utils.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

import static com.murshid.utils.WordUtils.GA_NUKTA;

@Named
public class RekhtaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RekhtaService.class);

    private RekhtaRepository rekhtaRepository;

    public RekhtaEntry save(RekhtaEntry rekhtaEntry){
        return rekhtaRepository.save(rekhtaEntry);
    }

    public Optional<RekhtaEntry> findOne(DictionaryKey key){
        RekhtaEntry rekhtaEntry = rekhtaRepository.findOne(key);
        return Optional.ofNullable(rekhtaEntry);
    }

    public boolean exists(DictionaryKey key){
        return findOne(key).isPresent();
    }

    public boolean exists(String hindiWord, int index){
        DictionaryKey dictionaryKey = new DictionaryKey().setHindiWord(hindiWord).setWordIndex(index);
        return exists(dictionaryKey);
    }

    public List<RekhtaEntry> findByHindiWord(String hindiWord){
        return rekhtaRepository.findByDictionaryKeyHindiWord(hindiWord);
    }

    public Optional<RekhtaEntry> findOne(String hindiWord, int index){
        DictionaryKey dictionaryKey = new DictionaryKey().setHindiWord(hindiWord).setWordIndex(index);
        return findOne(dictionaryKey);
    }

    /**
     * Replaces all hindi_word occurrences of
     *
     * ग	DEVANAGARI LETTER GA    0917	2327	&‌#2327;	&‌#x0917;
     * ़	devanagari sign nukta	04474	2364	0x93C	&#2364;
     *
     * with the single letter "DEVANAGARI LETTER GHHA"
     *
     * DEVANAGARI LETTER GHHA	ग़	Option+095A	ALT+2394	&‌#2394;	&‌#x095A;
     *
     * to parse, use https://unicodelookup.com/?#%E0%A5%98/1
     *
     * Attention: since this process changes part of the primary keys, entries have to be deleted, then reinserted
     *
     * @return   the list of changed and persisted PlattsEntries
     */
    public List<RekhtaEntry> replaceGhaNuktas(){
        List<RekhtaEntry> list = Lists.newArrayList(findByHindiWordLike("%".concat(GA_NUKTA).concat("%")));

        for (RekhtaEntry pe : list){
            rekhtaRepository.delete(pe);
            pe.getDictionaryKey().setHindiWord(WordUtils.replace2CharsWithGhaNukta(pe.getHindiWord()));
            save(pe);
        }
        return list;
    }

    private List<RekhtaEntry> findByHindiWordLike(String hindiWord){
        return rekhtaRepository.findByDictionaryKeyHindiWordLike(hindiWord);
    }


    public boolean isValid(RekhtaEntry rekhtaEntry) {
        if (rekhtaEntry.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (rekhtaEntry.getDictionaryKey() == null) {
            LOGGER.info("dictionary key cannot be null");
            return false;
        } else {
            if (rekhtaEntry.getDictionaryKey().hindiWord == null) {
                LOGGER.info("dictionary entry key cannot be null");
                return false;
            }
        }

        if (rekhtaEntry.getUrduWord() == null) {
            LOGGER.info("urduWord cannot be null");
            return false;
        }

        if (rekhtaEntry.getMeaning() == null) {
            LOGGER.info("meaning cannot be null");
            return false;
        }

        return true;
    }

    @Inject
    public void setRekhtaRepository(RekhtaRepository rekhtaRepository) {
        this.rekhtaRepository = rekhtaRepository;
    }
}
