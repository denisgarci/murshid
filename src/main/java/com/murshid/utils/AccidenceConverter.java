package com.murshid.utils;

import com.murshid.ingestor.wikitionary.models.WikiAccidence;
import com.murshid.models.enums.Accidence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccidenceConverter {

    private static Optional<Accidence> wikiToGeneralAccidence(WikiAccidence wikiAccidence){
        switch(wikiAccidence){
            case MASCULINE:
                return Optional.of(Accidence.MASCULINE);
            case FEMININE:
                return Optional.of(Accidence.FEMININE);
            case PLURAL_NUMBER:
                return Optional.of(Accidence.PLURAL);
            case NEUTER_GENDER:
                return Optional.of(Accidence.NEUTER);
            case GENDER_INCOMPLETE:
                return Optional.empty();
            case SINGULAR_NUMBER:
                return Optional.of(Accidence.SINGULAR);
                default:
                    throw new IllegalArgumentException("unknown wikiAccidence = " + wikiAccidence);
        }
    }

    public static List<Accidence> wikiToGeneralAccidentList(List<WikiAccidence> wikiAccidences){
        List<Accidence> result = new ArrayList<>();
        wikiAccidences.forEach(wa -> {
            Optional<Accidence> accidence = wikiToGeneralAccidence(wa);
            accidence.ifPresent(result::add);
        });
        return result;
    }
}
