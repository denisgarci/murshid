package com.murshid.services;

import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.Inflected;
import jersey.repackaged.com.google.common.collect.Lists;
import jersey.repackaged.com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

class SpellCheckServiceTest {

    private Inflected create(String inflectedHindi, Set<Accidence> accidence, PartOfSpeech partOfSpeech){
        Inflected inflected = new Inflected();
        Inflected.InflectedKey inflectedKey = new Inflected.InflectedKey();
        inflected.setInflectedKey(inflectedKey);
        inflectedKey.setInflectedHindi(inflectedHindi);

        inflected.setInflectedKey(inflectedKey)
                .setPartOfSpeech(partOfSpeech)
                .setAccidence(Lists.newArrayList(accidence));

        return inflected;
    }

    private Inflected create(String inflectedHindi, String inflectedUrdu, Set<Accidence> accidence, PartOfSpeech partOfSpeech){
        Inflected inflected = create(inflectedHindi, accidence, partOfSpeech);
        inflected.setUrdu(inflectedUrdu);
        return inflected;
    }

    @Test
    void supplementInfinitivePluralDirectFeminine() {
        SpellCheckService service = new SpellCheckService();

        Inflected inflected =  create("बोलनीं", Sets.newHashSet(Accidence.PLURAL, Accidence.FEMININE, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Inflected canonicalInfinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(canonicalInfinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولنیں");
    }

    @Test
    void supplementPerfectParticipleSingularDirectFemininie() {
        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलीं", Sets.newHashSet(Accidence.PLURAL, Accidence.FEMININE, Accidence.DIRECT, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE);

        Inflected canonicalInfinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(canonicalInfinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولیں");
    }

    @Test
    void supplementVocativePluralNounsFem() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बेटीयो", Sets.newHashSet(Accidence.PLURAL, Accidence.FEMININE, Accidence.VOCATIVE), PartOfSpeech.NOUN);

        Inflected obliquePluralFeminine =create("बेटीयों", "بیٹیوں", Sets.newHashSet(Accidence.OBLIQUE, Accidence.PLURAL, Accidence.FEMININE), PartOfSpeech.NOUN);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(obliquePluralFeminine));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بیٹیو");
    }

    @Test
    void absolutive() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलके", Sets.newHashSet(Accidence.ABSOLUTIVE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولکے");
    }

    @Test
    void masculineFuture1ps() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलूँगा", Sets.newHashSet(Accidence.MASCULINE, Accidence._1ST, Accidence.SINGULAR, Accidence.FUTURE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولوں گا");
    }

    @Test
    void feminineFuture1ps() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलूँगी", Sets.newHashSet(Accidence.FEMININE, Accidence._1ST, Accidence.SINGULAR, Accidence.FUTURE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا" ,Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولوں گی");
    }


    @Test
    void masculineFuture2ps() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलेगा", Sets.newHashSet(Accidence.MASCULINE, Accidence._2ND, Accidence.SINGULAR, Accidence.FUTURE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولے گا");
    }


    @Test
    void masculineFuture2pp() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलोगे", Sets.newHashSet(Accidence.MASCULINE, Accidence._2ND, Accidence.PLURAL, Accidence.FUTURE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولو گی");
    }

    @Test
    void masculineFuture1pp() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलेंगे", Sets.newHashSet(Accidence.MASCULINE, Accidence._1ST, Accidence.PLURAL, Accidence.FUTURE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولیں گے");
    }

    @Test
    void feminineFuture1pp() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलेंगी", Sets.newHashSet(Accidence.FEMININE, Accidence._1ST, Accidence.PLURAL, Accidence.FUTURE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولیں گی");
    }

    @Test
    void feminineFuture2ps() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलेगी", Sets.newHashSet(Accidence.FEMININE, Accidence._2ND, Accidence.SINGULAR, Accidence.FUTURE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولے گی");
    }

    @Test
    void feminineFuture2pp() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलोगी", Sets.newHashSet(Accidence.FEMININE, Accidence._2ND, Accidence.PLURAL, Accidence.FUTURE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولو گی");
    }

    @Test
    void imperative3ppShortForm() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलिए", Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولئے");
    }

    @Test
    void imperative3ppLongForm() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("बोलिये", Sets.newHashSet( Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB);

        Inflected infinitive = create("बोलना", "بولنا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولئیے");
    }

    @Test
    void subjunctivesRootInII1ps() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = create("पीऊँ", Sets.newHashSet( Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB);

        Inflected infinitive = create("पीना", "پینا", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "پیوں");
    }
}