package com.murshid.utils;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;


public class WordUtilsTest {

    @Test
    public void replaceAnusvaara() throws Exception {

        String dilonAnus = "दिलोँ";
        assertEquals(dilonAnus, WordUtils.replaceAnusvaara(dilonAnus));

        String batenAnus = "बातें";
        assertEquals(batenAnus, WordUtils.replaceAnusvaara(batenAnus));

        String hindiAnus = "हिंदी";
        assertEquals(hindiAnus, WordUtils.replaceAnusvaara(hindiAnus));

        String dekungaAnus = "देखूंगा";
        String dekungaChandra = "देखूँगा";
        assertEquals(dekungaChandra, WordUtils.replaceAnusvaara(dekungaAnus));

        String milungaAnus = "मिलुंगा";
        String milungaChandra = "मिलुँगा";
        assertEquals(milungaChandra, WordUtils.replaceAnusvaara(milungaAnus));

    }

    @Test
    public void explodeHehs() throws Exception {
        //initials
        String hogaWithHehGoal = "ہوگا";
        String hogaWithHehDoachsmee = "ھوگا";
        Set<String> exploded = WordUtils.explodeHehs(hogaWithHehGoal);

        assertTrue(exploded.contains(hogaWithHehDoachsmee));

        String huinWithGoal = "ہونیں";
        String huinWithGeDoChashmee = "هونیں";
        exploded = WordUtils.explodeHehs(huinWithGeDoChashmee);
        assertTrue(exploded.contains(huinWithGoal));
    }

    @Test
    public void removeNasalizationFromRoot()  {
        assertEquals(WordUtils.removeNasalizationFromRoot("बोलूं"), "बोलू");
        assertEquals(WordUtils.removeNasalizationFromRoot("बोलूँ"), "बोलू");

        assertEquals(WordUtils.removeNasalizationFromRoot("कर"), "कर");
    }

    @Test
    public void endsWithVowel()  {
        //because it is nasalized, it "doesn't " end with vowel (but with nasalization)
        assertFalse(WordUtils.endsWithVowel("बोलूं"));

        assertFalse(WordUtils.endsWithVowel("कर"));
        assertFalse(WordUtils.endsWithVowel("बोल"));


        assertTrue(WordUtils.endsWithVowel("करना"));
        assertTrue(WordUtils.endsWithVowel("होने"));
        assertTrue(WordUtils.endsWithVowel("तू"));
        assertTrue(WordUtils.endsWithVowel("तू"));
        assertTrue(WordUtils.endsWithVowel("जो"));
        assertTrue(WordUtils.endsWithVowel("हिन्दुओ"));
    }





}
