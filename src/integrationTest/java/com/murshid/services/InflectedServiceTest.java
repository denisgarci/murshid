package com.murshid.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.Inflected;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InflectedServiceTest {

    private String bolnaa = "बोलना";

    private String jiinaa = "जीना";

    private Inflected create(String inflectedHindi, Set<Accidence> accidences, PartOfSpeech partOfSpeech){
        Inflected inflected = new Inflected();
        Inflected.InflectedKey inflectedKey = new Inflected.InflectedKey();
        inflectedKey.setInflectedHindi(inflectedHindi);
        inflected.setInflectedKey(inflectedKey);

        inflected.setPartOfSpeech(partOfSpeech)
                .setAccidence(Lists.newArrayList(accidences));
        return inflected;
    }

    private Inflected create(String canonicalHindi,  String inflectedHindi, Set<Accidence> accidences, PartOfSpeech partOfSpeech){
        Inflected inflected = create(inflectedHindi, accidences, partOfSpeech);
        inflected.setCanonicalHindi(canonicalHindi);
        return inflected;
    }

    @Test
    public void explode()  {

        Inflected master = create("बोलता", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR), PartOfSpeech.PARTICIPLE);

         List<Inflected> result = inflectedService.explode(master);

        Inflected expectedMasculineVocativeSingular = create("बोलते", Sets.newHashSet(Accidence.MASCULINE, Accidence.VOCATIVE, Accidence.SINGULAR), PartOfSpeech.PARTICIPLE);


        Inflected expectedFeminineDirectPlural = create("बोलतीं", Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL), PartOfSpeech.PARTICIPLE);

        assertTrue(result.contains(expectedMasculineVocativeSingular));
        assertTrue(result.contains(expectedFeminineDirectPlural));

    }

    @Test
    public void explodeAllVerbs() {
        Inflected infinitive = create(bolnaa, bolnaa, Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

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
        Inflected infinitive = create(bataanaa, bataanaa, Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        List<Inflected> verbs = inflectedService.explodeAllVerbs(infinitive);

        List<Inflected> perfective = verbs.stream().
                filter(v -> v.getAccidence()!= null && v.getAccidence().contains(Accidence.PERFECTIVE) && v.getPartOfSpeech() == PartOfSpeech.PARTICIPLE)
                .collect(Collectors.toList());

        assertTrue(verbs.containsAll(perfectiveParticiplesRootInVowel()));
        assertTrue(verbs.containsAll(bataanaaSubjunctives()));
        assertTrue(verbs.containsAll(bataanaaImperatives()));
        assertTrue(verbs.containsAll(futuresRootInVowel()));

    }

    private List<Inflected> verbRoot(){
        return Lists.newArrayList( create(bolnaa, "बोल", Sets.newHashSet(Accidence.VERB_ROOT), PartOfSpeech.VERB));
    }

    private List<Inflected> infinitives(){
        return Lists.newArrayList(
                create(bolnaa, "बोलना", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR), PartOfSpeech.INFINITIVE),
                create(bolnaa, "बोलने", Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR), PartOfSpeech.INFINITIVE),
                create(bolnaa, "बोलने", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL), PartOfSpeech.INFINITIVE),
                create(bolnaa, "बोलने", Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL), PartOfSpeech.INFINITIVE),
                create(bolnaa, "बोलनी", Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR), PartOfSpeech.INFINITIVE),
                create(bolnaa, "बोलनीं", Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR), PartOfSpeech.INFINITIVE),
                create(bolnaa, "बोलनीं", Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL), PartOfSpeech.INFINITIVE),
                create(bolnaa, "बोलनीं", Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL), PartOfSpeech.INFINITIVE)
        );
    }

    private List<Inflected> imperfectiveParticiples(){
        return Lists.newArrayList(
                create(bolnaa, "बोलता", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.IMPERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोलते", Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.IMPERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोलते", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.IMPERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोलते", Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.IMPERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोलती", Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.IMPERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोलतीं", Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.IMPERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोलतीं", Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.IMPERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोलतीं", Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.IMPERFECTIVE), PartOfSpeech.PARTICIPLE)
        );

    }

    private List<Inflected> perfectiveParticiples(){
        return Lists.newArrayList(
                create(bolnaa, "बोला", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोले", Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोले", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोले", Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोली", Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोलीं", Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोलीं",Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bolnaa, "बोलीं",Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE)
        );
    }

    private List<Inflected> perfectiveParticiplesRootInVowel(){
        return Lists.newArrayList(
                create(bataanaa, "बताया", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bataanaa, "बताए", Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bataanaa, "बताए", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bataanaa, "बताए", Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bataanaa, "बताई", Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bataanaa, "बताईं", Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bataanaa, "बताईं", Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE),
                create(bataanaa, "बताईं", Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE), PartOfSpeech.PARTICIPLE)
        );
    }


    private List<Inflected> absolutives(){
        return Lists.newArrayList(
                create(bolnaa, "बोल", Sets.newHashSet(Accidence.ABSOLUTIVE), PartOfSpeech.VERB),
                create(bolnaa, "बोलकर", Sets.newHashSet(Accidence.ABSOLUTIVE), PartOfSpeech.VERB),
                create(bolnaa, "बोलके", Sets.newHashSet(Accidence.ABSOLUTIVE), PartOfSpeech.VERB)
        );
    }

    private List<Inflected> verbalNouns(){
        return Lists.newArrayList(
                create(bolnaa, "बोलने वाला", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR), PartOfSpeech.VERBAL_NOUN),
                create(bolnaa, "बोलने वाले", Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR), PartOfSpeech.VERBAL_NOUN),
                create(bolnaa, "बोलने वाले", Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL), PartOfSpeech.VERBAL_NOUN),
                create(bolnaa, "बोलने वाले", Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL), PartOfSpeech.VERBAL_NOUN),
                create(bolnaa, "बोलने वाली", Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR), PartOfSpeech.VERBAL_NOUN),
                create(bolnaa, "बोलने वालीं", Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR), PartOfSpeech.VERBAL_NOUN),
                create(bolnaa, "बोलने वालीं", Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL), PartOfSpeech.VERBAL_NOUN),
                create(bolnaa, "बोलने वालीं", Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL), PartOfSpeech.VERBAL_NOUN)
        );
    }

    @Test
    public void anyWithAccidence(){
        List<Inflected> toBeSought = Lists.newArrayList(
                new Inflected().setAccidence(Lists.newArrayList(Accidence.FUTURE, Accidence.SINGULAR)),
                new Inflected().setAccidence(Lists.newArrayList(Accidence.FUTURE, Accidence.PLURAL)),
                new Inflected().setAccidence(Lists.newArrayList(Accidence.PERFECTIVE, Accidence.SINGULAR)),
                new Inflected().setAccidence(Lists.newArrayList(Accidence.PERFECTIVE, Accidence.PLURAL))
        );

        assertTrue(InflectedService.anyWithAccidence(toBeSought, Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR)));
        assertTrue(InflectedService.anyWithAccidence(toBeSought, Sets.newHashSet(Accidence.PERFECTIVE, Accidence.PLURAL)));

        assertFalse(InflectedService.anyWithAccidence(toBeSought, Sets.newHashSet(Accidence.IMPERATIVE, Accidence.PLURAL)));
    }

    @Test
    public void subtractByAccidence(){
        Inflected.InflectedKey dummy = new Inflected.InflectedKey();
        List<Inflected> minuend = Lists.newArrayList(
                create(null, null, Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR), null),
                create(null, null, Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL), null),
                create(null, null, Sets.newHashSet(Accidence.PERFECTIVE, Accidence.SINGULAR), null),
                create(null, null, Sets.newHashSet(Accidence.PERFECTIVE, Accidence.PLURAL), null)
        );

        List<Inflected> subtrahend = Lists.newArrayList(
                create(null, null, Sets.newHashSet(Accidence.FUTURE, Accidence.SINGULAR), null),
                create(null, null, Sets.newHashSet(Accidence.FUTURE, Accidence.PLURAL), null)
        );

        List<Inflected> expected = Lists.newArrayList(
                create(null, null, Sets.newHashSet(Accidence.PERFECTIVE, Accidence.SINGULAR), null),
                create(null, null, Sets.newHashSet(Accidence.PERFECTIVE, Accidence.PLURAL), null)
        );

        List<Inflected> result = InflectedService.subtractByAccidence(minuend, subtrahend);
        assertEquals(expected, result);
    }



    protected static List<Inflected> subtractByAccidence(List<Inflected> proposed, List<Inflected> existing){
        return proposed.stream().filter(inflected -> !anyWithAccidence(existing, Sets.newHashSet(inflected.getAccidence())))
                .collect(toList());
    }


    protected static boolean anyWithAccidence(List<Inflected> inflectedList, Set<Accidence> accidences){
        return inflectedList.stream().anyMatch(inflected -> inflected.getAccidence().equals(accidences));
    }

    private List<Inflected> bolnaaSubjunctives(){
        return Lists.newArrayList(
                create(bolnaa, "बोलूँ", Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(bolnaa, "बोले", Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(bolnaa, "बोले", Sets.newHashSet(Accidence._3RD, Accidence.SINGULAR, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(bolnaa, "बोलें", Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(bolnaa, "बोलो", Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(bolnaa, "बोलें", Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB));
    }

    private List<Inflected> jiinaaSubjunctives(){
        return Lists.newArrayList(
                create(jiinaa, "जिऊँ", Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(jiinaa, "जिए", Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(jiinaa, "जिए", Sets.newHashSet(Accidence._3RD, Accidence.SINGULAR, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(jiinaa, "जिएँ", Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(jiinaa, "जिओ", Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(jiinaa, "जिएँ", Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB)
        );
    }

    private List<Inflected> jiinaaImperatives(){
        return Lists.newArrayList(
                create(jiinaa, "जी", Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.IMPERATIVE), PartOfSpeech.VERB),
                create(jiinaa, "जिओ", Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB),
                create(bolnaa, "जिये", Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB),
                create(bolnaa, "जिए", Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB)
        );
    }

    private List<Inflected> bataanaaSubjunctives(){
        return Lists.newArrayList(
                create(bataanaa, "बताऊँ", Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(bataanaa, "बताए", Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(bataanaa, "बताए", Sets.newHashSet(Accidence._3RD, Accidence.SINGULAR, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),

                create(bataanaa, "बताएँ", Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(bataanaa, "बताओ", Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB),
                create(bataanaa, "बताएँ", Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.SUBJUNCTIVE), PartOfSpeech.VERB)
        );
    }

    @Test
    public void explodeSubjunctivesRootInConsonant() {
        Inflected infinitive = create(bolnaa, bolnaa, Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);
        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeSubjunctives(infinitive, result);

        assertTrue(result.containsAll(bolnaaSubjunctives()));
    }

    @Test
    public void explodeSubjunctivesRootInVowelNotUUorII() {
        Inflected infinitive = create(bataanaa, bataanaa, Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);

        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeSubjunctives(infinitive, result);

        assertTrue(result.containsAll(bataanaaSubjunctives()));
    }

    @Test
    public void explodeSubjunctivesRootInII() {
        Inflected infinitive = create(jiinaa, jiinaa, Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);
        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeSubjunctives(infinitive, result);

        assertTrue(result.containsAll(jiinaaSubjunctives()));
    }

    @Test
    public void explodeImperativesRootInConsonant() {
        Inflected infinitive = create(bolnaa, bolnaa, Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);
        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeImperatives(infinitive, result);

        assertTrue(result.containsAll(bolnaaImperatives()));
    }

    @Test
    public void explodeImperativesRootInVowelNotUUorII() {
        Inflected infinitive = create(bataanaa, bataanaa, Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);
        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeImperatives(infinitive, result);

        assertTrue(result.containsAll(bataanaaImperatives()));
    }

    @Test
    public void explodeImperativesRootInII() {
        Inflected infinitive = create(jiinaa, jiinaa, Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT), PartOfSpeech.INFINITIVE);
        List<Inflected> result = Lists.newArrayList();
        inflectedService.explodeImperatives(infinitive, result);

        assertTrue(result.containsAll(jiinaaImperatives()));
    }




    private List<Inflected> bolnaaImperatives(){
        return Lists.newArrayList(
                create(bolnaa, "बोल", Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.IMPERATIVE), PartOfSpeech.VERB),
                create(bolnaa, "बोलो", Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB),
                create(bolnaa, "बोलिये", Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB),
                create(bolnaa, "बोलिए", Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB)
        );
    }

    private List<Inflected> bataanaaImperatives(){
        return Lists.newArrayList(
                create(bataanaa, "बता", Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.IMPERATIVE), PartOfSpeech.VERB),
                create(bataanaa, "बताओ", Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB),
                create(bataanaa, "बताइये", Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB),
                create(bataanaa, "बताइए", Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE), PartOfSpeech.VERB)
        );
    }

    private List<Inflected> futures(){
        return Lists.newArrayList(
                create(bolnaa, "बोलूँगा", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST), PartOfSpeech.VERB),
                create(bolnaa, "बोलेगा", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND), PartOfSpeech.VERB),
                create(bolnaa, "बोलेगा", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD), PartOfSpeech.VERB),
                create(bolnaa, "बोलेंगे", Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST), PartOfSpeech.VERB),
                create(bolnaa, "बोलोगे", Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND), PartOfSpeech.VERB),
                create(bolnaa, "बोलेंगे", Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD), PartOfSpeech.VERB),

                create(bolnaa, "बोलूँगी", Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST), PartOfSpeech.VERB),
                create(bolnaa, "बोलेगी", Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND), PartOfSpeech.VERB),
                create(bolnaa, "बोलेगी", Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD), PartOfSpeech.VERB),
                create(bolnaa, "बोलेंगी", Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST), PartOfSpeech.VERB),
                create(bolnaa, "बोलोगी", Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND), PartOfSpeech.VERB),
                create(bolnaa, "बोलेंगी", Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD), PartOfSpeech.VERB)
        );
    }

    private List<Inflected> futuresRootInVowel(){
        return Lists.newArrayList(
                create(bataanaa, "बताऊँगा", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST), PartOfSpeech.VERB),
                create(bataanaa, "बताएगा", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND), PartOfSpeech.VERB),
                create(bataanaa, "बताएगा", Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD), PartOfSpeech.VERB),
                create(bataanaa, "बताएँगे", Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST), PartOfSpeech.VERB),
                create(bataanaa, "बताओगे", Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND), PartOfSpeech.VERB),
                create(bataanaa, "बताएँगे", Sets.newHashSet(Accidence.MASCULINE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD), PartOfSpeech.VERB),

                create(bataanaa, "बताऊँगी", Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._1ST), PartOfSpeech.VERB),
                create(bataanaa, "बताएगी", Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._2ND), PartOfSpeech.VERB),
                create(bataanaa, "बताएगी", Sets.newHashSet(Accidence.FEMININE, Accidence.SINGULAR, Accidence.FUTURE, Accidence._3RD), PartOfSpeech.VERB),
                create(bataanaa, "बताएँगी", Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._1ST), PartOfSpeech.VERB),
                create(bataanaa, "बताओगी", Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._2ND), PartOfSpeech.VERB),
                create(bataanaa, "बताएँगी", Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.FUTURE, Accidence._3RD), PartOfSpeech.VERB)

        );
    }




    @Inject
    private InflectedService inflectedService;

}
