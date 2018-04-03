package com.murshid.morphology;

import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;

import java.util.Set;
import static org.junit.Assert.*;

public class CanonizerTest {

    @Test
    public void process() throws Exception {
        String boyInflected = "बेटे";
        String boyCanonical = "बेटा";


        Set<CanonicalResult> results = Canonizer.process(boyInflected);
        Set<PartOfSpeech> possiblePos = Sets.newHashSet(PartOfSpeech.NOUN, PartOfSpeech.ADJECTIVE);

        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(possiblePos)
                        .setCanonicalForm(boyCanonical).setAccidence(com.google.common.collect.Sets.newHashSet(Accidence.SINGULAR, Accidence.OBLIQUE)),
                new CanonicalResult().setPossiblePOS(possiblePos)
                        .setCanonicalForm(boyCanonical).setAccidence(com.google.common.collect.Sets.newHashSet(Accidence.SINGULAR, Accidence.VOCATIVE)),
                new CanonicalResult().setPossiblePOS(possiblePos)
                        .setCanonicalForm(boyCanonical).setAccidence(com.google.common.collect.Sets.newHashSet(Accidence.PLURAL, Accidence.DIRECT))
        );

        assertEquals(results, expected);

    }

}
