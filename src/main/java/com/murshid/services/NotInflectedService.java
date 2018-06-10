package com.murshid.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.dynamo.domain.NotInflected;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.NotInflectedRepository;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.models.CanonicalKey;
import com.murshid.models.DictionaryKey;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.InflectedConverter;
import com.murshid.models.converters.NotInflectedConverter;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.MurshidEntry;
import com.murshid.persistence.domain.PlattsEntry;
import com.murshid.persistence.domain.RekhtaEntry;
import com.murshid.persistence.domain.WikitionaryEntry;
import com.murshid.persistence.domain.views.NotInflectedKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Named
public class NotInflectedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotInflectedService.class);

    private static Gson gsonMapper = new Gson();


    public List<NotInflected> getAll(){

        ScanRequest scanRequest = new ScanRequest().withTableName("not_inflected");

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        return scanResult.getItems()
                .stream().map(NotInflectedConverter::fromAvMap)
                .collect(Collectors.toList());
    }

    /**
     * retrieves all NotInflected entries relevant for a song, but instead of in List form, in a Map<String, Object> form
     * that is suitable to be transformed into a Javascript object.
     * Then, writes them in String form into the Song's DynamoDB record.
     *
     * Also returns the NotInflected entries retrieved
     *
     * @param song      a Song model
     * @return          a Map<String, Object> similar easily transformable into a JS object
     */
    public Map<String, Object> generateNotInflectedEntries(Song song){
        List<NotInflected> notInflectedList = allEntriesForSong(song);
        Map<String, Object> result = new HashMap<>();
        notInflectedList.forEach(notInflected -> {
            Map<String, Object> value = new HashMap<>();
            value.put("hindi", notInflected.getHindi());
            value.put("urdu", notInflected.getUrdu());
            value.put("part_of_speech", notInflected.getPartOfSpeech());
            value.put("master_dictionary_key", notInflected.getMasterDictionaryKey().toMap());

            result.put(notInflected.getKey(), value);
        });
        song.setNotInflectedEntries(gsonMapper.toJson(result).toString());
        songRepository.save(song);

        return result;
    }

    /**
     * Retrieves all DynamoDB.NotInflected entries relevant for a Song.
     * The song is assumed to have the word_list_master member populated
     * @param song         a song model
     * @return             a list (not necessarily ordered) of said master keys
     */
    public List<NotInflected> allEntriesForSong(Song song){

        if (song.getDictionaryEntriesNotInflected() != null) {

            //collect all master keys, without repetition
            Set<NotInflectedKey> mks = song.getWordListNotInflected()
                    .stream().map(wlm -> wlm.getNotInflectedKey())
                    .collect(Collectors.toSet());

            return mks.stream().map(mk ->
                    notInflectedRepository.findOne(mk.getHindi(), mk.getHindiIndex()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }else{
            return Collections.EMPTY_LIST;
        }
    }

    public void validateAll(){

        ScanRequest scanRequest = new ScanRequest().withTableName("not_inflected");

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        List<NotInflected> masters = scanResult.getItems()
                .stream().map(NotInflectedConverter::fromAvMap)
                .collect(Collectors.toList());



        for(NotInflected master: masters){
            LOGGER.info("NotInflected=" + master);
            if (!isValid(master)){
                throw new RuntimeException(String.format("not_inflected entry invalid: hindi=%s index=%s", master.getHindi(), master.getHindiIndex()));
            }
            save(master);
        }


    }

    /**
     * Parses DynameDB.NotInflected in search of the minimum possible next index for that hindi word
     */
    public int suggestNewIndex(String hindi){
        int index = -1;
        Iterator<Item> it = notInflectedRepository.findByHindi(hindi);
        while(it.hasNext()){
            Item item = it.next();
            index = Math.max(index, item.getInt("hindi_index"));
        }
        return index + 1;
    }



    public List<NotInflected> getByHindi(@Nonnull String hindi) {

        Map<String, AttributeValue> expressionAttributeValues =  new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":hindi", new AttributeValue().withS(hindi));

        ScanRequest scanRequest = new ScanRequest()
        .withTableName("inflected").withFilterExpression( "hindi = :hindi")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult scanResult = DynamoAccessor.client.scan(scanRequest);
        return scanResult.getItems()
                .stream().map(NotInflectedConverter::fromAvMap)
                .collect(Collectors.toList());
    }


    public boolean exists(String hindiWord, int index){
        return notInflectedRepository.findOne(hindiWord, index).isPresent();
    }

    public boolean save(NotInflected notInflected){
        try {
            notInflected.setUrdu(spellCheckService.passMultipleWordsToUrdu(notInflected.getHindi()));
            notInflectedRepository.save(notInflected);
        }catch (RuntimeException ex){
            LOGGER.error("error saving NotInflected entry {}", ex.getMessage());
            return false;
        }
        return true;
    }


    public boolean isValid(NotInflected master) {
        if (master.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (master.getHindi() == null) {
            LOGGER.info("not_inflected hindi cannot be null");
            return false;
        }

        if (!spellCheckService.wordsExist(master.getHindi())){
            LOGGER.info("the not_inflected hindi  {} has words that do not exist in spell_check ", master.getHindi());
            return false;
        }

        if (master.getPartOfSpeech() == null) {
            LOGGER.info("part of speech cannot be null");
            return false;
        }

        if (!validateCanonicalKeys(master)){
            LOGGER.info("some of the canonical keys are not present or incorrect");
            return false;
        }

        return true;
    }

    /**
     * checks if all canonical keys contained in Master (if any)
     * really exist in the respective entities
     * @param master    the Master record
     * @return          true if all canonical keys exist, false otherwise
     */
    public boolean validateCanonicalKeys(NotInflected master){

        return true;
    }

    @Inject
    private NotInflectedRepository notInflectedRepository;

    @Inject
    private SongRepository songRepository;


    @Inject
    private WikitionaryService wikitionaryService;

    @Inject
    private PlattsService plattsService;

    @Inject
    private MurshidService murshidService;

    @Inject
    private RekhtaService rekhtaService;

    @Inject
    private SpellCheckService spellCheckService;



}
