package com.murshid.ingestor.pratts;

import com.google.common.base.Strings;
import com.murshid.ingestor.Scripts;
import com.murshid.ingestor.utils.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;

public class LastUrduRestLatin {

    private static final Logger LOGGER = LoggerFactory.getLogger(LastUrduRestLatin.class);

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
                    "UPDATE entries SET latin = ?, urdu = ?  where id = ?");

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT head, id from entries where hindi  is null and urdu is null and latin is null");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String head = rs.getString("head");

                String tokens[] = head.split(" ");
                if (tokens.length <= 2) continue;

//                if (Strings.isNullOrEmpty(tokens[0]) || Strings.isNullOrEmpty(tokens[1]) || Strings.isNullOrEmpty(tokens[2])) {
//                    continue;
//                }

                if (Strings.isNullOrEmpty(tokens[0]) ||  WordUtils.scriptType(tokens[0]) != Scripts.NASTALIQ){
                    continue;
                }

                boolean restLatin = true;
                for (int p=1; p<tokens.length; p++){
                    if (Strings.isNullOrEmpty(tokens[p]) || WordUtils.scriptType(tokens[p]) != Scripts.LATIN){
                        restLatin = false;
                        break;
                    }
                }
                if (!restLatin) continue;

                String urdu = tokens[0];
                String[] latinWords = Arrays.copyOfRange(tokens, 1, tokens.length);
                String latin  = String.join(" ", latinWords);

                LOGGER.info("head={}", head);
                LOGGER.info("urdu={} latin={}", urdu, latin);
                ps.setString(1, latin);
                ps.setString(2, urdu);
                ps.setInt(3, id);
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