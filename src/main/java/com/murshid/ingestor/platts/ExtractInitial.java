package com.murshid.ingestor.platts;

import java.sql.*;

public class ExtractInitial {

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
                    "UPDATE hindi_words SET initial = ?   where word = ? ");

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT word from hindi_words");


            int i = 0;
            while (rs.next()) {

                String word = rs.getString("canonicalWord");
                char initial = word.charAt(0);
                if (initial == '_'){
                    initial = word.charAt(1);
                }

                ps.setString(1, new String(new char[]{initial}));
                ps.setString(2, word);
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
