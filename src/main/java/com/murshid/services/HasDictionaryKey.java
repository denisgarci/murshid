package com.murshid.services;

import com.murshid.models.DictionaryKey;
import com.murshid.models.enums.PartOfSpeech;

public interface HasDictionaryKey {

    DictionaryKey getDictionaryKey();

    String getHindiWord();

    int getWordIndex();

    PartOfSpeech getPartOfSpeech();

    String getMeaning();

}
