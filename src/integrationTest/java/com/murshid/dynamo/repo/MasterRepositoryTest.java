package com.murshid.dynamo.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MasterRepository.class)
public class MasterRepositoryTest {

    @Test
    public void aa() throws Exception {
        assertTrue(true);
    }


//    @Test
//    public void findOne() throws Exception {
//        Optional<Master> master = masterRepository.findOne("दिल", 0);
//
//        assertTrue(master.isPresent());
//        assertEquals(master.get().getUrduSpelling(), "دل");
//
//    }
//
//    @Test
//    public void save() throws Exception {
//        Master master = new Master().setAccidence(Lists.newArrayList(Accidence.FEMININE, Accidence.SINGULAR))
//                .setCanonicalWord("कहानी")
//                .setPartOfSpeech(PartOfSpeech.NOUN)
//                .setCanonicalKeys(Lists.newArrayList(
//                        new CanonicalKey().setCanonicalWord("कहानी").setCanonicalIndex(0).setDictionarySource(DictionarySource.WIKITIONARY),
//                        new CanonicalKey().setCanonicalWord("कहानी").setCanonicalIndex(0).setDictionarySource(DictionarySource.REKHTA),
//                        new CanonicalKey().setCanonicalWord("कहानी").setCanonicalIndex(0).setDictionarySource(DictionarySource.PRATTS)
//                ))
//                .setUrduSpelling("کہانی")
//                .setCanonicalIndex(0);
//        masterRepository.save(master);
//
//    }
//
//
//
//
//    @Inject
//    protected MasterRepository masterRepository;

}
