package com.murshid.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InflectedServiceTest {

    private String bolnã = "बोलना";

    private Set<CanonicalKey> canonicalKeysBolnã = Sets.newHashSet(new CanonicalKey().setDictionarySource(DictionarySource.PLATTS)
            .setCanonicalIndex(0).setCanonicalWord(bolnã));

    @Test
    public void explode() throws Exception {

        Inflected master = new Inflected().setCanonicalKeys(canonicalKeysBolnã)
                .setInflectedHindi("बोलता")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);

         List<Inflected> result = inflectedService.explode(master);

        Inflected expectedMasculineVocativeSingular = new Inflected().setCanonicalKeys(canonicalKeysBolnã)
                .setInflectedHindi("बोलते")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.VOCATIVE, Accidence.SINGULAR))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);


        Inflected expectedFeminineDirectPlural = new Inflected().setCanonicalKeys(canonicalKeysBolnã)
                .setInflectedHindi("बोलतीं")
                .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);

        assertTrue(result.contains(expectedMasculineVocativeSingular));
        assertTrue(result.contains(expectedFeminineDirectPlural));

    }

    @Test
    public void explodeAllVerbs() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalKeys(canonicalKeysBolnã).setCanonicalHindi(bolnã).setInflectedHindi(bolnã).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> verbs = inflectedService.explodeAllVerbs(infinitive);

        assertTrue(verbs.containsAll(verbRoot()));
        assertTrue(verbs.containsAll(infinitives()));
        assertTrue(verbs.containsAll(imperfectiveParticiples()));
        assertTrue(verbs.containsAll(perfectiveParticiples()));
        assertTrue(verbs.containsAll(absolutives()));
        assertTrue(verbs.containsAll(verbalNouns()));
        assertTrue(verbs.containsAll(subjunctives()));
        assertTrue(verbs.containsAll(imperatives()));
        assertTrue(verbs.containsAll(futures()));

    }

    private String batãnã = "बताना";

    private Set<CanonicalKey> canonicalKeysBatãnã = Sets.newHashSet(new CanonicalKey().setDictionarySource(DictionarySource.PLATTS)
            .setCanonicalIndex(0).setCanonicalWord(batãnã));


    @Test
    public void explodeAllVerbsVowelRoot() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalKeys(canonicalKeysBatãnã).setCanonicalHindi(batãnã).setInflectedHindi(batãnã).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> verbs = inflectedService.explodeAllVerbs(infinitive);

        assertTrue(verbs.containsAll(perfectiveParticiplesRootInVowel()));
        assertTrue(verbs.containsAll(subjunctivesRootInVowel()));
        assertTrue(verbs.containsAll(imperativesRootInVowel()));
        assertTrue(verbs.containsAll(futuresRootInVowel()));

    }

    private List<Inflected> verbRoot(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोल").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.VERB_ROOT))
        );
    }

    private List<Inflected> infinitives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलना").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलने").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलने").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलने").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनी").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनीं").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनीं").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनीं").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL))
        );
    }

    private List<Inflected> imperfectiveParticiples(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलता").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलते").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलते").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलते").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.IMPERFECTIVE)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलती").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलतीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलतीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलतीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.IMPERFECTIVE))
        );

    }

    private List<Inflected> perfectiveParticiples(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोला").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोली").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE))
        );
    }

    private List<Inflected> perfectiveParticiplesRootInVowel(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताया").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताए").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताए").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताए").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE)),

                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताई").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताईं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताईं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताईं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE))
        );
    }


    private List<Inflected> absolutives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोल").setPartOfSpeech(PartOfSpeech.ABSOLUTIVE)
                        .setAccidence(Sets.newHashSet()),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलकर").setPartOfSpeech(PartOfSpeech.ABSOLUTIVE)
                        .setAccidence(Sets.newHashSet()),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलके").setPartOfSpeech(PartOfSpeech.ABSOLUTIVE)
                        .setAccidence(Sets.newHashSet())
        );
    }

    private List<Inflected> verbalNouns(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनेवाला").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनेवाले").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनेवाले").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनेवाले").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनेवाली").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनेवालीं").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनेवालीं").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलनेवालीं").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL))
        );
    }

    private List<Inflected> subjunctives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलूँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलें").setPartOfSpeech(PartOfSpeech.VERB)
                .setAccidence(Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलो").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलें").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.SUBJUNCTIVE))
               );
    }

    private List<Inflected> subjunctivesRootInVowel(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताऊँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),

                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताएँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताओ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताएँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.SUBJUNCTIVE))
        );
    }

    private List<Inflected> imperatives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोल").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलो").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलिये").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलिए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE))
        );
    }

    private List<Inflected> imperativesRootInVowel(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बता").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताओ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताइये").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताइए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE))
        );
    }

    private List<Inflected> futures(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलूँगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलेगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलेगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलेंगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलोगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलेंगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलूँगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलेगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलेगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलेंगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलोगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeysBolnã).setInflectedHindi("बोलेंगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD))


                );
    }

    private List<Inflected> futuresRootInVowel(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताऊँगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताएगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताएगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताएँगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताओगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताएँगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताऊँगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताएगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताएगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताएँगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताओगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(batãnã).setCanonicalKeys(canonicalKeysBatãnã).setInflectedHindi("बताएँगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD))

        );
    }




    @Inject
    private InflectedService inflectedService;

}
