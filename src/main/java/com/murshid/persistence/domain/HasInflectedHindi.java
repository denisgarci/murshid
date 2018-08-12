package com.murshid.persistence.domain;

public interface HasInflectedHindi <T> extends FluentModel {

    String getHindi();

    T setUrdu(String urdu);
}
