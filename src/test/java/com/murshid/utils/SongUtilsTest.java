package com.murshid.utils;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class SongUtilsTest {

    @Test
    public void removeTextBetweenBracketsSimple() throws Exception {
        String test = "Hello world [something] here I am";
        test = SongUtils.removeTextBetweenBrackets(test);
        assertEquals(test, "Hello world  here I am");
    }

    @Test
    public void removeTextBetweenBracketsGreedy() throws Exception {
        String test = "Hello world [something] here I am [something else] again";
        test = SongUtils.removeTextBetweenBrackets(test);
        assertEquals(test, "Hello world  here I am  again");
    }

    @Test
    public void extractHindiTokens() throws Exception {
        String test = " तद मैं कमली\nतद मैं कमली \n\n मैं";

        Set<String> hindiTokens = SongUtils.hindiTokens(test);

        Set<String> expected = com.google.common.collect.ImmutableSet.of("कमली", "तद", "मैं");

        assertEquals(hindiTokens, expected);
    }

    @Test
    public void extractHindiTokensWithBrackets() throws Exception {
        String test = " तद मैं कमली\nतद [refrain] मैं कमली \n\n मैं [repeats refrain]";

        Set<String> hindiTokens = SongUtils.hindiTokens(test);

        Set<String> expected = com.google.common.collect.ImmutableSet.of("कमली", "तद", "मैं");

        assertEquals(hindiTokens, expected);
    }

}
