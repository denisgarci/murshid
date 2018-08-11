package com.murshid;


import com.google.common.collect.Lists;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.persistence.repo.InflectedRepositoryDB;
import com.murshid.persistence.repo.MasterDictionaryRepository;
import com.murshid.services.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.*;

@SpringBootApplication

public class MurshidApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryLetterIngestor.class);

    private static ConfigurableApplicationContext context;

	public static void main(String[] args) throws Exception{
		context = SpringApplication.run(MurshidApplication.class, args);

        dumpInflected();

	}

    private static void dumpInflected() {
        InflectedRepositoryDB inflectedRepositoryDB = context.getBean(InflectedRepositoryDB.class);
        MasterDictionaryRepository masterDictionaryRepository = context.getBean(MasterDictionaryRepository.class);
        InflectedService masterService = context.getBean(InflectedService.class);
        List<Inflected> all = masterService.getAll();
        all.forEach(inf -> {
            com.murshid.persistence.domain.Inflected dbInf = new com.murshid.persistence.domain.Inflected();
            dbInf.setAccidence(inf.getAccidence() != null? Lists.newArrayList(inf.getAccidence()): null);
            MasterDictionary masterDictionary = masterDictionaryRepository.findOne(inf.getMasterDictionaryId());
            dbInf.setMasterDictionary(masterDictionary);
            dbInf.setPartOfSpeech(inf.getPartOfSpeech());
            dbInf.setOwnMeaning(inf.isOwnMeaning());
            dbInf.setInflectedUrdu(inf.getInflectedUrdu());
            com.murshid.persistence.domain.Inflected.InflectedKey key = new com.murshid.persistence.domain.Inflected.InflectedKey();
            key.setInflectedHindi(inf.getInflectedHindi());
            key.setInflectedHindiIndex(inf.getInflectedHindiIndex());
            dbInf.setInflectedKey(key);
            inflectedRepositoryDB.save(dbInf);
        });
        LOGGER.info("finished passing inflected");
    }



    private static void resequence() {
        SongsService inflectedService = context.getBean(SongsService.class);
        inflectedService.resequenceSongWordsToInflected("Dard Dilom Ke");
        LOGGER.info("finished resequencung");
    }

    private static void reorder() {
        SongsService inflectedService = context.getBean(SongsService.class);
        inflectedService.sortBySpanId("Fevicol Se");
        LOGGER.info("finished resequencung");
    }


    private static void validateAll() throws InterruptedException{
        InflectedService masterService = context.getBean(InflectedService.class);
        List<Inflected> all = masterService.getAll();
        all.forEach(inf -> {
            if (inf.isOwnMeaning() && StringUtils.isEmpty(inf.getCanonicalHindi())){
                LOGGER.info("{}-{} doesn't have canonical_hindi", inf.getInflectedHindi(), inf.getInflectedHindiIndex());
            }
        });

        masterService.validateAll();
        LOGGER.info("finished validating");
    }


    private static void songRepo(){
        SongRepository processor = context.getBean(SongRepository.class);
        Song newWords = processor.findOne("Alvida");
        System.out.println(newWords);
    }

    private static void newWordsInSong(){
        SongsService processor = context.getBean(SongsService.class);
        Set<String> newWords = processor.newWordsInSong("Alvida");
        System.out.println(newWords);
    }
}
