package com.murshid.ingestor.pratts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrimApostrophesFromHead {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrimApostrophesFromHead.class);

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
            rs = select.executeQuery("SELECT head, id from entries where hindi  is null and urdu is null and latin is null and split_head is null");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String head = rs.getString("head");


                Pattern p = Pattern.compile( "\'([^\']*)\'" );
                Matcher m = p.matcher( head );

                if (m.find() && head.startsWith("'")){
                    LOGGER.info("head={}", head);
                    LOGGER.info("extraction={}", m.group());

//                    ps.setString(1, splitHead);
//                    ps.setInt(2, id);
//                    ps.execute();
                    i++;

                }else{
                    continue;
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
