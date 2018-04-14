package com.murshid;


import com.murshid.dynamo.domain.Master;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.CanonicalKey;
import com.murshid.services.MasterService;
import com.murshid.services.SongProcesspor;
import com.murshid.services.WikitionaryLetterIngestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication

public class IngestorApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryLetterIngestor.class);

    private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(IngestorApplication.class, args);

        sameCanonical();
        //songRepo();

//
//
//        GonzaloRepository gonzaloRepository = context.getBean(GonzaloRepository.class);
//
//        DictionaryKey dictionaryKey = new DictionaryKey().setCanonicalWord("बैरीयां").setCanonicalIndex(0);
//
//        CanonicalKey canonicalKey = new CanonicalKey().setCanonicalWord("बैरी").setDictionarySource(DictionarySource.PRATTS);
//
//		List<Accidence> accidence = Lists.newArrayList(Accidence.MASCULINE, Accidence.PLURAL_NUMBER);
//
//        GonzaloEntry ge = gonzaloRepository.save(new GonzaloEntry().setAccidence(accidence)
//                .setUrduWord("بيریاں")
//				.setDictionaryKey(dictionaryKey)
//                .setCanonicalKey(canonicalKey)
//				.setMeaning("enemies (poetic plural of बैरी, enemy)"));


	}

    private static void sameCanonical(){
        MasterService processor = context.getBean(MasterService.class);
        List<Master> entries = processor.getAllWords();

        for (Master master : entries){
            Set<String> canonicalWords = master.getCanonicalKeys().stream().map(CanonicalKey::getCanonicalWord).collect(Collectors.toSet());
            if (canonicalWords.size() > 1){
                LOGGER.info("the master hindiWord={} wordIndex={} has more than one canonical key", master.getHindiWord(), master.getWordIndex());
            }
        }
        LOGGER.info("finished");
    }


    private static void songRepo(){
        SongRepository processor = context.getBean(SongRepository.class);
        Song newWords = processor.findOne("Alvida");
        System.out.println(newWords);
    }


    private static void newWordsInSong(){
        SongProcesspor processor = context.getBean(SongProcesspor.class);
        Set<String> newWords = processor.newWordsInSong("Alvida");
        System.out.println(newWords);
    }
}
