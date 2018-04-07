package com.murshid.morphology;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AllCanonizers {

    public static Set<String> allCanonicals(@Nonnull String inflected){
        Set<CanonicalResult> nounCanonicals = NounCanonizer.process(inflected);
        Set<CanonicalResult> verbalCanonicals = VerbalCanonizer.process(inflected);
        Set<CanonicalResult> adjectiveCanonicals = AdjectiveCanonizer.process(inflected);

        Set<CanonicalResult> results = new HashSet<>();
        results.addAll(nounCanonicals);
        results.addAll(verbalCanonicals);
        results.addAll(adjectiveCanonicals);

        Set<String> result = results.stream().map(CanonicalResult::getCanonicalForm).collect(Collectors.toSet());
        result.add(inflected);

        return result;
    }
}
