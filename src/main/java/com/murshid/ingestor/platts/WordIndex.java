package com.murshid.ingestor.platts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class WordIndex {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordIndex.class);

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
                    "UPDATE pratts SET word_index = ? where hindi_word =? and    id = ?");

            Statement select = con.createStatement();
            rs = select.executeQuery("select count(*), hindi_word from pratts where word_index is null   \n" +
                                     "group by hindi_word\n" +
                                     "having count(*) > 1");

            PreparedStatement selectByWord = con.prepareStatement("SELECT * from pratts where hindi_word=?");

            int i = 0;
            while (rs.next()) {

                String hw = rs.getString("hindi_word");

                selectByWord.setString(1, hw);
                ResultSet reSameHw = selectByWord.executeQuery();
                int index = 0;
                while (reSameHw.next()){
                    int id = reSameHw.getInt("id");
                    ps.setInt(1, index);
                    ps.setString(2, hw);
                    ps.setInt(3, id);
                    ps.execute();
                    index++;
                    i++;
                }
                reSameHw.close();



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
