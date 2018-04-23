package com.murshid.ingestor.platts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class ShortenMeanings {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortenMeanings.class);

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
                    "UPDATE entries SET body = ? where id = ?");

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT body, id from entries where length(body) > 500");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String body = rs.getString("body");

                body = process(body);

//                ps.setString(1, body);
//                ps.setInt(2, id);
//                ps.execute();
                i++;

                if (i % 100 == 0) {
                    System.out.println("updated " + i + " records");
             //       con.commit();
                }

            }
            System.out.println("finished");
            //con.commit();

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


    private static String process(String body) {
        LOGGER.info("ORIGINAL ******\n" + body);
        body = body.substring(0, Math.min(501, body.length()));
        int location = body.lastIndexOf(":—");
        if (location > 0) {
            body = body.substring(0, location);
        } else {
            location = body.lastIndexOf(";");
            if (location > 0) {
                body = body.substring(0, location);
            }

        }
        LOGGER.info("RESULT *******\n" + body);
        return body;
    }

}
