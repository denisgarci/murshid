package com.murshid.persistence.domain;

public interface HasInflectedHindi <T> extends FluentModel {

    String getInflectedHindi();

    T setInflectedUrdu(String inflectedUrdu);
}
