package com.murshid.morphology;

import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;

import java.util.Set;
import static org.junit.Assert.*;

public class NounCanonizerTest {

    @Test
    public void masculineNounsInA() throws Exception {

        String sonCanonical = "बेटा";

        String sonInflected = "बेटे";
        Set<CanonicalResult> results = NounCanonizer.process(sonInflected);

        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN, PartOfSpeech.ADJECTIVE))
                        .setCanonicalForm(sonCanonical).setAccidence(Sets.newHashSet(Accidence.SINGULAR, Accidence.OBLIQUE)),
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN, PartOfSpeech.ADJECTIVE))
                        .setCanonicalForm(sonCanonical).setAccidence(Sets.newHashSet(Accidence.SINGULAR, Accidence.VOCATIVE)),
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN, PartOfSpeech.ADJECTIVE))
                        .setCanonicalForm(sonCanonical).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.DIRECT))
        );
        assertTrue(results.containsAll(expected));

        String sonPluralOblique = "बेटों";
        results = NounCanonizer.process(sonPluralOblique);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN))
                        .setCanonicalForm(sonCanonical).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.OBLIQUE))
        );
        assertTrue(results.containsAll(expected));

        String sonPluralVocative = "बेटो";
        results = NounCanonizer.process(sonPluralVocative);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN))
                        .setCanonicalForm(sonCanonical).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.VOCATIVE))
        );
        assertTrue(results.containsAll(expected));

    }

    @Test
    public void feminineNounsInI() throws Exception {

        String daughterCanonical = "बेटी";

        String daughterObliquePlural = "बेटियों";
        Set<CanonicalResult> results = NounCanonizer.process(daughterObliquePlural);
        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN))
                        .setCanonicalForm(daughterCanonical).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.OBLIQUE))
        );
        assertTrue(results.containsAll(expected));

        String daughterDirectPlural = "बेटियाँ";
        results = NounCanonizer.process(daughterDirectPlural);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN))
                        .setCanonicalForm(daughterCanonical).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.DIRECT))
        );
        assertTrue(results.containsAll(expected));

    }


    @Test
    public void masculinesThatShortenUU() throws Exception {

        String thiefCanonical = "बाकू";

        String thievesPluralOblique = "बाकुओं";
        Set<CanonicalResult> results = NounCanonizer.process(thievesPluralOblique);
        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN))
                        .setCanonicalForm(thiefCanonical).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.OBLIQUE))
        );
        assertTrue(results.containsAll(expected));
    }

    @Test
    public void feminineNounsNotInI() throws Exception {

        String sisterCanonical = "बहन";

        String sisterPluralDirect = "बहनें";
        Set<CanonicalResult> results = NounCanonizer.process(sisterPluralDirect);
        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN))
                        .setCanonicalForm(sisterCanonical).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.DIRECT))
        );
        assertTrue(results.containsAll(expected));

        String sisterPluralOblique = "बहनों";
        results = NounCanonizer.process(sisterPluralOblique);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN))
                        .setCanonicalForm(sisterCanonical).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.OBLIQUE))
        );
        assertTrue(results.containsAll(expected));

        String sisterPluralVocative = "बहनो";
        results = NounCanonizer.process(sisterPluralVocative);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(Sets.newHashSet(PartOfSpeech.NOUN))
                        .setCanonicalForm(sisterCanonical).setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.VOCATIVE))
        );
        assertTrue(results.containsAll(expected));
    }

}
