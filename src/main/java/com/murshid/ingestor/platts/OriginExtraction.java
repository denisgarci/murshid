package com.murshid.ingestor.platts;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class OriginExtraction {

    private static final Logger LOGGER = LoggerFactory.getLogger(OriginExtraction.class);

    public static void main(String[] args) {

        Connection con = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://localhost:3306/pratts";
        String user = "root";
        String password = "";

        try {

            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);


            PreparedStatement ps = con.prepareStatement(
                    "UPDATE pratts SET meaning = ?, origin = ?  where hindi_word = ? and word_index =? ");

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT meaning, hindi_word, word_index from pratts ");

            int i = 0;
            while (rs.next()) {
                int word_index = rs.getInt("word_index");
                String hindi_word = rs.getString("hindi_word");
                String meaning = rs.getString("meaning");

                Pair<String, String> origMean =  OriginUtil.extractMeaning(meaning, hindi_word, word_index);

                if (origMean.getLeft() == null){
                    continue;
                }


                ps.setString(1, origMean.getRight());
                ps.setString(2, origMean.getLeft());
                ps.setString(3, hindi_word);
                ps.setInt(4, word_index);
                ps.execute();

                i++;

                if (i % 100 == 0) {
                    System.out.println("updated " + i + " records");
                    con.commit();
                }

            }
            System.out.println("finished");
            con.commit();

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


    static String[] replaceables={
            "H P", "H S", "P H", "P A", "A P", "P & H", "P & T", "T & P",
            "S & P", "T P"
    };

    public static String replaceFirstCapitals(String original){
        for (String beginning : replaceables){
            if (original.startsWith(beginning)){
                original = "L" + original.substring(beginning.length());
                return original;
            }
        }
        return original;
    }


}
