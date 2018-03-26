package com.murshid.models.enums;

public enum Accidence {
    MASCULINE("masculine"), FEMININE("feminine"), NEUTER("neuter"),
    PLURAL("plural"), SINGULAR("singular"),
    FORMAL("formal"), FAMILIAR("familiar"), INTIMATE("intimate"),
    INDICATIVE("indicative"), SUBJUNCTIVE("subjunctive"), CONDITIONAL("conditional"), IMPERATIVE("imperative"), PRESUMPTIVE("presumptive"),
    FACTUAL("factual"), CONTRAFACTUAL("contrafactual"),
    _1ST("first person"), _2ND("second person"), _3RD("thrid person"),
    ACTIVE("active"), PASSIVE("passive"),
    NON_ASPECTUAL("non aspectual"), HABITUAL("habitual"), CONTINUOUS("continuous"), PERFECTIVE("perfective"),
    PRESENT("present"), FUTURE("future"), PAST("past"),
    DIRECT("direct"), OBLIQUE("oblique"), VOCATIVE("vocative");


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
