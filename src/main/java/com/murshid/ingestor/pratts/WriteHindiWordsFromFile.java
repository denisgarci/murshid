package com.murshid.ingestor.pratts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class WriteHindiWordsFromFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteHindiWordsFromFile.class);

    public static void main(String[] args) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://localhost:3306/pratts";
        String user = "root";
        String password = "";

        try {

            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);


            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO hindi_words (word) values (?)");

            String csvFile = "/Users/gonzalodiaz/dev/ingestor/src/main/resources/hindi_words.txt";
            BufferedReader br = null;
            String line = "";


                br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"));
                int i= 0;
                while ((line = br.readLine()) != null) {

                    String word = line;
                    ps.setString(1, word);
                    ps.execute();
                    i++;

                    if (i % 20 == 0) {
                        LOGGER.info("Writing hindiWord={}", word);
                        con.commit();
                    }



                }
                con.commit();
                LOGGER.info("Finished entered {} words", i);

            } catch (IOException  |SQLException e ) {
                e.printStackTrace();

            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



}
