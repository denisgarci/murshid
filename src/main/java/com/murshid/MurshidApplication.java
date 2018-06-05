package com.murshid;


import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.services.InflectedService;
import com.murshid.services.SongsService;
import com.murshid.services.WikitionaryLetterIngestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Set;

@SpringBootApplication

public class MurshidApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryLetterIngestor.class);

    private static ConfigurableApplicationContext context;

	public static void main(String[] args) throws Exception{
		context = SpringApplication.run(MurshidApplication.class, args);

       //List<Inflected> allInflected = getAll();



	}

    private static void generateHtml() throws InterruptedException{
        SongsService songsService = context.getBean(SongsService.class);
        songsService.generateSpans("Alvida");
        LOGGER.info("finished generating spans");
    }

    private static List<Inflected> getAll() throws InterruptedException{
        InflectedService inflectedService = context.getBean(InflectedService.class);
        try{
            List<Inflected> allInflected = inflectedService.getAll();
            for (Inflected inflected : allInflected){
//                    if (inflected.getAccidence()!= null && inflected.getAccidence().contains(Accidence.ABSOLUTIVE)) {
//                        inflected.getAccidence().remove(Accidence.ABSOLUTIVE);
//                        inflected.setPartOfSpeech(PartOfSpeech.VERB);
//                    }
//                    inflectedService.save(inflected);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }


        //allInflected.forEach(in -> inflectedService.save(in));
        LOGGER.info("finished deleting");
        return null;
    }

//    private static List<Inflected> getAll() throws InterruptedException{
//        InflectedService inflectedService = context.getBean(InflectedService.class);
//        List<Inflected> allInflected = inflectedService.getAll();
//        for (Inflected inflected : allInflected){
//            if (inflected.getCanonicalHindi().equals("चाहना"))
//            inflectedService.delete(inflected);
//        }
//
//        //allInflected.forEach(in -> inflectedService.save(in));
//        LOGGER.info("finished deleting");
//        return allInflected;
//    }

    private static void validateAll() throws InterruptedException{
        InflectedService masterService = context.getBean(InflectedService.class);
        masterService.validateAll();
        LOGGER.info("finished validating");
    }

    private static void createIndex() throws InterruptedException{
        Table table = DynamoAccessor.dynamoDB.getTable("inflected");

        Index index = table.createGSI(
                new CreateGlobalSecondaryIndexAction()
                        .withIndexName("idx-canonical_hindi")
                        .withKeySchema(
                                new KeySchemaElement("canonical_hindi", KeyType.HASH))
                        .withProvisionedThroughput(
                                new ProvisionedThroughput(3L, 3L))
                        .withProjection(
                                new Projection()
                                        .withProjectionType(ProjectionType.KEYS_ONLY)),
                new AttributeDefinition("canonical_hindi",
                                        ScalarAttributeType.S));
        index.waitForActive();
        LOGGER.info("index created");

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
