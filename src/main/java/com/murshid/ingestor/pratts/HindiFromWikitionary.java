package com.murshid.ingestor.pratts;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class HindiFromWikitionary {

    private static final Logger LOGGER = LoggerFactory.getLogger(HindiFromWikitionary.class);

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
                    "UPDATE pratts SET hindi_word =? where id = ?");

            PreparedStatement selectHindiInRekhta = con.prepareStatement(
                    "SELECT hindi_word from wikitionary where urdu_spelling=? LIMIT 1");

            Statement select = con.createStatement();
            rs = select.executeQuery("select * from pratts where hindi_word  is null ");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String urdu = rs.getString("urdu");

                selectHindiInRekhta.setString(1, urdu);
                ResultSet rsHindiRekhta = selectHindiInRekhta.executeQuery();
                if (rsHindiRekhta.next()){
                    String hindiFromRekhta = rsHindiRekhta.getString(1);
                    if (!StringUtils.isEmpty(hindiFromRekhta)){

                        LOGGER.info("updating pratts row with urdu={} with hindi={}", urdu, hindiFromRekhta);
                        ps.setString(1, hindiFromRekhta);
                        ps.setInt(2, id);
                        ps.execute();
                        i++;
                    }
                }


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



}
