package com.murshid.models.enums;

public enum Accidence {
    MASCULINE("masculine"), FEMININE("feminine"), NEUTER("neuter"),
    DIRECT("direct"), OBLIQUE("oblique"), VOCATIVE("vocative"),
    PLURAL("plural"), SINGULAR("singular"),
    _1ST("first person"), _2ND("second person"), _3RD("thrid person"),
    PRESENT("present"), FUTURE("future"), PAST("past"), PLUSQUAMPERFECT("plusquamperfect"),
    ACTIVE("active"), PASSIVE("passive"),
    INDICATIVE("indicative"), SUBJUNCTIVE("subjunctive"), CONDITIONAL("conditional"), IMPERATIVE("imperative"), PRESUMPTIVE("presumptive"),
    NON_ASPECTUAL("non aspectual"), HABITUAL("habitual"), CONTINUOUS("continuous"), PERFECTIVE("perfective"), IMPERFECTIVE("imperfective"),
    FACTUAL("factual"), CONTRAFACTUAL("contrafactual"),
    ABSOLUTIVE("absolutive"),
    LONG_FORM("long form"),
    VERB_ROOT("Verb root");

    public String getLabel() {
        return label;
    }

    private String label;

    Accidence(String label){
        this.label = label;
    }

    public static Accidence fromLabel(String label){
        for (Accidence accidence : Accidence.values()){
            if (accidence.label.equals(label)){
                return accidence;
            }
        }
        throw new IllegalArgumentException("label " + label + " not supported");
    }

}
