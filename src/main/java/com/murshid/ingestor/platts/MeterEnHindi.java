package com.murshid.ingestor.platts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.*;

public class MeterEnHindi {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeterEnHindi.class);

    public static void main(String[] args) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://localhost:3306/pratts";
        String user = "root";
        String password = "";

        String wordsFile = "/Users/gonzalodiaz/dev/ingestor/src/main/resources/urdu_words_from_db.txt";

        try (BufferedWriter buw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wordsFile)))) {


            con = DriverManager.getConnection(url, user, password);
            PreparedStatement ps = con.prepareStatement("SELECT word from urdu_words where hindi_spelling is null order by word limit 50000");

            rs = ps.executeQuery();

            while (rs.next()) {
                buw.write(rs.getString(1));
                buw.newLine();
            }
            buw.close();

            LOGGER.info("Finished moving words to file words");

        } catch (IOException | SQLException e) {
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
