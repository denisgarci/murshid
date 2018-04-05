package com.murshid.morphology;

import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class VerbalCanonizerTest {

    @Test
    public void imperative() throws Exception {

        String speakcanonical = "बोलना";

        String imperativeFormal1 = "बोलिए";
        Set<CanonicalResult> results = VerbalCanonizer.process(imperativeFormal1);

        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.VERB))
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.FORMAL))
        );
        assertTrue(results.containsAll(expected));

        String imperativeFormal2 = "बोलिये";
        results = VerbalCanonizer.process(imperativeFormal2);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.VERB))
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.FORMAL))
        );
        assertTrue(results.containsAll(expected));


        String imperativeInformal = "बोलो";
        results = VerbalCanonizer.process(imperativeInformal);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.VERB))
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.FAMILIAR))
        );
        assertTrue(results.containsAll(expected));


        String imperativeIntimate = "बोल";
        results = VerbalCanonizer.process(imperativeIntimate);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.VERB))
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.INTIMATE))
        );
        assertTrue(results.containsAll(expected));


    }

}
