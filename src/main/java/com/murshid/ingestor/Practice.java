package com.murshid.ingestor;

import com.murshid.utils.SongUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Set;

public class Practice {

    static public final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";


    public static BigInteger calculateAnswer(String a, String b) {
        BigInteger ab = new BigInteger(a);
        BigInteger bc = new BigInteger(b);
        return bc.subtract(ab);
    }

    public static void main(String[] args) throws IOException {
        String fileName = "alvida.txt";
        String songText = getSong(fileName);
        Set<String> hindiTokens = SongUtils.hindiTokens(songText);

        String[] allTokens = songText.split(String.format(WITH_DELIMITER, "\\s+|\\\\n"));
        StringBuilder result = new StringBuilder();
        int index = 0;
        for(String token: allTokens){
            if (hindiTokens.contains(token)){
                index += 10;
                result.append("<span class=\"relevant\" id=\"" + index + "\">" + token + "</span>");
                result.append(" ");
            }else if (token.equals(" ")){
                result.append("&nbsp;");
            }else if (token.equals("\\n")){
                result.append("<br/>");
            }else {
                result.append(token);
            }
        }
        System.out.println(result.toString().replaceAll("\\\"", "\\\\\""));

    }






    public static String getSong(String fileName) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/gonzalodiaz/dev/murshid/src/main/resources/songs/" + fileName ), "UTF-8"));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\\n");
        }
        return sb.toString();
    }
}
