package com.murshid.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WordUtilsTest {

    @Test
    public void replaceAnusvaara() throws Exception {

        String dilonAnus = "दिलोँ";
        assertEquals(dilonAnus, IngestorWordUtils.replaceAnusvaara(dilonAnus));

        String batenAnus = "बातें";
        assertEquals(batenAnus, IngestorWordUtils.replaceAnusvaara(batenAnus));

        String hindiAnus = "हिंदी";
        assertEquals(hindiAnus, IngestorWordUtils.replaceAnusvaara(hindiAnus));

        String dekungaAnus = "देखूंगा";
        String dekungaChandra = "देखूँगा";
        assertEquals(dekungaChandra, IngestorWordUtils.replaceAnusvaara(dekungaAnus));

        String milungaAnus = "मिलुंगा";
        String milungaChandra = "मिलुँगा";
        assertEquals(milungaChandra, IngestorWordUtils.replaceAnusvaara(milungaAnus));

    }



}
