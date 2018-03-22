package com.murshid.utils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SongUtils {

    public static String removeTextBetweenBrackets(@Nonnull String original){
        String result = original.replaceAll("\\[.*?\\]", "");
        return result;
    }

    /**
     * Given a song's text, returns a set of unique Hindi tokens
     */
    public static Set<String> hindiTokens(@Nonnull String original){
        String result = removeTextBetweenBrackets(original);
        result = result.replace("?", " ");
        result = result.replace("/", " ");
        result = result.replace(".", " ");
        result = result.replace(",", " ");
        result = result.replace("\n", " ");
        result = result.trim().replaceAll(" +", " "); //replaces any number of spaces with a single one
        String[] tokens = result.split(" ");
        return Arrays.stream(tokens).sorted().collect(Collectors.toSet());
    }
}
