package com.murshid.services;

import com.google.common.collect.Sets;
import com.murshid.dynamo.domain.Master;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MasterServiceTest {

    @Test
    public void explode() throws Exception {

        CanonicalKey canonicalKey = new CanonicalKey().setDictionarySource(DictionarySource.PRATTS)
                .setCanonicalIndex(0).setCanonicalWord("बोलना");
        Master master = new Master().setCanonicalKeys(Sets.newHashSet(canonicalKey))
                .setHindiWord("बोलता")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);

         List<Master> result = masterService.explode(master);

        Master expectedMasculineVocativeSingular = new Master().setCanonicalKeys(Sets.newHashSet(canonicalKey))
                .setHindiWord("बोलते")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.VOCATIVE, Accidence.SINGULAR))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);


        Master expectedFeminineDirectPlural = new Master().setCanonicalKeys(Sets.newHashSet(canonicalKey))
                .setHindiWord("बोलतीं")
                .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);

        assertTrue(result.contains(expectedMasculineVocativeSingular));
        assertTrue(result.contains(expectedFeminineDirectPlural));

    }

    @Inject
    private MasterService masterService;

}
