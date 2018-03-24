package com.murshid.ingestor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

public class Practice {

    public static BigInteger calculateAnswer(String a, String b) {
        BigInteger ab = new BigInteger(a);
        BigInteger bc = new BigInteger(b);
        return bc.subtract(ab);
    }

    public static void main(String[] args) throws IOException {
        String fileName = "alvida.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/gonzalodiaz/dev/ingestor/src/main/resources/songs/" + fileName ), "UTF-8"));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\\n");
        }

        sb.insert(0, "\"");
        sb.append("\",");
        System.out.println(sb.toString());

    }
}
