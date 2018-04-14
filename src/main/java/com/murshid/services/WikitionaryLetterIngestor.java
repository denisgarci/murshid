package com.murshid.services;

import com.google.common.collect.Lists;
import com.murshid.ingestor.wikitionary.WikiUtils;
import com.murshid.ingestor.wikitionary.WikitionaryCaller;
import com.murshid.ingestor.wikitionary.models.WikiEntry;
import com.murshid.models.enums.DictionarySource;
import com.murshid.persistence.domain.HindiWord;
import com.murshid.persistence.repo.SpellCheckRepository;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
public class WikitionaryLetterIngestor implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryLetterIngestor.class);
    private String letter;
    private boolean retryFailed;

    /**
     * Reads words from the hindi_words list, and tries to ingest that canonicalWord from the Wikidictionary website
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

        List<HindiWord> targetHindiWords;

         if (retryFailed){
             targetHindiWords = spellCheckRepository.selectByInitialExceptSuccessful(letter, DictionarySource.WIKITIONARY.name());

         } else {
             targetHindiWords = spellCheckRepository.selectByInitialExceptAllTried(letter, DictionarySource.WIKITIONARY.name());
         }

        WikitionaryCaller caller = new WikitionaryCaller();
        String currentWord = null;

        for (HindiWord hindiWord: targetHindiWords){
            wikitionaryWordProcessor.processWord(caller, hindiWord.getWord());
        }

        LOGGER.info("finished processing letter {}", letter);
    }

    private List<WikiEntry> attemptWithWord(String word, Document document){
        Optional<WikiEntry> entry = WikiUtils.populateEntry(word, document);
        List<WikiEntry> result = new ArrayList<>();
        if (entry.isPresent()) {
            result = Lists.newArrayList(entry.get());
        } else {
            result = WikiUtils.populateEtymologyEntries(word, document);
        }
        return result;
    }

    @Inject
    private SpellCheckRepository spellCheckRepository;

    @Inject
    private WikitionaryWordProcessor wikitionaryWordProcessor;



}
