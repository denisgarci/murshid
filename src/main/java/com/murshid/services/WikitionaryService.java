package com.murshid.services;

import com.murshid.models.DictionaryKey;
import com.murshid.persistence.domain.WikitionaryEntry;
import com.murshid.persistence.repo.SpellCheckRepository;
import com.murshid.persistence.repo.WikitionaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Named
public class WikitionaryService implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryService.class);

    private ApplicationContext applicationContext;

    public void processAllLetters(boolean retryFailed){
        List<Character> initials = spellCheckRepository.selectDistinctInitials();
        ExecutorService pool = Executors.newFixedThreadPool(initials.size(), new LoggerThreadFactory());

        for (Character initial : initials){
            WikitionaryLetterIngestor ingestor = applicationContext.getBean(WikitionaryLetterIngestor.class, String.valueOf(initial), retryFailed);
            pool.submit(ingestor);
        }



        pool.shutdown();
    }

    public WikitionaryEntry save(WikitionaryEntry rekhtaEntry){
        return wikitionaryRepository.save(rekhtaEntry);
    }

    class LoggerThreadFactory implements ThreadFactory {
        private int counter;
        public Thread newThread(Runnable r) {
            return new Thread(r, "letter-ingestion " + counter++);
        }
    }

    public boolean exists(DictionaryKey key){
        return findOne(key).isPresent();
    }

    public boolean exists(String hindiWord, int index){
        DictionaryKey dictionaryKey = new DictionaryKey().setHindiWord(hindiWord).setWordIndex(index);
        return exists(dictionaryKey);
    }

    public Optional<WikitionaryEntry> findOne(DictionaryKey key){
        return Optional.ofNullable(wikitionaryRepository.findOne(key));
    }

    public Optional<WikitionaryEntry> findOne(String hindiWord, int wordIndex){
        DictionaryKey key = new DictionaryKey().setHindiWord(hindiWord).setWordIndex(wordIndex);
        return findOne(key);
    }

    public List<WikitionaryEntry> findByHindiWord(String hindiWord){
        try{
            return wikitionaryRepository.findByDictionaryKeyHindiWord(hindiWord);
        }catch (Exception ex){
            LOGGER.error("error when finding hindiWord={} in Wikitionary", hindiWord, ex);
            return Collections.emptyList();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Inject
    private WikitionaryRepository wikitionaryRepository;

    @Inject
    private SpellCheckRepository spellCheckRepository;
}
