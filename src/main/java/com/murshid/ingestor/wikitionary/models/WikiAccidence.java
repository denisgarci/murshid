package com.murshid.ingestor.wikitionary.models;

public enum WikiAccidence {
    MASCULINE("masculine gender"), FEMININE("feminine gender"), GENDER_INCOMPLETE("gender incomplete"),
    NEUTER_GENDER("neuter gender"), PLURAL_NUMBER("plural number"), SINGULAR_NUMBER("singular number");

    private String label;

    WikiAccidence(String label){
        this.label = label;
    }

    public static WikiAccidence fromLabel(String label){
        for (WikiAccidence accidence : WikiAccidence.values()){
            if (accidence.label.equals(label)){
                return accidence;
            }
        }
        throw new IllegalArgumentException("label " + label + " not supported");
    }


}
