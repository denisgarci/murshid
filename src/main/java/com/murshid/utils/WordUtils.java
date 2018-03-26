package com.murshid.utils;

import java.util.Arrays;

public class WordUtils {

    private static char ANUSVAARA = '\u0902';
    private static char CHANDRABINDU = '\u0901';

    /**
     * characters that go above the line (they should not replace anusvaara with chandrabindu)
     */
    private static char[] aboveLine = {
            '\u0904', '\u0908', '\u090d', '\u090e', '\u0910', '\u0911', '\u0912', '\u0913', '\u0914',
            '\u093f', '\u0940', '\u0945', '\u0946', '\u0947', '\u0948', '\u0949',
            '\u094a', '\u094b', '\u094c', '\u094f',
            '\u0950', '\u0953', '\u0954', '\u0955', '\u0940'
    };


    public static String replaceAnusvaara(String original){
        char[] chars = original.toCharArray();
        boolean changed = false;
        for (int i=0; i< chars.length; i++){
            if (chars[i] == ANUSVAARA){
                if (i > 0 && Arrays.binarySearch(aboveLine, chars[i - 1]) < 0 ){
                    chars[i] = CHANDRABINDU;
                    changed = true;
                }
            }
        }

        if (changed){
            return new String(chars);
        }else{
            return original;
        }
    }

}
