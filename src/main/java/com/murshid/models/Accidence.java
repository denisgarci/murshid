package com.murshid.models;

public enum Accidence {
    MASCULINE("masculine gender"), FEMININE("feminine gender"), GENDER_INCOMPLETE("gender incomplete"),
    NEUTER_GENDER("neuter gender"), PLURAL_NUMBER("plural number"), SINGULAR_NUMBER("singular number");

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
