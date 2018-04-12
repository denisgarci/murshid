package com.murshid.ingestor.wikitionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;

public class ExtractAccidenceClasses {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractAccidenceClasses.class);
    private static final Logger FILE_LOGGER = LoggerFactory.getLogger("ingestion_errors");

    static String[] problematic = new String[] {
        "आलू", "बद", "बोल", "हस्ती", "होता", "अरब", "अली", "आना", "आया"
    };

    public static void main(String[] args) throws Exception{



        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://localhost:3306/pratts";
        String user = "root";
        String password = "";

        try {

            con = DriverManager.getConnection(url, user, password);


            Statement select = con.createStatement();
            //rs = select.executeQuery("SELECT canonicalWord from hindi_words where canonicalWord >= 'उभय' ");
            rs = select.executeQuery("SELECT word from hindi_words where word >= 'आया' ");

            int i = 0;
            while (rs.next()) {
                String word = null;
                try {

                     word = rs.getString("canonicalWord");

                    if (Arrays.binarySearch(problematic, word) > -1)
                        continue;

                    LOGGER.info("canonicalWord is " + word);

                    WikitionaryCaller caller = new WikitionaryCaller();
                    org.jsoup.nodes.Document document = WikitionaryCaller.documentForWord(caller, word);

                    WikiUtils.populateEntry(word, document);
                }catch (Exception ex){
                    FILE_LOGGER.error("canonicalWord={} failed ", word,  ex);
                }

            }

        } catch (SQLException e) {
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
