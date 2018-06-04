package com.murshid.utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import org.antlr.v4.runtime.CharStream;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.IntStream;

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

    private static char[] vowels = { 'अ','आ', 'ा',  'ई' , 'ी',  'इ', 'ि',   'उ', 'ु', 'ऊ', 'ू',
            'ऋ', 'ए', 'े', 'ऐ', 'ै',  'ओ', 'ो', 'औ', 'ौ' };

    @VisibleForTesting
    protected static String removeNasalizationFromRoot(String root){
        if (root.endsWith("ं") || root.endsWith("ँ")){
            return root.substring(0, root.length()-1);
        }else{
            return root;
        }
    }

    public static boolean endsWithVowel(String word){
        char last = word.charAt(word.length()-1);
        String allVowels = new String(vowels);
        return allVowels.indexOf(last) > -1;
    }

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

    private static char HEH_DOCHASHMEE = '\u06be';
    private static char HEH_GOAL = '\u06C1';
    private static char HEH = '\u0647';

    public static Set<String> explodeHehs(String original) {
        StringBuilder sb = new StringBuilder(original);
        for (int i =0 ; i< sb.length(); i++){
            char letter = sb.charAt(i);
            if (letter == HEH_DOCHASHMEE || letter == HEH_GOAL || letter == HEH){
                sb.setCharAt(i, '_');
            }
        }

        String allHeh = sb.toString().replaceAll("_" , new String(new char[]{HEH}));
        String allHehGoal = sb.toString().replaceAll("_" , new String(new char[]{HEH_GOAL}));
        String allHehDoChashmee = sb.toString().replaceAll("_" , new String(new char[]{HEH_DOCHASHMEE}));

        return Sets.newHashSet(allHeh, allHehDoChashmee, allHehGoal);
    }

}
