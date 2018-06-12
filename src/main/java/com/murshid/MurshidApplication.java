package com.murshid;


import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.NotInflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.DictionaryKey;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.*;
import com.murshid.persistence.repo.*;
import com.murshid.services.*;
import org.aspectj.weaver.ast.Not;
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

       //List<Inflected> allInflected = getAll();

        //replaceNuktas();
//        paoulate();
//        changeAbsolutives();
        //arrangePOS();
        // populateDictionaryEntries();

//        allDicsInEntries();

        //addMDKToInflected();
//        addMDKToNotInflected();

//        deleteCanonicalsFromNotInflected();



	}

    private static void replaceNuktas() throws InterruptedException{
        WikitionaryService wikitionaryService =  context.getBean(WikitionaryService.class);
        wikitionaryService.replaceNuktas();

        RekhtaService rekhtaService =  context.getBean(RekhtaService.class);
        rekhtaService.replaceNuktas();
        LOGGER.info("finished replacing spans");
    }

    private static void generateHtml() throws InterruptedException{
        SongsService songsService = context.getBean(SongsService.class);
        songsService.generateSpans("Alvida");
        LOGGER.info("finished generating spans");
    }




//    private static List<Inflected> addMDKToNotInflected() throws InterruptedException{
//        NotInflectedService notInflectedService = context.getBean(NotInflectedService.class);
//        MasterDictionaryService masterDictionaryService = context.getBean(MasterDictionaryService.class);
//        DictionaryEntryRepository dictionaryEntryRepository = context.getBean(DictionaryEntryRepository.class);
//
//        RekhtaService rekhtaService = context.getBean(RekhtaService.class);
//        MurshidService murshidService = context.getBean(MurshidService.class);
//        PlattsService plattsService = context.getBean(PlattsService.class);
//        WikitionaryService wikitionaryService = context.getBean(WikitionaryService.class);
//
//        try{
//            List<NotInflected> allInflected = notInflectedService.getAll();
//            for (NotInflected inflected : allInflected){
//                MasterDictionary md = new MasterDictionary();
//
//                Map<DictionarySource, PartOfSpeech> partOfSpeeches = new HashMap<>();
//                inflected.getCanonicalKeys().forEach(ck -> {
//                    if (ck.dictionarySource == DictionarySource.MURSHID){
//                        Optional<MurshidEntry> murshidEntry = murshidService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!murshidEntry.isPresent()){
//                            LOGGER.error("murshid canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getHindi(), inflected.getHindiIndex());
//                        }
//                        DictionaryEntry de = dictionaryEntryRepository.findByWordIndexAndDictionarySourceAndMasterDictionary_HindiWordAndMasterDictionary_PartOfSpeech(
//                                ck.getCanonicalIndex(), DictionarySource.MURSHID, murshidEntry.get().getHindiWord(), murshidEntry.get().getPartOfSpeech());
//                        if (de == null){
//                            LOGGER.error("cannot find in dictionary entries by wordIndex {} source {} hindiWordInMaster {}", ck.canonicalIndex, DictionarySource.MURSHID, murshidEntry.get().getHindiWord());
//                        }else{
//                            DictionaryKey dk = new DictionaryKey().setHindiWord(de.masterDictionary.getHindiWord()).setWordIndex(de.masterDictionary.getWordIndex());
//                            inflected.setMasterDictionaryKey(dk);
//                            notInflectedService.save(inflected);
//                        }
//
//                    }
//                    else if (ck.dictionarySource == DictionarySource.WIKITIONARY){
//                        Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!wikitionaryEntry.isPresent()){
//                            LOGGER.error("wikitionary canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getHindi(), inflected.getHindiIndex());
//                        }
//                        DictionaryEntry de = dictionaryEntryRepository.findByWordIndexAndDictionarySourceAndMasterDictionary_HindiWordAndMasterDictionary_PartOfSpeech(
//                                ck.getCanonicalIndex(), DictionarySource.WIKITIONARY, wikitionaryEntry.get().getHindiWord(), wikitionaryEntry.get().getPartOfSpeech());
//                        if (de == null){
//                            LOGGER.error("cannot find in dictionary entries by wordIndex {} source {} hindiWordInMaster {}", ck.canonicalIndex, DictionarySource.WIKITIONARY, wikitionaryEntry.get().getHindiWord());
//                        }else{
//                            DictionaryKey dk = new DictionaryKey().setHindiWord(de.masterDictionary.getHindiWord()).setWordIndex(de.masterDictionary.getWordIndex());
//                            inflected.setMasterDictionaryKey(dk);
//                            notInflectedService.save(inflected);
//                        }
//
//                    }
//                    else if (ck.dictionarySource == DictionarySource.PLATTS){
//                        Optional<PlattsEntry> plattsEntryOptional = plattsService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!plattsEntryOptional.isPresent()){
//                            LOGGER.error("platts canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getHindi(), inflected.getHindiIndex());
//                        }
//                        DictionaryEntry de = dictionaryEntryRepository.findByWordIndexAndDictionarySourceAndMasterDictionary_HindiWordAndMasterDictionary_PartOfSpeech(
//                                ck.getCanonicalIndex(), DictionarySource.PLATTS, plattsEntryOptional.get().getHindiWord(), plattsEntryOptional.get().getPartOfSpeech());
//                        if (de == null){
//                            LOGGER.error("cannot find in dictionary entries by wordIndex {} source {} hindiWordInMaster {}", ck.canonicalIndex, DictionarySource.PLATTS, plattsEntryOptional.get().getHindiWord());
//                        }else{
//                            DictionaryKey dk = new DictionaryKey().setHindiWord(de.masterDictionary.getHindiWord()).setWordIndex(de.masterDictionary.getWordIndex());
//                            inflected.setMasterDictionaryKey(dk);
//                            notInflectedService.save(inflected);
//                        }
//
//
//                    }
//                    else if (ck.dictionarySource == DictionarySource.REKHTA){
//                        Optional<RekhtaEntry> rekhtaEntry = rekhtaService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!rekhtaEntry.isPresent()){
//                            LOGGER.error("rekhta canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getHindi(), inflected.getHindiIndex());
//                        }
//                        DictionaryEntry de = dictionaryEntryRepository.findByWordIndexAndDictionarySourceAndMasterDictionary_HindiWordAndMasterDictionary_PartOfSpeech(
//                                ck.getCanonicalIndex(), DictionarySource.REKHTA, rekhtaEntry.get().getHindiWord(), rekhtaEntry.get().getPartOfSpeech());
//                        if (de == null){
//                            LOGGER.error("cannot find in dictionary entries by wordIndex {} source {} hindiWordInMaster {}", ck.canonicalIndex, DictionarySource.REKHTA, rekhtaEntry.get().getHindiWord());
//                        }else{
//                            DictionaryKey dk = new DictionaryKey().setHindiWord(de.masterDictionary.getHindiWord()).setWordIndex(de.masterDictionary.getWordIndex());
//                            inflected.setMasterDictionaryKey(dk);
//                            notInflectedService.save(inflected);
//                        }
//
//
//                    }
//
//                });
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//
//
//        //allInflected.forEach(in -> inflectedService.save(in));
//        LOGGER.info("finished analyzoong");
//        return null;
//    }

