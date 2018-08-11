package com.murshid.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InflectedServiceTest {

    private String bolnaa = "बोलना";

    private String jiinaa = "जीना";

    @Test
    public void explode()  {

        Inflected master = new Inflected()
                .setInflectedHindi("बोलता")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);

         List<Inflected> result = inflectedService.explode(master);

        Inflected expectedMasculineVocativeSingular = new Inflected()
                .setInflectedHindi("बोलते")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.VOCATIVE, Accidence.SINGULAR))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);


        Inflected expectedFeminineDirectPlural = new Inflected()
                .setInflectedHindi("बोलतीं")
                .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);

        assertTrue(result.contains(expectedMasculineVocativeSingular));
        assertTrue(result.contains(expectedFeminineDirectPlural));

    }

    @Test
    public void explodeAllVerbs() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalHindi(bolnaa).setInflectedHindi(bolnaa).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> verbs = inflectedService.explodeAllVerbs(infinitive);

        assertTrue(verbs.containsAll(verbRoot()));
        assertTrue(verbs.containsAll(infinitives()));
        assertTrue(verbs.containsAll(imperfectiveParticiples()));
        assertTrue(verbs.containsAll(perfectiveParticiples()));
        assertTrue(verbs.containsAll(absolutives()));
        assertTrue(verbs.containsAll(verbalNouns()));
        assertTrue(verbs.containsAll(bolnaaSubjunctives()));
        assertTrue(verbs.containsAll(bolnaaImperatives()));
        assertTrue(verbs.containsAll(futures()));

    }

    private String bataanaa = "बताना";

    @Test
    public void explodeAllVerbsVowelRoot() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalHindi(bataanaa).setInflectedHindi(bataanaa).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> verbs = inflectedService.explodeAllVerbs(infinitive);

        assertTrue(verbs.containsAll(perfectiveParticiplesRootInVowel()));
        assertTrue(verbs.containsAll(bataanaaSubjunctives()));
        assertTrue(verbs.containsAll(bataanaaImperatives()));
        assertTrue(verbs.containsAll(futuresRootInVowel()));

    }

    private List<Inflected> verbRoot(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोल").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.VERB_ROOT))
        );
    }

    private List<Inflected> infinitives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलना").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL)),

                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलनी").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलनीं").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलनीं").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलनीं").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL))
        );
    }

    private List<Inflected> imperfectiveParticiples(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलता").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलते").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलते").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलते").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.IMPERFECTIVE)),

                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलती").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलतीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलतीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलतीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.IMPERFECTIVE))
        );

    }

    private List<Inflected> perfectiveParticiples(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोला").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE)),

                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोली").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE))
        );
    }

    private List<Inflected> perfectiveParticiplesRootInVowel(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताया").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताए").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताए").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताए").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE)),

                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताई").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताईं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताईं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताईं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE))
        );
    }


    private List<Inflected> absolutives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोल").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.ABSOLUTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलकर").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.ABSOLUTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलके").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.ABSOLUTIVE))
        );
    }

    private List<Inflected> verbalNouns(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने वाला").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने वाले").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने वाले").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने वाले").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL)),

                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने वाली").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने वालीं").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने वालीं").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलने वालीं").setPartOfSpeech(PartOfSpeech.VERBAL_NOUN)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL))
        );
    }

    @Test
    public void anyWithAccidence(){
        List<Inflected> toBeSought = Lists.newArrayList(
                new Inflected().setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR)),
                new Inflected().setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL)),
                new Inflected().setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.SINGULAR)),
                new Inflected().setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.PLURAL))
        );

        assertTrue(InflectedService.anyWithAccidence(toBeSought, Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR)));
        assertTrue(InflectedService.anyWithAccidence(toBeSought, Sets.newHashSet(Accidence.PERFECTIVE, Accidence.PLURAL)));

        assertFalse(InflectedService.anyWithAccidence(toBeSought, Sets.newHashSet(Accidence.IMPERATIVE, Accidence.PLURAL)));
    }

    @Test
    public void subtractByAccidence(){
        List<Inflected> minuend = Lists.newArrayList(
                new Inflected().setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR)),
                new Inflected().setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL)),
                new Inflected().setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.SINGULAR)),
                new Inflected().setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.PLURAL))
        );

        List<Inflected> subtrahend = Lists.newArrayList(
                new Inflected().setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR)),
                new Inflected().setAccidence(Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL))
        );

        List<Inflected> expected = Lists.newArrayList(
                new Inflected().setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.SINGULAR)),
                new Inflected().setAccidence(Sets.newHashSet(Accidence.PERFECTIVE, Accidence.PLURAL))
        );

        List<Inflected> result = InflectedService.subtractByAccidence(minuend, subtrahend);
        assertEquals(expected, result);
    }



    protected static List<Inflected> subtractByAccidence(List<Inflected> proposed, List<Inflected> existing){
        return proposed.stream().filter(inflected -> !anyWithAccidence(existing, inflected.getAccidence()))
                .collect(toList());
    }


    protected static boolean anyWithAccidence(List<Inflected> inflectedList, Set<Accidence> accidences){
        return inflectedList.stream().anyMatch(inflected -> inflected.getAccidence().equals(accidences));
    }

    private List<Inflected> bolnaaSubjunctives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलूँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),

                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलें").setPartOfSpeech(PartOfSpeech.VERB)
                .setAccidence(Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलो").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलें").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.SUBJUNCTIVE))
               );
    }

    private List<Inflected> jiinaaSubjunctives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("जिऊँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("जिए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("जिए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),

                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("जिएँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("जिओ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("जिएँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.SUBJUNCTIVE))
        );
    }

    private List<Inflected> jiinaaImperatives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("जी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("जिओ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("जिये").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("जिए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE))
        );
    }

    private List<Inflected> bataanaaSubjunctives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताऊँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)),

                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताएँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताओ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.SUBJUNCTIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताएँ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.SUBJUNCTIVE))
        );
    }

    @Test
    public void explodeSubjunctivesRootInConsonant() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalHindi(bolnaa).setInflectedHindi(bolnaa).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeSubjunctives(infinitive, result);

        assertTrue(result.containsAll(bolnaaSubjunctives()));
    }

    @Test
    public void explodeSubjunctivesRootInVowelNotUUorII() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalHindi(bataanaa).setInflectedHindi(bataanaa).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeSubjunctives(infinitive, result);

        assertTrue(result.containsAll(bataanaaSubjunctives()));
    }

    @Test
    public void explodeSubjunctivesRootInII() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalHindi(jiinaa).setInflectedHindi(jiinaa).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeSubjunctives(infinitive, result);

        assertTrue(result.containsAll(jiinaaSubjunctives()));
    }

    @Test
    public void explodeImperativesRootInConsonant() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalHindi(bataanaa).setInflectedHindi(bataanaa).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeImperatives(infinitive, result);

        assertTrue(result.containsAll(bataanaaImperatives()));
    }

    @Test
    public void explodeImperativesRootInVowelNotUUorII() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalHindi(bataanaa).setInflectedHindi(bataanaa).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeImperatives(infinitive, result);

        assertTrue(result.containsAll(bataanaaImperatives()));
    }

    @Test
    public void explodeImperativesRootInII() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalHindi(jiinaa).setInflectedHindi(jiinaa).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeImperatives(infinitive, result);

        assertTrue(result.containsAll(jiinaaImperatives()));
    }




    private List<Inflected> bolnaaImperatives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोल").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलो").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलिये").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलिए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE))
        );
    }

    private List<Inflected> bataanaaImperatives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बता").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताओ").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताइये").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताइए").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE))
        );
    }

    private List<Inflected> futures(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलूँगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलेगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलेगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलेंगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलोगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलेंगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलूँगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलेगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलेगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलेंगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलोगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bolnaa).setInflectedHindi("बोलेंगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD))


                );
    }

    private List<Inflected> futuresRootInVowel(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताऊँगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताएगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताएगा").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताएँगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताओगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताएँगे").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताऊँगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताएगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताएगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD)),

                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताएँगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताओगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND)),
                new Inflected().setCanonicalHindi(bataanaa).setInflectedHindi("बताएँगी").setPartOfSpeech(PartOfSpeech.VERB)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD))

        );
    }




    @Inject
    private InflectedService inflectedService;

}
