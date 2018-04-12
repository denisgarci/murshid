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

        //iniciales
        String hogaWithHehGoal = "ہوگا";
        String hogaWithHehDoachsmee = "ھوگا";
        Set<String> exploded = WordUtils.explodeHehs(hogaWithHehGoal);

        assertTrue(exploded.contains(hogaWithHehDoachsmee));

        String huinWithGoal = "ہونیں";
        String huinWithGeDoChashmee = "هونیں";
        exploded = WordUtils.explodeHehs(huinWithGeDoChashmee);
        assertTrue(exploded.contains(huinWithGoal));


    }




}
