package com.murshid.dynamo;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.murshid.dynamo.domain.Master;
import com.murshid.models.CanonicalKey;
import com.murshid.models.converters.MasterConverter;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class MasterConverterTest {

    @Test
    public void convertToAvMap() throws Exception {

        Set<Accidence> accidenceList = Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR);
        Set<CanonicalKey> canonicalKeysList = Sets.newHashSet(
                new CanonicalKey().setCanonicalIndex(0).setDictionarySource(DictionarySource.MURSHID).setCanonicalWord("भी"),
                new CanonicalKey().setCanonicalIndex(0).setDictionarySource(DictionarySource.PLATTS).setCanonicalWord("भी"));


        Master master = new Master()
                .setWordIndex(0)
                .setHindiWord("भी")
                .setPartOfSpeech(PartOfSpeech.ADVERB)
                .setAccidence(accidenceList)
                .setCanonicalKeys(canonicalKeysList);

        Map<String, AttributeValue> avMap = MasterConverter.convertToAvMap(master);

        assertEquals(avMap.get("hindi_word").getS(), "भी");

        Set<Accidence> accAvs = avMap.get("accidence").getL().stream().map(av ->  Accidence.valueOf(av.getS())).collect(
                Collectors.toSet());
        assertEquals(accAvs, accidenceList);

        Set<CanonicalKey> cksAvs = avMap.get("canonical_keys").getL().stream().map(av -> CanonicalKey.fromAvMap(av.getM())).collect(
                Collectors.toSet());
        assertEquals(cksAvs, canonicalKeysList);
    }

    @Test
    public void convertFromAvMap() throws Exception {
        Map<String, AttributeValue> avMap = new HashMap<>();
        avMap.put("hindi_word", new AttributeValue("भी"));
        avMap.put("part_of_speech", new AttributeValue(PartOfSpeech.ADVERB.name()));
        AttributeValue wordIndex = new AttributeValue();
        wordIndex.setN("3");
        avMap.put("word_index", wordIndex);
        avMap.put("canonical_word", new AttributeValue("भी"));

        //create Dictionary Sources
        Map<String, AttributeValue> ckMap1 = new HashMap<>();
        ckMap1.put("dictionary_source", new AttributeValue().withS(DictionarySource.MURSHID.name()));
        ckMap1.put("canonical_word", new AttributeValue("भी"));
        ckMap1.put("canonical_index", new AttributeValue().withN(Integer.toString(0)));
        AttributeValue ck1Av = new AttributeValue().withM(ckMap1);

        Map<String, AttributeValue> ckMap2 = new HashMap<>();
        ckMap2.put("dictionary_source", new AttributeValue().withS(DictionarySource.PLATTS.name()));
        ckMap2.put("canonical_word", new AttributeValue("भी"));
        ckMap2.put("canonical_index", new AttributeValue().withN(Integer.toString(0)));
        AttributeValue ck2Av = new AttributeValue().withM(ckMap2);

        AttributeValue cksAv = new AttributeValue();
        cksAv.setL(Lists.newArrayList(ck1Av, ck2Av));

        //create Accidences
        AttributeValue avAcc1 = new AttributeValue(Accidence.FEMININE.name());
        AttributeValue avAcc2 = new AttributeValue(Accidence.SINGULAR.name());
        AttributeValue avAccs = new AttributeValue();
        avAccs.setL(Sets.newHashSet(avAcc1, avAcc2));

        avMap.put("canonical_keys", cksAv);
        avMap.put("accidence", avAccs);

        Master master = MasterConverter.fromAvMap(avMap);

        assertNotNull(master);
        assertEquals(master.getHindiWord(), "भी");
        assertEquals(master.getAccidence(), Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR));
        assertEquals(master.getWordIndex(), 3);

        Set<CanonicalKey> canonicalKeysList = Sets.newHashSet(
                new CanonicalKey().setCanonicalIndex(0).setDictionarySource(DictionarySource.MURSHID).setCanonicalWord("भी"),
                new CanonicalKey().setCanonicalIndex(0).setDictionarySource(DictionarySource.PLATTS).setCanonicalWord("भी"));
        assertEquals(master.getCanonicalKeys(), canonicalKeysList);
    }





}
