package com.murshid.morphology;

import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class VerbalCanonizerTest {

    @Test
    public void infinitive() throws Exception {

        String speakcanonical = "बोलना";

        String speakInfinitive = speakcanonical;
        Set<CanonicalResult> results = VerbalCanonizer.process(speakInfinitive);

        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.INFINITIVE)
                        .setCanonicalForm(speakcanonical));
        assertTrue(results.containsAll(expected));
    }

    @Test
    public void imperative() throws Exception {

        String speakcanonical = "बोलना";

        String imperativeFormal1 = "बोलिए";
        Set<CanonicalResult> results = VerbalCanonizer.process(imperativeFormal1);

        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.FORMAL))
        );
        assertTrue(results.containsAll(expected));

        String imperativeFormal2 = "बोलिये";
        results = VerbalCanonizer.process(imperativeFormal2);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.FORMAL))
        );
        assertTrue(results.containsAll(expected));


        String imperativeInformal = "बोलो";
        results = VerbalCanonizer.process(imperativeInformal);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.FAMILIAR))
        );
        assertTrue(results.containsAll(expected));


        String imperativeIntimate = "बोल";
        results = VerbalCanonizer.process(imperativeIntimate);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERATIVE, Accidence.INTIMATE))
        );
        assertTrue(results.containsAll(expected));

    }

    @Test
    public void absolutive() throws Exception {

        String speakcanonical = "बोलना";

        String absolutive1 = "बोलकर";
        Set<CanonicalResult> results = VerbalCanonizer.process(absolutive1);

        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.ABSOLUTIVE)
                        .setCanonicalForm(speakcanonical));
        assertTrue(results.containsAll(expected));

        String absolutive2 = "बोलके";
        results = VerbalCanonizer.process(absolutive2);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.ABSOLUTIVE)
                        .setCanonicalForm(speakcanonical));
        assertTrue(results.containsAll(expected));


        String absolutive3 = "बोल";
        results = VerbalCanonizer.process(absolutive3);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.ABSOLUTIVE)
                        .setCanonicalForm(speakcanonical));
        assertTrue(results.containsAll(expected));
    }

    @Test
    public void future() throws Exception {

        String speakcanonical = "बोलना";

        String future1 = "बोलूँगा";
        Set<CanonicalResult> results = VerbalCanonizer.process(future1);

        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._1ST, Accidence.SINGULAR, Accidence.MASCULINE))
        );
        assertTrue(results.containsAll(expected));

        String future2 = "बोलेगा";
        results = VerbalCanonizer.process(future2);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._2ND, Accidence.SINGULAR, Accidence.MASCULINE)),
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._3RD, Accidence.SINGULAR, Accidence.MASCULINE))
        );
        assertTrue(results.containsAll(expected));


        String future3 = "बोलेंगे";
        results = VerbalCanonizer.process(future3);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._1ST, Accidence.PLURAL, Accidence.MASCULINE)),
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._3RD, Accidence.PLURAL, Accidence.MASCULINE))

        );
        assertTrue(results.containsAll(expected));

        String future4 = "बोलोगे";
        results = VerbalCanonizer.process(future4);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._2ND, Accidence.PLURAL, Accidence.MASCULINE))
        );
        assertTrue(results.containsAll(expected));

        String future5 = "बोलूँगी";
        results = VerbalCanonizer.process(future5);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._1ST, Accidence.SINGULAR, Accidence.FEMININE))
        );
        assertTrue(results.containsAll(expected));

        String future6 = "बोलेगी";
        results = VerbalCanonizer.process(future6);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._2ND, Accidence.SINGULAR, Accidence.FEMININE)),
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._3RD, Accidence.SINGULAR, Accidence.FEMININE))

        );
        assertTrue(results.containsAll(expected));

        String future7 = "बोलेंगी";
        results = VerbalCanonizer.process(future7);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._1ST, Accidence.PLURAL, Accidence.FEMININE)),
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._3RD, Accidence.PLURAL, Accidence.FEMININE))

        );
        assertTrue(results.containsAll(expected));

        String future8 = "बोलोगी";
        results = VerbalCanonizer.process(future8);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._2ND, Accidence.PLURAL, Accidence.FEMININE))
        );
        assertTrue(results.containsAll(expected));


    }

    @Test
    public void participle() throws Exception {

        String speakcanonical = "बोलना";

        String participle1 = "बोलता";
        Set<CanonicalResult> results = VerbalCanonizer.process(participle1);
        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERFECTIVE, Accidence.MASCULINE, Accidence.SINGULAR))
        );
        assertTrue(results.containsAll(expected));

        String participle2 = "बोलती";
        results = VerbalCanonizer.process(participle2);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERFECTIVE, Accidence.FEMININE, Accidence.SINGULAR)),
                new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERFECTIVE, Accidence.FEMININE, Accidence.PLURAL))
        );
        assertTrue(results.containsAll(expected));


        String participle3 = "बोलते";
        results = VerbalCanonizer.process(participle3);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERFECTIVE, Accidence.MASCULINE, Accidence.PLURAL))
        );
        assertTrue(results.containsAll(expected));

        String participle4 = "बोलतीं";
        results = VerbalCanonizer.process(participle4);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.PARTICIPLE)
                        .setCanonicalForm(speakcanonical).setAccidence(Sets.newHashSet(Accidence.IMPERFECTIVE, Accidence.FEMININE, Accidence.PLURAL))
                );
        assertTrue(results.containsAll(expected));

    }

}
