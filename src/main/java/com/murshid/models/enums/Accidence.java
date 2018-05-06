package com.murshid.models.enums;

import static com.murshid.models.enums.AccidenceCategory.*;

public enum Accidence {
    MASCULINE("masculine", GENDER), FEMININE("feminine", GENDER), NEUTER("neuter", GENDER),
    DIRECT("direct", CASE), OBLIQUE("oblique", CASE), VOCATIVE("vocative", CASE),
    PLURAL("plural", NUMBER), SINGULAR("singular", NUMBER),
    _1ST("first person", PERSON), _2ND("second person", PERSON), _3RD("thrid person", PERSON),
    PRESENT("present", TENSE), FUTURE("future", TENSE), PAST("past", TENSE), PLUSQUAMPERFECT("plusquamperfect", TENSE),
    ACTIVE("active", VOICE), PASSIVE("passive", VOICE),
    INDICATIVE("indicative", MODE), SUBJUNCTIVE("subjunctive", MODE), CONDITIONAL("conditional", MODE), IMPERATIVE("imperative", MODE), PRESUMPTIVE("presumptive", MODE),
    NON_ASPECTUAL("non aspectual", ASPECT), HABITUAL("habitual", ASPECT), CONTINUOUS("continuous", ASPECT), PERFECTIVE("perfective", ASPECT), IMPERFECTIVE("imperfective", ASPECT),
    FACTUAL("factual", FACTUALITY), CONTRAFACTUAL("contrafactual", FACTUALITY),
    LONG_FORM("long form", FORM_LENGTH);

    public String getLabel() {
        return label;
    }

    private String label;
    private AccidenceCategory accidenceCategory;

    Accidence(String label, AccidenceCategory accidenceCategory){
        this.label = label;
        this.accidenceCategory = accidenceCategory;
    }

    public static Accidence fromLabel(String label){
        for (Accidence accidence : Accidence.values()){
            if (accidence.label.equals(label)){
                return accidence;
            }
        }
        throw new IllegalArgumentException("label " + label + " not supported");
    }

    public AccidenceCategory getAccidenceCategory() {
        return accidenceCategory;
    }

}
