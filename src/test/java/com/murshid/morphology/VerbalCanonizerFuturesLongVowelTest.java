package com.murshid.morphology;

import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class VerbalCanonizerFuturesLongVowelTest {

    @Test
    public void masculineFuturesInII() throws Exception {

        String drinkCanonical = "पीना";

        String iWillDrinkMasc = "पिऊँगा";
        Set<CanonicalResult> results = VerbalCanonizer.process(iWillDrinkMasc);

        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._1ST, Accidence.SINGULAR, Accidence.MASCULINE))
        );
        assertTrue(results.containsAll(expected));

        String youSingularWillDrinkMasc = "पिएगा";
        results = VerbalCanonizer.process(youSingularWillDrinkMasc);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._2ND, Accidence.SINGULAR, Accidence.MASCULINE)),
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._3RD, Accidence.SINGULAR, Accidence.MASCULINE))
        );
        assertTrue(results.containsAll(expected));


        String weWillDrinkMasc = "पिएँगे";
        results = VerbalCanonizer.process(weWillDrinkMasc);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._1ST, Accidence.PLURAL, Accidence.MASCULINE)),
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._3RD, Accidence.PLURAL, Accidence.MASCULINE))

        );
        assertTrue(results.containsAll(expected));

        String youPlurallWillDrinkMasc = "पिओगे";
        results = VerbalCanonizer.process(youPlurallWillDrinkMasc);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._2ND, Accidence.PLURAL, Accidence.MASCULINE))
        );
        assertTrue(results.containsAll(expected));

    }

    @Test
    public void feminineFuturesInII() throws Exception {

        String drinkCanonical = "पीना";

        String iWillDrinkFem = "पिऊँगी";
        Set<CanonicalResult> results = VerbalCanonizer.process(iWillDrinkFem);

        Set <CanonicalResult> expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._1ST, Accidence.SINGULAR, Accidence.FEMININE))
        );
        assertTrue(results.containsAll(expected));

        String youSingularWillDrinkFem = "पिएगी";
        results = VerbalCanonizer.process(youSingularWillDrinkFem);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._2ND, Accidence.SINGULAR, Accidence.FEMININE)),
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._3RD, Accidence.SINGULAR, Accidence.FEMININE))
        );
        assertTrue(results.containsAll(expected));


        String weWillDrinkFem = "पिएँगी";
        results = VerbalCanonizer.process(weWillDrinkFem);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._1ST, Accidence.PLURAL, Accidence.FEMININE)),
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._3RD, Accidence.PLURAL, Accidence.FEMININE))

        );
        assertTrue(results.containsAll(expected));

        String youPlurallWillDrinkFem = "पिओगी";
        results = VerbalCanonizer.process(youPlurallWillDrinkFem);
        expected = Sets.newHashSet(
                new CanonicalResult().setPossiblePOS(PartOfSpeech.VERB)
                        .setCanonicalForm(drinkCanonical).setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence._2ND, Accidence.PLURAL, Accidence.FEMININE))
        );
        assertTrue(results.containsAll(expected));

    }


}
