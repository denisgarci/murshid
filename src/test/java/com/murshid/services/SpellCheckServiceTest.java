package com.murshid.services;

import com.murshid.dynamo.domain.Inflected;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.SpellCheckEntry;
import com.murshid.persistence.repo.SpellCheckRepository;
import jersey.repackaged.com.google.common.collect.Lists;
import jersey.repackaged.com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpellCheckServiceTest {

    @Test
    void supplementInfinitivePluralDirectFeminine() {
        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलनीं")
                .setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.FEMININE, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Inflected canonicalInfinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(canonicalInfinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولنیں");
    }

    @Test
    void supplementPerfectParticipleSingularDirectFemininie() {
        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलीं")
                .setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.FEMININE, Accidence.DIRECT, Accidence.PERFECTIVE))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);

        Inflected canonicalInfinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.DIRECT, Accidence.SINGULAR, Accidence.MASCULINE))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(canonicalInfinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولیں");
    }

    @Test
    void supplementVocativePluralNounsFem() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बेटीयो")
                .setAccidence(Sets.newHashSet(Accidence.PLURAL, Accidence.FEMININE, Accidence.VOCATIVE))
                .setPartOfSpeech(PartOfSpeech.NOUN);

        Inflected obliquePluralFeminine = new Inflected().setInflectedHindi("बेटीयों").setInflectedUrdu("بیٹیوں")
                .setAccidence(Sets.newHashSet(Accidence.OBLIQUE, Accidence.PLURAL, Accidence.FEMININE))
                .setPartOfSpeech(PartOfSpeech.NOUN);


        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(obliquePluralFeminine));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بیٹیو");
    }

    @Test
    void absolutive() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलके")
                .setAccidence(Sets.newHashSet(Accidence.ABSOLUTIVE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولکے");
    }

    @Test
    void masculineFuture1ps() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलूँगा")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence._1ST, Accidence.SINGULAR, Accidence.FUTURE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولوں گا");
    }

    @Test
    void feminineFuture1ps() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलूँगी")
                .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence._1ST, Accidence.SINGULAR, Accidence.FUTURE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولوں گی");
    }


    @Test
    void masculineFuture2ps() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलेगा")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence._2ND, Accidence.SINGULAR, Accidence.FUTURE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولے گا");
    }


    @Test
    void masculineFuture2pp() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलोगे")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence._2ND, Accidence.PLURAL, Accidence.FUTURE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولو گی");
    }

    @Test
    void masculineFuture1pp() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलेंगे")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence._1ST, Accidence.PLURAL, Accidence.FUTURE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولیں گے");
    }

    @Test
    void feminineFuture1pp() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलेंगी")
                .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence._1ST, Accidence.PLURAL, Accidence.FUTURE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولیں گی");
    }

    @Test
    void feminineFuture2ps() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलेगी")
                .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence._2ND, Accidence.SINGULAR, Accidence.FUTURE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولے گی");
    }

    @Test
    void feminineFuture2pp() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलोगी")
                .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence._2ND, Accidence.PLURAL, Accidence.FUTURE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولو گی");
    }

    @Test
    void imperative3ppShortForm() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलिए")
                .setAccidence(Sets.newHashSet( Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولئے");
    }

    @Test
    void imperative3ppLongForm() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("बोलिये")
                .setAccidence(Sets.newHashSet( Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("बोलना").setInflectedUrdu("بولنا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "بولئیے");
    }

    @Test
    void subjunctivesRootInII1ps() {

        SpellCheckService service = new SpellCheckService();

        Inflected inflected = new Inflected().setInflectedHindi("पीऊँ")
                .setAccidence(Sets.newHashSet( Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE))
                .setPartOfSpeech(PartOfSpeech.VERB);

        Inflected infinitive = new Inflected().setInflectedHindi("पीना").setInflectedUrdu("پینا")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setPartOfSpeech(PartOfSpeech.INFINITIVE);

        Pair<Inflected, Optional<String>> proposed = service.propose(inflected, Lists.newArrayList(infinitive));

        assertTrue(proposed.getRight().isPresent());
        assertEquals(proposed.getRight().get(), "پیوں");
    }
}