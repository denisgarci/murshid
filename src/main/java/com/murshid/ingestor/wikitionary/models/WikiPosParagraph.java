package com.murshid.ingestor.wikitionary.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the paragraph entry under a Part-of-speech header
 */
public class WikiPosParagraph {

    public WikiPartOfSpeech partOfSpeech;

    public List<Accidence> accidence = new ArrayList<>();

    public Optional<String> urduSpelling;

    public List<String> meanings = new ArrayList<>();
}
