package com.murshid;


import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.murshid.dynamo.domain.NotInflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.InflectedRepository;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.InflectedConverter;
import com.murshid.services.*;
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

      //clean();

        //replaceNuktas();
//        paoulate();
//        changeAbsolutives();
        //arrangePOS();
        // populateDictionaryEntries();

//        allDicsInEntries();

        //addMDKToInflected();
//        addMDKToNotInflected();

//        deleteCanonicalsFromNotInflected();

        //deleteIndex();
        //masterDictionaryId();
        //verifyAccidences();



	}

    private static void generateHtml() throws InterruptedException{
        SongsService songsService = context.getBean(SongsService.class);
        songsService.generateSpans("Alvida");
        LOGGER.info("finished generating spans");
    }



    private static void deleteCanonicalsFromNotInflected() {
	    NotInflectedService notInflectedService = context.getBean(NotInflectedService.class);
            List<NotInflected> allInflected = notInflectedService.getAll();
            for (NotInflected notInflected : allInflected){
                notInflectedService.save(notInflected);
            }
        LOGGER.info("finished deleting canonicals");
    }

    private static void verifyAccidences() throws InterruptedException{
        InflectedRepository inflectedService = context.getBean(InflectedRepository.class);
        List<Map<String, AttributeValue>> avs = inflectedService.scanAll();
        avs.forEach(av -> {
            if (av.containsKey("accidence")){
                AttributeValue value = av.get("accidence");
                if (value.getL() == null){
                    LOGGER.info( "value {} does not have a list in accidences", value);
                }
            }


            InflectedConverter.fromAvMap(av).getAccidence();
        });
        LOGGER.info("finished");
    }


    private static void validateAll() throws InterruptedException{
        InflectedService masterService = context.getBean(InflectedService.class);
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
