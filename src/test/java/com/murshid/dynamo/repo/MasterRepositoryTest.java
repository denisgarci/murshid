package com.murshid.dynamo.repo;

import com.google.common.collect.Lists;
import com.murshid.dynamo.domain.Master;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MasterRepository.class)
public class MasterRepositoryTest {

    @Test
    public void findOne() throws Exception {
        Optional<Master> master = masterRepository.findOne("दिल", 0);

        assertTrue(master.isPresent());
        assertEquals(master.get().getUrduSpelling(), "دل");

    }

    @Test
    public void save() throws Exception {
        Master master = new Master().setAccidence(Lists.newArrayList(Accidence.FEMININE, Accidence.SINGULAR))
                .setHindiWord("कहानी")
                .setPartOfSpeech(PartOfSpeech.NOUN)
                .setCanonicalKeys(Lists.newArrayList(
                        new CanonicalKey().setWord("कहानी").setWordIndex(0).setDictionarySource(DictionarySource.WIKITIONARY),
                        new CanonicalKey().setWord("कहानी").setWordIndex(0).setDictionarySource(DictionarySource.REKHTA),
                        new CanonicalKey().setWord("कहानी").setWordIndex(0).setDictionarySource(DictionarySource.PRATTS)
                ))
                .setUrduSpelling("کہانی")
                .setWordIndex(0);
        masterRepository.save(master);

    }




    @Inject
    protected MasterRepository masterRepository;

}
