package com.murshid.persistence.domain;


/**
 * Interface that should be implemented by superclass models (those annotated {@link
 * javax.persistence.MappedSuperclass}) to allow them to be built using the fluent style.
 *
 * see http://stackoverflow.com/q/10941464 for a more detailed explanation
 *
 * @param <T> The model type
 */
public interface FluentModel<T extends FluentModel> {

    /**
     * Return {@code this}.
     *
     * @return The entity.
     */
    T self();

}

