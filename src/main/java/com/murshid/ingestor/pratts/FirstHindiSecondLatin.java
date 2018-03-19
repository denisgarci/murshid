package com.murshid.ingestor.pratts;

import com.google.common.base.Strings;
import com.murshid.ingestor.Scripts;
import com.murshid.ingestor.utils.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class FirstHindiSecondLatin {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstHindiSecondLatin.class);

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
                    "UPDATE entries SET latin = ?, hindi = ?  where id = ?");

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT head, id from entries where hindi  is null and urdu is null and latin is null");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String head = rs.getString("head");

                String tokens[] = head.split(" ");
                if (tokens.length != 2) continue;

                if (Strings.isNullOrEmpty(tokens[0]) || Strings.isNullOrEmpty(tokens[1])) {
                    continue;
                }

                if (WordUtils.scriptType(tokens[0]) != Scripts.DEVANAGARI
                        || WordUtils.scriptType(tokens[1]) != Scripts.LATIN){
                    continue;
                }

                String hindi = tokens[0];
                String latin  = tokens[1];

                ps.setString(1, latin);
                ps.setString(2, hindi);
                ps.setInt(3, id);
                ps.execute();
                i++;

                if (i % 100 == 0) {
                    System.out.println("updated " + i + " records");
                    con.commit();
                }

            }
            System.out.println("finished, total =" + i);
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