//    private static List<Inflected> addMDKToInflected() throws InterruptedException{
//        InflectedService inflectedService = context.getBean(InflectedService.class);
//        MasterDictionaryService masterDictionaryService = context.getBean(MasterDictionaryService.class);
//        DictionaryEntryRepository dictionaryEntryRepository = context.getBean(DictionaryEntryRepository.class);
//
//        RekhtaService rekhtaService = context.getBean(RekhtaService.class);
//        MurshidService murshidService = context.getBean(MurshidService.class);
//        PlattsService plattsService = context.getBean(PlattsService.class);
//        WikitionaryService wikitionaryService = context.getBean(WikitionaryService.class);
//
//        try{
//            List<Inflected> allInflected = inflectedService.getAll();
//            for (Inflected inflected : allInflected){
//                MasterDictionary md = new MasterDictionary();
//
//                Map<DictionarySource, PartOfSpeech> partOfSpeeches = new HashMap<>();
//                inflected.getCanonicalKeys().forEach(ck -> {
//                    if (ck.dictionarySource == DictionarySource.MURSHID){
//                        Optional<MurshidEntry> murshidEntry = murshidService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!murshidEntry.isPresent()){
//                            LOGGER.error("murshid canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex());
//                        }
//                        DictionaryEntry de = dictionaryEntryRepository.findByWordIndexAndDictionarySourceAndMasterDictionary_HindiWordAndMasterDictionary_PartOfSpeech(
//                                ck.getCanonicalIndex(), DictionarySource.MURSHID, murshidEntry.get().getHindiWord(), murshidEntry.get().getPartOfSpeech());
//                        if (de == null){
//                            LOGGER.error("cannot find in dictionary entries by wordIndex {} source {} hindiWordInMaster {}", ck.canonicalIndex, DictionarySource.MURSHID, murshidEntry.get().getHindiWord());
//                        }else{
//                            DictionaryKey dk = new DictionaryKey().setHindiWord(de.masterDictionary.getHindiWord()).setWordIndex(de.masterDictionary.getWordIndex());
//                            inflected.setMasterDictionaryKey(dk);
//                            inflectedService.save(inflected);
//                        }
//
//                    }
//                    else if (ck.dictionarySource == DictionarySource.WIKITIONARY){
//                        Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!wikitionaryEntry.isPresent()){
//                            LOGGER.error("wikitionary canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex());
//                        }
//                        DictionaryEntry de = dictionaryEntryRepository.findByWordIndexAndDictionarySourceAndMasterDictionary_HindiWordAndMasterDictionary_PartOfSpeech(
//                                ck.getCanonicalIndex(), DictionarySource.WIKITIONARY, wikitionaryEntry.get().getHindiWord(), wikitionaryEntry.get().getPartOfSpeech());
//                        if (de == null){
//                            LOGGER.error("cannot find in dictionary entries by wordIndex {} source {} hindiWordInMaster {}", ck.canonicalIndex, DictionarySource.WIKITIONARY, wikitionaryEntry.get().getHindiWord());
//                        }else{
//                            DictionaryKey dk = new DictionaryKey().setHindiWord(de.masterDictionary.getHindiWord()).setWordIndex(de.masterDictionary.getWordIndex());
//                            inflected.setMasterDictionaryKey(dk);
//                            inflectedService.save(inflected);
//                        }
//
//                    }
//                    else if (ck.dictionarySource == DictionarySource.PLATTS){
//                        Optional<PlattsEntry> plattsEntryOptional = plattsService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!plattsEntryOptional.isPresent()){
//                            LOGGER.error("platts canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex());
//                        }
//                        DictionaryEntry de = dictionaryEntryRepository.findByWordIndexAndDictionarySourceAndMasterDictionary_HindiWordAndMasterDictionary_PartOfSpeech(
//                                ck.getCanonicalIndex(), DictionarySource.PLATTS, plattsEntryOptional.get().getHindiWord(), plattsEntryOptional.get().getPartOfSpeech());
//                        if (de == null){
//                            LOGGER.error("cannot find in dictionary entries by wordIndex {} source {} hindiWordInMaster {}", ck.canonicalIndex, DictionarySource.PLATTS, plattsEntryOptional.get().getHindiWord());
//                        }else{
//                            DictionaryKey dk = new DictionaryKey().setHindiWord(de.masterDictionary.getHindiWord()).setWordIndex(de.masterDictionary.getWordIndex());
//                            inflected.setMasterDictionaryKey(dk);
//                            inflectedService.save(inflected);
//                        }
//
//
//                    }
//                    else if (ck.dictionarySource == DictionarySource.REKHTA){
//                        Optional<RekhtaEntry> rekhtaEntry = rekhtaService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!rekhtaEntry.isPresent()){
//                            LOGGER.error("rekhta canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex());
//                        }
//                        DictionaryEntry de = dictionaryEntryRepository.findByWordIndexAndDictionarySourceAndMasterDictionary_HindiWordAndMasterDictionary_PartOfSpeech(
//                                ck.getCanonicalIndex(), DictionarySource.REKHTA, rekhtaEntry.get().getHindiWord(), rekhtaEntry.get().getPartOfSpeech());
//                        if (de == null){
//                            LOGGER.error("cannot find in dictionary entries by wordIndex {} source {} hindiWordInMaster {}", ck.canonicalIndex, DictionarySource.REKHTA, rekhtaEntry.get().getHindiWord());
//                        }else{
//                            DictionaryKey dk = new DictionaryKey().setHindiWord(de.masterDictionary.getHindiWord()).setWordIndex(de.masterDictionary.getWordIndex());
//                            inflected.setMasterDictionaryKey(dk);
//                            inflectedService.save(inflected);
//                        }
//
//
//                    }
//
//                });
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//
//
//        //allInflected.forEach(in -> inflectedService.save(in));
//        LOGGER.info("finished analyzoong");
//        return null;
//    }

