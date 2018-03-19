package com.murshid.ingestor.pratts;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SeparateEquals {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeparateEquals.class);

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
                    "UPDATE entries SET split_head = ?  where id = ?");

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT head, id from entries where hindi  is null and urdu is null and latin is null");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String head = rs.getString("head");

                int equalsLocation = head.indexOf(" = ");
                if (equalsLocation <0) continue;

                String tokens[] = head.split(" = ");



                if (Strings.isNullOrEmpty(tokens[0]) ){
                    continue;
                }


                String splitHead = tokens[0];

                ps.setString(1, splitHead);
                ps.setInt(2, id);
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



}
