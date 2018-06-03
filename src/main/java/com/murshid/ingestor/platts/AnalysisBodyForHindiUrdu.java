package com.murshid.ingestor.platts;

import com.murshid.ingestor.enums.Scripts;
import com.murshid.ingestor.utils.IngestorWordUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class AnalysisBodyForHindiUrdu {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisBodyForHindiUrdu.class);

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
                    "UPDATE pratts SET urdu = ?, hindi_word = ?, latin = ?  where id = ?");

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT body, id from pratts where hindi_word  is null and urdu is null ");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String body = rs.getString("body");

                String body2 = replaceFirstCapitals(body);
                if (!body.equals(body2)){
                    body = body2;
                }

                String tokens[] = body.split(" ");

                if (tokens.length < 3){
                    LOGGER.info("id={} does not have enough tokens in the body", id);
                    continue;
                }

                if (StringUtils.isEmpty(tokens[1]) || StringUtils.isEmpty(tokens[2]) || StringUtils.isEmpty(tokens[3] )){
                    LOGGER.info("id={} has tokens too short", id);
                    continue;
                }

                String supposedUrdu = null;
                if (IngestorWordUtils.scriptType(tokens[1]) != Scripts.NASTALIQ){
                    continue;
                }else{
                    supposedUrdu = tokens[1];
                }

                String supposedHindi = null;
                if (IngestorWordUtils.scriptType(tokens[2]) != Scripts.DEVANAGARI){
                    continue;
                }else{
                    supposedHindi = tokens[2];
                }

                String supposedLatin = null;
                if (IngestorWordUtils.scriptType(tokens[3]) != Scripts.LATIN){
                    continue;
                }else{
                    supposedLatin = tokens[3];
                }


                if (supposedUrdu== null || supposedHindi == null || supposedLatin == null){
                    continue;
                }

                ps.setString(1, supposedUrdu);
                ps.setString(2, supposedHindi);
                ps.setString(3, supposedLatin);
                ps.setInt(4, id);
                ps.execute();
                LOGGER.info("hindiWordIndex={}, urdu={}, latin={}",  supposedHindi, supposedUrdu, supposedLatin);

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


    static String[] replaceables={
            "H P", "H S", "P H", "P A", "A P", "P & H", "P & T", "T & P",
            "S & P", "T P"
    };

    public static String replaceFirstCapitals(String original){
        for (String beginning : replaceables){
            if (original.startsWith(beginning)){
                original = "L" + original.substring(beginning.length());
                return original;
            }
        }
        return original;
    }


}
