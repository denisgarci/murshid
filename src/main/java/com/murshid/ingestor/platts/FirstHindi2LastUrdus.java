package com.murshid.ingestor.platts;

import com.google.common.base.Strings;
import com.murshid.ingestor.enums.Scripts;
import com.murshid.ingestor.utils.IngestorWordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;

public class FirstHindi2LastUrdus {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstHindi2LastUrdus.class);

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
                    "UPDATE entries SET latin = ?, urdu = ?, hindi = ?  where id = ?");

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT head, id from entries where hindi  is null and urdu is null and latin is null");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String head = rs.getString("head");

                String tokens[] = head.split(" ");
                if (tokens.length <= 3) continue;

                if (Strings.isNullOrEmpty(tokens[0]) || Strings.isNullOrEmpty(tokens[1]) || Strings.isNullOrEmpty(tokens[2])) {
                    continue;
                }

                if (IngestorWordUtils.scriptType(tokens[0]) != Scripts.NASTALIQ && IngestorWordUtils.scriptType(tokens[1]) != Scripts.NASTALIQ
                        || IngestorWordUtils.scriptType(tokens[2]) != Scripts.DEVANAGARI){
                    continue;
                }

                boolean restLatin = true;
                for (int p=3; p<tokens.length; p++){
                    if (Strings.isNullOrEmpty(tokens[p]) || IngestorWordUtils.scriptType(tokens[p]) != Scripts.LATIN){
                        restLatin = false;
                        break;
                    }
                }
                if (!restLatin) continue;

                String urdu = tokens[0].concat(" ").concat(tokens[1]);
                String hindi = tokens[2];
                String[] latinWords = Arrays.copyOfRange(tokens, 3, tokens.length);
                String latin  = String.join(" ", latinWords);

                ps.setString(1, latin);
                ps.setString(2, urdu);
                ps.setString(3, hindi);
                ps.setInt(4, id);
                ps.execute();
                LOGGER.info("head={}", head);
                LOGGER.info("hindiWordIndex={}, urdu={}, latin={}", hindi, urdu, latin);

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
