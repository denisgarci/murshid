package com.murshid.services;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.murshid.dynamo.domain.Song;
import com.murshid.dynamo.repo.SongRepository;
import com.murshid.persistence.domain.MasterDictionary;
import com.murshid.persistence.domain.NotInflected;
import com.murshid.persistence.domain.views.NotInflectedKey;
import com.murshid.persistence.domain.views.NotInflectedView;
import com.murshid.persistence.domain.views.SongWordsToNotInflectedTable;
import com.murshid.persistence.repo.NotInflectedRepositoryDB;
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

    private NotInflectedRepositoryDB notInflectedRepository;
    private SongRepository songRepository;
    private SpellCheckService spellCheckService;

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
            value.put("hindi", notInflected.getNotInflectedKey().getHindi());
            value.put("urdu", notInflected.getUrdu());
            value.put("part_of_speech", notInflected.getPartOfSpeech());
            value.put("master_dictionary_key", ImmutableMap.of(
                    "hindi_word",  notInflected.getNotInflectedKey().hindi,
                    "word_index",  notInflected.getNotInflectedKey().hindiIndex
            ));

            result.put(notInflected.getKey(), value);
        });
        song.setNotInflectedEntries(gsonMapper.toJson(result));
        songRepository.save(song);

        return result;
    }

    /**
     * Retrieves all DynamoDB.NotInflected entries relevant for a Song.
     * The song is assumed to have the word_list_master member populated
     * @param song         a song model
     * @return             a list (not necessarily ordered) of said master keys
     */
    private List<NotInflected> allEntriesForSong(Song song){

        if (song.getWordListNotInflected() != null) {

            //collect all master keys, without repetition
            Set<NotInflectedKey> mks = song.getWordListNotInflected()
                    .stream().map(SongWordsToNotInflectedTable::getNotInflectedKey)
                    .collect(Collectors.toSet());

            return mks.stream()
                    .map(mk -> notInflectedRepository.findByNotInflectedKey_HindiAndNotInflectedKey_HindiIndex(mk.getHindi(), mk.getHindiIndex())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        String.format("the not-inflected entry %s-%s in the song, is not in the not_inflected repository ", mk.getHindi(), mk.getHindiIndex())))
                    ).collect(Collectors.toList());
        }else{
            return Collections.emptyList();
        }
    }

    /**
     * Parses DynameDB.NotInflected in search of the minimum possible next index for that hindi word
     */
    public int suggestNewIndex(String hindi){
        int index = -1;
        Iterator<NotInflected> it = notInflectedRepository.findByNotInflectedKey_Hindi(hindi).iterator();
        while(it.hasNext()){
            NotInflected item = it.next();
            index = Math.max(index, item.getNotInflectedKey().hindiIndex);
        }
        return index + 1;
    }



    public List<NotInflected> getByHindi(@Nonnull String hindi) {
        return notInflectedRepository.findByNotInflectedKey_Hindi(hindi);
    }


    public boolean exists(String hindiWord, int index){
        return notInflectedRepository.findByNotInflectedKey_HindiAndNotInflectedKey_HindiIndex(hindiWord, index).isPresent();
    }

    public boolean save(NotInflected notInflected){
        try {
            notInflected.setUrdu(spellCheckService.passMultipleWordsToUrdu(notInflected.getNotInflectedKey().hindi));
            notInflectedRepository.save(notInflected);
        }catch (RuntimeException ex){
            LOGGER.error("error saving NotInflected entry {}", ex.getMessage());
            return false;
        }
        return true;
    }

    public NotInflected fromView(NotInflectedView notInflectedView, MasterDictionary masterDictionary){
        NotInflected notInflected = new NotInflected();
        notInflected.setMasterDictionary(masterDictionary);
        NotInflected.NotInflectedKey inflectedKey = new NotInflected.NotInflectedKey();
        inflectedKey.setHindi(notInflectedView.getHindi());
        inflectedKey.setHindiIndex(suggestNewIndex(notInflectedView.getHindi()));
        notInflected.setNotInflectedKey(inflectedKey);
        notInflected.setUrdu(notInflectedView.getUrdu());
        notInflected.setPartOfSpeech(notInflectedView.getPartOfSpeech());
        return notInflected;
    }


    public boolean isValid(NotInflectedView master) {
        if (master.getPartOfSpeech() == null) {
            LOGGER.info("partOfSpeech cannot be null");
            return false;
        }

        if (master.getHindi() == null) {
            LOGGER.info("not_inflected hindi cannot be null");
            return false;
        }

        if (spellCheckService.wordsDontExist(master.getHindi())){
            LOGGER.info("the not_inflected hindi  {} has words that do not exist in spell_check ", master.getHindi());
            return false;
        }

        if (master.getPartOfSpeech() == null) {
            LOGGER.info("part of speech cannot be null");
            return false;
        }

        return true;
    }

    @Inject
    public void setNotInflectedRepository(NotInflectedRepositoryDB notInflectedRepository) {
        this.notInflectedRepository = notInflectedRepository;
    }

    @Inject
    public void setSongRepository(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Inject
    public void setSpellCheckService(SpellCheckService spellCheckService) {
        this.spellCheckService = spellCheckService;
    }



}
