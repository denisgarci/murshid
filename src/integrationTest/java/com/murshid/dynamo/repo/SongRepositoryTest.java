package com.murshid.dynamo.repo;

import com.murshid.dynamo.domain.Song;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SongRepository.class)
public class SongRepositoryTest {

    @Test
    public void findOne() throws Exception {
        Song song = songRepository.findOne("Alvida");

        assertNotNull(song);
        assertEquals(song.getTitleHindi(), "अलविदा");

    }

    @Inject
    protected SongRepository songRepository;

}