//    private static List<Inflected> arrangePOS() throws InterruptedException{
//        InflectedService inflectedService = context.getBean(InflectedService.class);
//        MasterDictionaryService masterDictionaryService = context.getBean(MasterDictionaryService.class);
//
//        RekhtaService rekhtaService = context.getBean(RekhtaService.class);
//        MurshidService murshidService = context.getBean(MurshidService.class);
//        PlattsService plattsService = context.getBean(PlattsService.class);
//        WikitionaryService wikitionaryService = context.getBean(WikitionaryService.class);
//
//        try{
//            List<Inflected> allInflected = inflectedService.getAll();
//            for (Inflected inflected : allInflected){
//                final MasterDictionary md = new MasterDictionary();
//                Map<DictionarySource, PartOfSpeech> partOfSpeeches = new HashMap<>();
//                inflected.getCanonicalKeys().forEach(ck -> {
//                    if (ck.dictionarySource == DictionarySource.MURSHID){
//                        Optional<MurshidEntry> murshidEntry = murshidService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!murshidEntry.isPresent()){
//                            LOGGER.error("murshid canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex());
//                        }else{
//                            partOfSpeeches.put(DictionarySource.MURSHID, murshidEntry.get().getPartOfSpeech());
//                            md.setPartOfSpeech(murshidEntry.get().getPartOfSpeech());
//                        }
//                        md.setMurhsidIndex(ck.canonicalIndex);
//
//                    }
//                    else if (ck.dictionarySource == DictionarySource.WIKITIONARY){
//                        Optional<WikitionaryEntry> wikitionaryEntry = wikitionaryService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!wikitionaryEntry.isPresent()){
//                            LOGGER.error("wikitionary canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex());
//                        }else{
//                            partOfSpeeches.put(DictionarySource.WIKITIONARY, wikitionaryEntry.get().getPartOfSpeech());
//                            md.setPartOfSpeech(wikitionaryEntry.get().getPartOfSpeech());
//                        }
//                        md.setWikitionaryIndex(ck.canonicalIndex);
//                    }
//                    else if (ck.dictionarySource == DictionarySource.PLATTS){
//                        Optional<PlattsEntry> plattsEntryOptional = plattsService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!plattsEntryOptional.isPresent()){
//                            LOGGER.error("platts canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex());
//                        }else{
//                            partOfSpeeches.put(DictionarySource.PLATTS, plattsEntryOptional.get().getPartOfSpeech());
//                            md.setPartOfSpeech(plattsEntryOptional.get().getPartOfSpeech());
//                        }
//                        md.setPlattsIndex(ck.canonicalIndex);
//                    }
//                    else if (ck.dictionarySource == DictionarySource.REKHTA){
//                        Optional<RekhtaEntry> rekhtaEntry = rekhtaService.findOne(ck.canonicalWord, ck.canonicalIndex);
//                        if (!rekhtaEntry.isPresent()){
//                            LOGGER.error("rekhta canonical entry expressed in inflected {} index {} but not in the dictionary", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex());
//                        }else{
//                            partOfSpeeches.put(DictionarySource.REKHTA, rekhtaEntry.get().getPartOfSpeech());
//                            md.setPartOfSpeech(rekhtaEntry.get().getPartOfSpeech());
//                        }
//                        md.setRekhtaIndex(ck.canonicalIndex);
//                    }
//                    if (Sets.newHashSet(partOfSpeeches.values()).size() > 1){
//                        LOGGER.error("inflected entry {} {} has more than one POS {}", inflected.getInflectedHindi(), inflected.getInflectedHindiIndex(), partOfSpeeches);
//                    }
//                    md.setHindiWord(inflected.getCanonicalHindi());
//                    md.setWordIndex(0);
//                    masterDictionaryService.save(md);
//                });
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//
//
//        //allInflected.forEach(in -> inflectedService.save(in));
//        LOGGER.info("finished deleting");
//        return null;
//    }

    private static void deleteCanonicalsFromNotInflected() {
	    NotInflectedService notInflectedService = context.getBean(NotInflectedService.class);
            List<NotInflected> allInflected = notInflectedService.getAll();
            for (NotInflected notInflected : allInflected){
                notInflectedService.save(notInflected);
            }
        LOGGER.info("finished deleting canonicals");
    }

