package com.murshid.ingestor.utils;

import com.murshid.ingestor.Scripts;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class WordUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordUtilsTest.class);
    private static final Logger ERR_LOGGER = LoggerFactory.getLogger(WordUtilsTest.class);

    @Test
    public void scriptType() throws Exception {
        assertEquals(WordUtils.scriptType("آبائي"), Scripts.NASTALIQ);
        assertEquals(WordUtils.scriptType("अबाक"), Scripts.DEVANAGARI);
        assertEquals(WordUtils.scriptType("hello"), Scripts.LATIN);
    }

    @Test
    public void onlyLastWordIsUrdu() throws Exception {
        String entry = "अबाक Some Latin آبائي";
        assertTrue(WordUtils.onlyLastWordIsUrdu(entry));

        String entry2 = "अबाक Some Latin آبائي hello";
        assertFalse(WordUtils.onlyLastWordIsUrdu(entry2));

        String entry3 = "अबाक Some Latin آبائي آبائي";
        assertFalse(WordUtils.onlyLastWordIsUrdu(entry3));
    }

    @Test
    public void stripLastWord() throws Exception {
        String entry = "one two three";
        assertEquals(WordUtils.stripLastWord(entry), "one two");

        String entryWithUrdu = "अबाक Some Latin آبائي";
        assertEquals(WordUtils.stripLastWord(entryWithUrdu), "अबाक Some Latin");
    }

    @Test
    public void getLastWord() throws Exception {
        String entry = "one two three";
        assertEquals(WordUtils.getLastWord(entry), "three");

        String entryWithUrdu = "अबाक Some Latin آبائي";
        assertEquals(WordUtils.getLastWord(entryWithUrdu), "آبائي");


        String mixed = "अबाक Some Latin خاستن उसना Some more Latin آبائي";
        assertEquals(WordUtils.getLastWord(entryWithUrdu), "آبائي");

    }

    @Test
    public void endsWithUrduSafe() throws Exception {
        String entry = "one two three";
        assertTrue(WordUtils.endsWithUrduSafe(entry, "two three"));

        String entryWithUrdu = "अबाक Some Latin آبائي";
        assertTrue(WordUtils.endsWithUrduSafe(entryWithUrdu, "آبائي"));
        assertTrue(WordUtils.endsWithUrduSafe(entryWithUrdu, "Latin آبائي"));

        LOGGER.info("lalaal");
        LOGGER.info("lalaal");
        String mixed = "अबाक Some Latin خاستن उसना Some more Latin آبائي";
        assertTrue(WordUtils.endsWithUrduSafe(mixed, "آبائي"));
        assertTrue(WordUtils.endsWithUrduSafe(mixed, "Latin آبائي"));

    }

    @Test
    public void compactKhPlusDot() throws Exception {
        String xhubsuratSeparateDot = "ख़ूबसूरत";
        String xhubsuratNonSeparateDot = "ख़ूबसूरत";

        assertNotEquals(xhubsuratNonSeparateDot, xhubsuratSeparateDot);

        String normalized = WordUtils.normalizeHindi(xhubsuratSeparateDot);
        assertEquals(xhubsuratNonSeparateDot, normalized);

    }




}
