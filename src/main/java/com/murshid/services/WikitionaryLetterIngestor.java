package com.murshid.services;

import com.murshid.ingestor.wikitionary.WikitionaryCaller;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.SpellCheckEntry;
import com.murshid.persistence.repo.SpellCheckRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
@Scope("prototype")
public class WikitionaryLetterIngestor implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryLetterIngestor.class);
    private String letter;
    private boolean retryFailed;

    private SpellCheckRepository spellCheckRepository;
    private WikitionaryWordProcessor wikitionaryWordProcessor;

    /**
     * Reads words from the hindi_words list, and tries to ingest that hindiWordIndex from the Wikidictionary website
     * @param letter            the initial of the hindi_words we select
     * @param retryFailed       true if we should leave out only words that were successfully ingested in the past, false
     *                          if we should leave out all words that were tried before, independently of success.
     */
    public WikitionaryLetterIngestor(String letter, boolean retryFailed){
        this.letter = letter;
        this.retryFailed = retryFailed;
    }

    @Override
    public void run(){

        List<SpellCheckEntry> targetHindiWords;

         if (retryFailed){
             targetHindiWords = spellCheckRepository.selectByInitialExceptSuccessful(letter, DictionarySource.WIKITIONARY.name());

         } else {
             targetHindiWords = spellCheckRepository.selectByInitialExceptAllTried(letter, DictionarySource.WIKITIONARY.name());
         }

        WikitionaryCaller caller = new WikitionaryCaller();

        for (SpellCheckEntry hindiWord: targetHindiWords){
            wikitionaryWordProcessor.processWord(caller, hindiWord.getHindiWord());
        }

        LOGGER.info("finished processing letter {}", letter);
    }

    @Inject
    public void setSpellCheckRepository(SpellCheckRepository spellCheckRepository) {
        this.spellCheckRepository = spellCheckRepository;
    }

    @Inject
    public void setWikitionaryWordProcessor(WikitionaryWordProcessor wikitionaryWordProcessor) {
        this.wikitionaryWordProcessor = wikitionaryWordProcessor;
    }



}
