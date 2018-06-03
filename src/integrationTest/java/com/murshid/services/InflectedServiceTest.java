package com.murshid.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.murshid.dynamo.domain.Inflected;
import com.murshid.models.CanonicalKey;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.DictionarySource;
import com.murshid.models.enums.PartOfSpeech;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
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

    private Set<CanonicalKey> canonicalKeys = Sets.newHashSet(new CanonicalKey().setDictionarySource(DictionarySource.PLATTS)
            .setCanonicalIndex(0).setCanonicalWord(bolnã));

    @Test
    public void explode() throws Exception {

        Inflected master = new Inflected().setCanonicalKeys(canonicalKeys)
                .setInflectedHindi("बोलता")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);

         List<Inflected> result = inflectedService.explode(master);

        Inflected expectedMasculineVocativeSingular = new Inflected().setCanonicalKeys(canonicalKeys)
                .setInflectedHindi("बोलते")
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.VOCATIVE, Accidence.SINGULAR))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);


        Inflected expectedFeminineDirectPlural = new Inflected().setCanonicalKeys(canonicalKeys)
                .setInflectedHindi("बोलतीं")
                .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL))
                .setPartOfSpeech(PartOfSpeech.PARTICIPLE);

        assertTrue(result.contains(expectedMasculineVocativeSingular));
        assertTrue(result.contains(expectedFeminineDirectPlural));

    }

    @Test
    public void explodeAllVerbs() {
        Inflected infinitive = new Inflected().setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT))
                .setCanonicalKeys(canonicalKeys).setCanonicalHindi(bolnã).setInflectedHindi(bolnã).setPartOfSpeech(PartOfSpeech.INFINITIVE);

        List<Inflected> verbs = inflectedService.explodeAllVerbs(infinitive);

        assertTrue(verbs.containsAll(infinitives()));
        assertTrue(verbs.containsAll(imperfectiveParticiples()));
        assertTrue(verbs.containsAll(perfectiveParticiples()));
        assertTrue(verbs.containsAll(absolutives()));

    }

    private List<Inflected> infinitives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलना").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलने").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलने").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलने").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलनी").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलनीं").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलनीं").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलनीं").setPartOfSpeech(PartOfSpeech.INFINITIVE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL))
        );
    }

    private List<Inflected> imperfectiveParticiples(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलता").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलते").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलते").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलते").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.IMPERFECTIVE)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलती").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलतीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलतीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.IMPERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलतीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.IMPERFECTIVE))
        );

    }

    private List<Inflected> perfectiveParticiples(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोला").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोले").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.MASCULINE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE)),

                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोली").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.SINGULAR, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.DIRECT, Accidence.PLURAL, Accidence.PERFECTIVE)),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलीं").setPartOfSpeech(PartOfSpeech.PARTICIPLE)
                        .setAccidence(Sets.newHashSet(Accidence.FEMININE, Accidence.OBLIQUE, Accidence.PLURAL, Accidence.PERFECTIVE))
        );
    }

    private List<Inflected> absolutives(){
        return Lists.newArrayList(
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोल").setPartOfSpeech(PartOfSpeech.ABSOLUTIVE)
                        .setAccidence(Sets.newHashSet()),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलकर").setPartOfSpeech(PartOfSpeech.ABSOLUTIVE)
                        .setAccidence(Sets.newHashSet()),
                new Inflected().setCanonicalHindi(bolnã).setCanonicalKeys(canonicalKeys).setInflectedHindi("बोलके").setPartOfSpeech(PartOfSpeech.ABSOLUTIVE)
                        .setAccidence(Sets.newHashSet())
        );
    }



    @Inject
    private InflectedService inflectedService;

}
