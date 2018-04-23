package com.murshid.ingestor.platts;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OriginUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(OriginUtil.class);

    static String[] replaceables={
            "H P", "H S", "P H", "P A", "A P", "P & H", "P & T", "T & P",
            "S & P", "T P", "A", "S", "P", "H", "T", "E", "K"
    };

    public static Pair<String, String> extractMeaning(String meaning, String hindiWord, int wordIndex){
        for (String expression : replaceables){
            if (meaning.startsWith(expression)) {
                return org.apache.commons.lang3.tuple.Pair.of(expression, meaning.substring(expression.length()));
            }

        }
        if (meaning.isEmpty()){
            LOGGER.error(" hindiWord={} wordIndex={} have empty meaning", hindiWord, wordIndex);
            throw new RuntimeException();
        }

        if (StringUtils.isAllUpperCase(meaning.substring(0, 1))){
            LOGGER.error("meaning={} does not have expected origin for hindiWord={} wordIndex={}", meaning, hindiWord, wordIndex);
            throw new RuntimeException();
        }
        return Pair.of(null, meaning);
    }

}
