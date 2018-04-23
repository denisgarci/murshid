package com.murshid.ingestor.platts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class FileToHindiWords {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileToHindiWords.class);

    public static void main(String[] args) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://localhost:3306/pratts";
        String user = "root";
        String password = "";

        String urudResult =  "/Users/gonzalodiaz/dev/ingestor/src/main/resources/hindi_equivalent.txt";

        try (BufferedReader bur = new BufferedReader(new InputStreamReader(new FileInputStream(urudResult)))){

            con = DriverManager.getConnection(url, user, password);

            PreparedStatement insertInHindiWords = con.prepareStatement("INSERT INTO hindi_words (word, urdu_spelling) values (?, ?) ");

            PreparedStatement selectHindiWord = con.prepareStatement("SELECT 1 FROM hindi_words where word = ?  ");

            PreparedStatement allUrdus = con.prepareStatement("SELECT * from urdu_words");


            rs = allUrdus.executeQuery();

            while(rs.next()){
                String urduWord = rs.getString("word");
                String hindiWord = rs.getString("hindi_spelling");
                selectHindiWord.setString(1, hindiWord);
                ResultSet inHindi =selectHindiWord.executeQuery();
                if (inHindi.next()){
                    LOGGER.info("hindi word {}  already present in hindi_words");
                }else{
                    insertInHindiWords.setString(1, hindiWord);
                    insertInHindiWords.setString(2, urduWord);
                    insertInHindiWords.execute();
                    LOGGER.info("inserting hindiWord={} urduSpelling={} in hindi_words", hindiWord, urduWord);
                }
                inHindi.close();
            }


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
