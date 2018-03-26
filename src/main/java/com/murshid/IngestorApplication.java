package com.murshid;


import com.murshid.persistence.domain.HindiWord;
import com.murshid.services.HindiWordsService;
import com.murshid.services.SongProcesspor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Set;

@SpringBootApplication
public class IngestorApplication {

    private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(IngestorApplication.class, args);

		//insertWord();
        //newWordsInSong();

//
//
//        GonzaloRepository gonzaloRepository = context.getBean(GonzaloRepository.class);
//
//        DictionaryKey dictionaryKey = new DictionaryKey().setWord("बैरीयां").setWordIndex(0);
//
//        CanonicalKey canonicalKey = new CanonicalKey().setWord("बैरी").setDictionarySource(DictionarySource.PRATTS);
//
//		List<Accidence> accidence = Lists.newArrayList(Accidence.MASCULINE, Accidence.PLURAL_NUMBER);
//
//        GonzaloEntry ge = gonzaloRepository.save(new GonzaloEntry().setAccidence(accidence)
//                .setUrduWord("بيریاں")
//				.setDictionaryKey(dictionaryKey)
//                .setCanonicalKey(canonicalKey)
//				.setMeaning("enemies (poetic plural of बैरी, enemy)"));


	}

	private static void insertWord(){
        HindiWordsService hindiWordsService = context.getBean(HindiWordsService.class);
        HindiWord hindiWord = hindiWordsService.upsert("बैरीयां");
        System.out.println(hindiWord);

    }

    private static void newWordsInSong(){
        SongProcesspor processor = context.getBean(SongProcesspor.class);
        Set<String> newWords = processor.newWordsInSong("Alvida");
        System.out.println(newWords);
    }
}
