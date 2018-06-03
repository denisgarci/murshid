package com.murshid.services;

import com.google.common.collect.Lists;
import com.murshid.ingestor.utils.FunctionUtil;
import com.murshid.ingestor.wikitionary.WikiUtils;
import com.murshid.ingestor.wikitionary.WikitionaryCaller;
import com.murshid.ingestor.wikitionary.models.WikiEntry;
import com.murshid.ingestor.wikitionary.models.WikiPosParagraph;
import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.Language;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.Attempt;
import com.murshid.persistence.domain.AttemptKey;
import com.murshid.persistence.domain.WikitionaryEntry;
import com.murshid.persistence.repo.AttemptsRepository;
import com.murshid.persistence.repo.WikitionaryRepository;
import com.murshid.utils.WordUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Named
public class WikitionaryWordProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryWordProcessor.class);

    public void processWord(@Nonnull WikitionaryCaller caller,  @NotNull final String hindiWord){

        String retryMsg = "Crawling failed for hindiWordIndex " + hindiWord + " retrying [{}x]";
        String failureMsg = "Could not properly crawl hindiWordIndex " + hindiWord;
        org.jsoup.nodes.Document document = FunctionUtil.retryFn(() -> WikitionaryCaller.documentForWord(caller, hindiWord),
                                                                 e -> e instanceof SocketTimeoutException || e instanceof javax.ws.rs.ProcessingException ,
                                                                 Duration.ofSeconds(1).toMillis(), retryMsg, failureMsg);

        List<WikiEntry> entriesToWrite = attemptWithWord(hindiWord, document);
        String alternativeHindiWord = hindiWord;

        if (entriesToWrite.isEmpty()){
            String withCandra = WordUtils.replaceAnusvaara(hindiWord);
            if (withCandra.equals(alternativeHindiWord)){

                entriesToWrite = attemptWithWord(withCandra, document);
                alternativeHindiWord = withCandra;
            }
        }

        if (!entriesToWrite.isEmpty()) {
            int index = 0;
            for (WikiEntry wikiEntry : entriesToWrite) {
                for (WikiPosParagraph par : wikiEntry.posParagraphs) {
                    for (int parIndex = 0; parIndex < par.meanings.size(); parIndex++) {
                        DictionaryKey dictionaryKey = new DictionaryKey()
                                .setWordIndex(index)
                                .setHindiWord(alternativeHindiWord);

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
                                .setEntry(hindiWord);

                        Attempt attempt = new Attempt().setLanguage(Language.HINDI).setDictionarySource(
                                DictionarySource.WIKITIONARY)
                                .setAttemptKey(attemptKey).setSuccessful(true);

                        attemptsRepository.save(attempt);

                        LOGGER.info("new hindiWordIndex ingested hidiWord={} meaning {}", alternativeHindiWord, wikitionaryEntry.getMeaning());

                    }
                }
            }
        } else {
            AttemptKey attemptKey = new AttemptKey().setAttemptedAt(new Timestamp(System.currentTimeMillis()))
                    .setEntry(hindiWord);

            Attempt attempt = new Attempt().setLanguage(Language.HINDI).setDictionarySource(DictionarySource.WIKITIONARY)
                    .setAttemptKey(attemptKey).setSuccessful(false);

            attemptsRepository.save(attempt);
        }
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
    private WikitionaryRepository wikitionaryRepository;

    @Inject
    private AttemptsRepository attemptsRepository;
}
