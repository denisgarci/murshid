package com.murshid.ingestor.utils;

import com.google.common.base.Strings;
import com.murshid.ingestor.enums.Scripts;

import java.util.Arrays;

public class IngestorWordUtils {

    public static Scripts scriptType(String word) {
        char first = word.charAt(0);
        if (first == '\u202D') first = word.charAt(1);
        if (first >= '\u0901' && first <= '\u0970') {
            return Scripts.DEVANAGARI;
        } else if (
                (first >= '\u0600' && first <= '\u06FF')
                || (first >= '\u0750' && first <= '\u077F')
                || (first >= '\u08A0' && first <= '\u08FF')
                || (first >= '\uFE70' && first <= '\uFEFF')) {
            return Scripts.NASTALIQ;
        } else {
            return Scripts.LATIN;
        }
    }

    public static boolean onlyLastWordIsUrdu(String sentence){
        String[] tokens = sentence.split(" ");
        String last = tokens[tokens.length-1];
        if (!Strings.isNullOrEmpty(last) && scriptType(last) == Scripts.NASTALIQ){
            if (tokens.length> 1){
                String beforeTheLast = tokens[tokens.length-2];
                return (!Strings.isNullOrEmpty(beforeTheLast) && scriptType(beforeTheLast) != Scripts.NASTALIQ);
            }
        }
        return false;
    }

    public static boolean lastWordIsUrdu(String sentence){
        sentence = '\u202D' + sentence;
        String[] tokens = sentence.split(" ");
        String last = tokens[0];
        return !Strings.isNullOrEmpty(last) && scriptType(last) == Scripts.NASTALIQ;
    }

    public static String stripLastWord(String original){
        String[] tokens = original.split(" ");
        if (tokens.length < 2) throw new IllegalArgumentException("there is only one canonicalWord here");
        String[] sansLast = Arrays.copyOfRange(tokens, 0, tokens.length - 1);
        return String.join(" ", sansLast);
    }

    public static String getLastWord(String sentence){
        String[] tokens =  sentence.split(" ");
        if (lastWordIsUrdu(sentence)){
            return tokens[0];
        }else{
            return tokens[tokens.length -1];
        }
    }

    public static boolean endsWithUrduSafe(String sentence, String part){
        sentence = '\u202D' + sentence;
        part = part;
        return sentence.endsWith(part);
    }

    public static String normalizeHindi(String original){

        String kDot = new String(new char[]{2393});
        String kPlusDot = new String(new char[]{2326, 2364});

        return original.replace(kPlusDot, kDot);
    }
}
