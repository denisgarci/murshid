package com.murshid.services;

import com.google.common.collect.Lists;
import com.murshid.ingestor.FunctionUtil;
import com.murshid.ingestor.wikitionary.WikiUtils;
import com.murshid.ingestor.wikitionary.WikitionaryCaller;
import com.murshid.ingestor.wikitionary.models.WikiEntry;
import com.murshid.ingestor.wikitionary.models.WikiPosParagraph;
import com.murshid.models.WikitionaryEntry;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.Language;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.mysql.domain.Attempt;
import com.murshid.mysql.domain.AttemptKey;
import com.murshid.mysql.domain.DictionaryKey;
import com.murshid.mysql.domain.HindiWord;
import com.murshid.mysql.repo.AttemptsRepository;
import com.murshid.mysql.repo.HindiWordsRepository;
import com.murshid.mysql.repo.WikitionaryRepository;
import com.murshid.utils.IngestorWordUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.time.Duration;
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
     * Reads words from the hindi_words list, and tries to ingest that word from the Wikidictionary website
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
             targetHindiWords = hindiWordsRepository.selectByInitialExceptSuccessful(letter, DictionarySource.WIKITIONARY);

         } else {
             targetHindiWords = hindiWordsRepository.selectByInitialExceptAllTried(letter, DictionarySource.WIKITIONARY);
         }

        WikitionaryCaller caller = new WikitionaryCaller();
        String currentWord = null;

        for (HindiWord hindiWord: targetHindiWords){
            currentWord = hindiWord.getWord();

            String retryMsg = "Crawling failed for word " + currentWord + " retrying [{}x]";
            String failureMsg = "Could not properly crawl word " + currentWord;
            org.jsoup.nodes.Document document = FunctionUtil.retryFn(() -> WikitionaryCaller.documentForWord(caller, hindiWord.getWord()),
                                                                     e -> e instanceof SocketTimeoutException || e instanceof javax.ws.rs.ProcessingException ,
                                                                     Duration.ofSeconds(1).toMillis(), retryMsg, failureMsg);

            List<WikiEntry> entriesToWrite = attemptWithWord(currentWord, document);
            if (entriesToWrite.isEmpty()){
                String withCandra = IngestorWordUtils.replaceAnusvaara(currentWord);
                if (withCandra.equals(currentWord)){

                    entriesToWrite = attemptWithWord(withCandra, document);
                    currentWord = withCandra;
                }
            }

            if (!entriesToWrite.isEmpty()) {
                int index = 0;
                for (WikiEntry wikiEntry : entriesToWrite) {
                    for (WikiPosParagraph par : wikiEntry.posParagraphs) {
                        for (int parIndex = 0; parIndex < par.meanings.size(); parIndex++) {
                            DictionaryKey dictionaryKey = new DictionaryKey()
                                    .setWordIndex(index)
                                    .setWord(currentWord);

                            WikitionaryEntry wikitionaryEntry = new WikitionaryEntry()
                                    .setDictionaryKey(dictionaryKey)
                                    .setIpaPronunciation(wikiEntry.IPAPronunciation.orElse(null))
                                    .setPartOfSpeech(PartOfSpeech.valueOf(par.partOfSpeech.name()))
                                    .setAccidence(com.murshid.utils.AccidenceConverter.wikiToGeneralAccidentList(par.accidence))
                                    .setUrduSpelling(par.urduSpelling.orElse(null))
                                    .setMeaning(par.meanings.get(parIndex))
                                    .setEtymology(wikiEntry.etymology.orElse(null));

                            wikitionaryEntry = wikitionaryRepository.save(wikitionaryEntry);

                            index++;

                            AttemptKey attemptKey = new AttemptKey().setAttemptedAt(new Timestamp(System.currentTimeMillis()))
                                    .setEntry(currentWord);

                            Attempt attempt = new Attempt().setLanguage(Language.HINDI).setDictionarySource(DictionarySource.WIKITIONARY)
                                .setAttemptKey(attemptKey).setSuccessful(true);

                            attemptsRepository.save(attempt);

                            LOGGER.info("new word ingested hidiWord={} meaning {}", hindiWord.getWord(), wikitionaryEntry.getMeaning());

                        }
                    }
                }
            } else {
                AttemptKey attemptKey = new AttemptKey().setAttemptedAt(new Timestamp(System.currentTimeMillis()))
                        .setEntry(currentWord);

                Attempt attempt = new Attempt().setLanguage(Language.HINDI).setDictionarySource(DictionarySource.WIKITIONARY)
                        .setAttemptKey(attemptKey).setSuccessful(false);

                attemptsRepository.save(attempt);
            }
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
    private HindiWordsRepository hindiWordsRepository;

    @Inject
    private WikitionaryRepository wikitionaryRepository;

    @Inject
    private AttemptsRepository attemptsRepository;


}
