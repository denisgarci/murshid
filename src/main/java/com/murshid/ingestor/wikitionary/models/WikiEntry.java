package com.murshid.ingestor.wikitionary.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WikiEntry {

    public String hindiEntry;

    public Optional<String> IPAPronunciation;

    public Optional<String> etymology;

    public List<WikiPosParagraph> posParagraphs = new ArrayList<>();
}