//    private static List<Inflected> paoulate() throws InterruptedException{
//         InflectedService inflectedService = context.getBean(InflectedService.class);
//        MasterDictionaryService masterDictionaryService = context.getBean(MasterDictionaryService.class);
//        try{
//            List<Inflected> allInflected = inflectedService.getAll();
//            for (Inflected inflected : allInflected){
//                final MasterDictionary md = new MasterDictionary();
//                inflected.getCanonicalKeys().forEach(ck -> {
//                    if (ck.dictionarySource == DictionarySource.MURSHID){
//                        md.setMurhsidIndex(ck.canonicalIndex);
//                    }
//                    else if (ck.dictionarySource == DictionarySource.WIKITIONARY){
//                        md.setWikitionaryIndex(ck.canonicalIndex);
//                    }
//                    else if (ck.dictionarySource == DictionarySource.PLATTS){
//                        md.setPlattsIndex(ck.canonicalIndex);
//                    }
//                    else if (ck.dictionarySource == DictionarySource.REKHTA){
//                        md.setRekhtaIndex(ck.canonicalIndex);
//                    }
//                });
//                md.setDictionaryKey(new DictionaryKey().setHindiWord(inflected.getCanonicalHindi()));
//                masterDictionaryService.save(md);
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//
//
//        //allInflected.forEach(in -> inflectedService.save(in));
//        LOGGER.info("finished deleting");
//        return null;
//    }

//    private static List<Inflected> changeAbsolutives() throws InterruptedException{
//        InflectedService inflectedService = context.getBean(InflectedService.class);
//            List<Inflected> allInflected = inflectedService.getAll();
//            for (Inflected inflected : allInflected){
//                try {
//                    if (inflected.getAccidence() != null && inflected.getAccidence().contains(Accidence.ABSOLUTIVE)) {
//                        inflected.getAccidence().remove(Accidence.ABSOLUTIVE);
//                        inflected.setPartOfSpeech(PartOfSpeech.VERB);
//                    }
//                    inflectedService.save(inflected);
//                }catch (Exception ex){
//                    LOGGER.info("failed inflected {}", inflected);
//                    ex.printStackTrace();
//                }
//            }
//
//
//
//        //allInflected.forEach(in -> inflectedService.save(in));
//        LOGGER.info("finished deleting");
//        return null;
//    }

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
