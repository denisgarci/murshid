package com.murshid;


import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.services.InflectedService;
import com.murshid.services.SongsService;
import com.murshid.services.WikitionaryLetterIngestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Set;

@SpringBootApplication

public class IngestorApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryLetterIngestor.class);

    private static ConfigurableApplicationContext context;

	public static void main(String[] args) throws Exception{
		context = SpringApplication.run(IngestorApplication.class, args);

        //validateAll();
        //sameCanonical();
        //songRepo();
        //createIndex();
        //findByIndex();
        //createIndex();
//        changeEnums();
//
//
//        MurshidRepository gonzaloRepository = context.getBean(MurshidRepository.class);
//
//        DictionaryKey dictionaryKey = new DictionaryKey().setCanonicalWord("बैरीयां").setCanonicalIndex(0);
//
//        CanonicalKey canonicalKey = new CanonicalKey().setCanonicalWord("बैरी").setDictionarySource(DictionarySource.PRATTS);
//
//		List<Accidence> accidence = Lists.newArrayList(Accidence.MASCULINE, Accidence.PLURAL_NUMBER);
//
//        MurshidEntry ge = gonzaloRepository.save(new MurshidEntry().setAccidence(accidence)
//                .setUrduWord("بيریاں")
//				.setDictionaryKey(dictionaryKey)
//                .setCanonicalKey(canonicalKey)
//				.setMeaning("enemies (poetic plural of बैरी, enemy)"));


	}




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
