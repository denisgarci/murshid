package com.murshid;

import com.murshid.models.Accidence;
import com.murshid.models.DictionaryKey;
import com.murshid.models.GonzaloEntry;
import com.murshid.persistence.GonzaloRepository;
import com.murshid.services.SongProcesspor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Set;

@SpringBootApplication
public class IngestorApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(IngestorApplication.class, args);

		SongProcesspor processor = context.getBean(SongProcesspor.class);
		Set<String> newWords = processor.newWordsInSong("Alvida");


        GonzaloRepository gonzaloRepository = context.getBean(GonzaloRepository.class);

        DictionaryKey dictionaryKey = new DictionaryKey();
        dictionaryKey.word ="बैरि";
		dictionaryKey.wordIndex =0 ;

		String accidence = Accidence.MASCULINE.name().concat(" ").concat(Accidence.SINGULAR_NUMBER.name());

        GonzaloEntry ge = gonzaloRepository.save(new GonzaloEntry().setAccidence(accidence)
							   .setDictionaryKey(dictionaryKey)
							   .setMeaning("enemy"));





		System.out.println(ge);
	}
}
