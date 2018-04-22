package com.murshid.utils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SongUtils {

    /**
     * The input cannot have newlines (\n, \r)
     */
    public static String removeTextBetweenBrackets(@Nonnull String original){
        String result = original.replaceAll("\\[.*?\\]", "");
        return result;
    }

    /**
     * Given a song's text, returns a set of unique Hindi tokens
     */
    public static Set<String> hindiTokens(@Nonnull String original){
        String[] tokens = tokenizeSong(original);
        return Arrays.stream(tokens).sorted().collect(Collectors.toSet());
    }

    /**
     * Given a song's text, returns a list with all its relevant (analyzable) tokens
     */
    public static List<String> allHindiTokens(@Nonnull String original){
        String[] tokens = tokenizeSong(original);
        return Arrays.asList(tokens);
    }

    private static String[] tokenizeSong(@Nonnull String original){
        original = original.replaceAll("\\\\n", " ");
        original = original.replaceAll("\\r", " ");
        String result = removeTextBetweenBrackets(original);
        result = result.replace("?", " ");
        result = result.replace("/", " ");
        result = result.replace(".", " ");
        result = result.replace(",", " ");
        result = result.replace("\n", " ");
        result = result.trim().replaceAll(" +", " "); //replaces any number of spaces with a single one
        String[] tokens = result.split("\\s+"); // \s+ matches all whitespaces (of any size), including blank spaces and carriages returns
        return tokens;
    }


}
