package com.murshid.dynamo;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.Lists;
import com.murshid.dynamo.domain.Master;
import com.murshid.models.CanonicalKey;
import com.murshid.models.converters.MasterConverter;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;

public class MasterConverterTest {

    @Test
    public void convertToAvMap() throws Exception {

        List<Accidence> accidenceList = Lists.newArrayList(Accidence.FEMININE, Accidence.SINGULAR);
        List<CanonicalKey> canonicalKeysList = Lists.newArrayList(
                new CanonicalKey().setWordIndex(0).setDictionarySource(DictionarySource.GONZALO).setHindiWord("भी"),
                new CanonicalKey().setWordIndex(0).setDictionarySource(DictionarySource.PRATTS).setHindiWord("भी"));


        Master master = new Master()
                .setWordIndex(0)
                .setHindiWord("भी")
                .setUrduSpelling("بھی")
                .setPartOfSpeech(PartOfSpeech.ADVERB)
                .setAccidence(accidenceList)
                .setCanonicalKeys(canonicalKeysList);

        Map<String, AttributeValue> avMap = MasterConverter.convertToAvMap(master);

        assertEquals(avMap.get("hindiWord").getS(), "भी");
        assertEquals(avMap.get("urduSpelling").getS(), "بھی");

        List<Accidence> accAvs = avMap.get("accidence").getL().stream().map(av ->  Accidence.valueOf(av.getS())).collect(
                Collectors.toList());
        assertEquals(accAvs, accidenceList);

        List<CanonicalKey> cksAvs = avMap.get("canonicalKeys").getL().stream().map(av -> CanonicalKey.fromAvMap(av.getM())).collect(
                Collectors.toList());
        assertEquals(cksAvs, canonicalKeysList);
    }

    @Test
    public void convertFromAvMap() throws Exception {
        Map<String, AttributeValue> avMap = new HashMap<>();
        avMap.put("hindiWord", new AttributeValue("भी"));
        avMap.put("urduSpelling", new AttributeValue("بھی"));
        avMap.put("partOfSpeech", new AttributeValue(PartOfSpeech.ADVERB.name()));
        AttributeValue wordIndex = new AttributeValue();
        wordIndex.setN("3");
        avMap.put("wordIndex", wordIndex);

        //create Dictionary Sources
        Map<String, AttributeValue> ckMap1 = new HashMap<>();
        ckMap1.put("dictionarySource", new AttributeValue().withS(DictionarySource.GONZALO.name()));
        ckMap1.put("hindiWord", new AttributeValue("भी"));
        ckMap1.put("wordIndex", new AttributeValue().withN(Integer.toString(0)));
        AttributeValue ck1Av = new AttributeValue().withM(ckMap1);

        Map<String, AttributeValue> ckMap2 = new HashMap<>();
        ckMap2.put("dictionarySource", new AttributeValue().withS(DictionarySource.PRATTS.name()));
        ckMap2.put("hindiWord", new AttributeValue("भी"));
        ckMap2.put("wordIndex", new AttributeValue().withN(Integer.toString(0)));
        AttributeValue ck2Av = new AttributeValue().withM(ckMap2);

        AttributeValue cksAv = new AttributeValue();
        cksAv.setL(Lists.newArrayList(ck1Av, ck2Av));

        //create Accidences
        AttributeValue avAcc1 = new AttributeValue(Accidence.FEMININE.name());
        AttributeValue avAcc2 = new AttributeValue(Accidence.SINGULAR.name());
        AttributeValue avAccs = new AttributeValue();
        avAccs.setL(Lists.newArrayList(avAcc1, avAcc2));

        avMap.put("canonicalKeys", cksAv);
        avMap.put("accidence", avAccs);

        Master master = MasterConverter.fromAvMap(avMap);

        assertNotNull(master);
        assertEquals(master.getHindiWord(), "भी");
        assertEquals(master.getUrduSpelling(), "بھی");
        assertEquals(master.getAccidence(), Lists.newArrayList(Accidence.FEMININE, Accidence.SINGULAR));
        assertEquals(master.getWordIndex(), 3);

        List<CanonicalKey> canonicalKeysList = Lists.newArrayList(
                new CanonicalKey().setWordIndex(0).setDictionarySource(DictionarySource.GONZALO).setHindiWord("भी"),
                new CanonicalKey().setWordIndex(0).setDictionarySource(DictionarySource.PRATTS).setHindiWord("भी"));
        assertEquals(master.getCanonicalKeys(), canonicalKeysList);
    }





}
