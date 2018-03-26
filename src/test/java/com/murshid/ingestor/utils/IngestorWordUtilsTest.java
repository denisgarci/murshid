package com.murshid.ingestor.utils;

import com.murshid.ingestor.enums.Scripts;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class IngestorWordUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestorWordUtilsTest.class);
    private static final Logger ERR_LOGGER = LoggerFactory.getLogger(IngestorWordUtilsTest.class);

    @Test
    public void scriptType() throws Exception {
        assertEquals(IngestorWordUtils.scriptType("آبائي"), Scripts.NASTALIQ);
        assertEquals(IngestorWordUtils.scriptType("अबाक"), Scripts.DEVANAGARI);
        assertEquals(IngestorWordUtils.scriptType("hello"), Scripts.LATIN);
    }

    @Test
    public void onlyLastWordIsUrdu() throws Exception {
        String entry = "अबाक Some Latin آبائي";
        assertTrue(IngestorWordUtils.onlyLastWordIsUrdu(entry));

        String entry2 = "अबाक Some Latin آبائي hello";
        assertFalse(IngestorWordUtils.onlyLastWordIsUrdu(entry2));

        String entry3 = "अबाक Some Latin آبائي آبائي";
        assertFalse(IngestorWordUtils.onlyLastWordIsUrdu(entry3));
    }

    @Test
    public void stripLastWord() throws Exception {
        String entry = "one two three";
        assertEquals(IngestorWordUtils.stripLastWord(entry), "one two");

        String entryWithUrdu = "अबाक Some Latin آبائي";
        assertEquals(IngestorWordUtils.stripLastWord(entryWithUrdu), "अबाक Some Latin");
    }

    @Test
    public void getLastWord() throws Exception {
        String entry = "one two three";
        assertEquals(IngestorWordUtils.getLastWord(entry), "three");

        String entryWithUrdu = "अबाक Some Latin آبائي";
        assertEquals(IngestorWordUtils.getLastWord(entryWithUrdu), "آبائي");


        String mixed = "अबाक Some Latin خاستن उसना Some more Latin آبائي";
        assertEquals(IngestorWordUtils.getLastWord(entryWithUrdu), "آبائي");

    }

    @Test
    public void endsWithUrduSafe() throws Exception {
        String entry = "one two three";
        assertTrue(IngestorWordUtils.endsWithUrduSafe(entry, "two three"));

        String entryWithUrdu = "अबाक Some Latin آبائي";
        assertTrue(IngestorWordUtils.endsWithUrduSafe(entryWithUrdu, "آبائي"));
        assertTrue(IngestorWordUtils.endsWithUrduSafe(entryWithUrdu, "Latin آبائي"));

        LOGGER.info("lalaal");
        LOGGER.info("lalaal");
        String mixed = "अबाक Some Latin خاستن उसना Some more Latin آبائي";
        assertTrue(IngestorWordUtils.endsWithUrduSafe(mixed, "آبائي"));
        assertTrue(IngestorWordUtils.endsWithUrduSafe(mixed, "Latin آبائي"));

    }

    @Test
    public void compactKhPlusDot() throws Exception {
        String xhubsuratSeparateDot = "ख़ूबसूरत";
        String xhubsuratNonSeparateDot = "ख़ूबसूरत";

        assertNotEquals(xhubsuratNonSeparateDot, xhubsuratSeparateDot);

        String normalized = IngestorWordUtils.normalizeHindi(xhubsuratSeparateDot);
        assertEquals(xhubsuratNonSeparateDot, normalized);

    }




}
