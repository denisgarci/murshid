package com.murshid.ingestor.platts;

import com.murshid.ingestor.enums.Scripts;
import com.murshid.ingestor.utils.IngestorWordUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static com.murshid.ingestor.platts.AnalysisBodyForHindiUrdu.replaceFirstCapitals;

public class AnalysisBody2ForHindiUrdu {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisBody2ForHindiUrdu.class);

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
                    "UPDATE pratts SET urdu = ?, latin = ?  where id = ?");

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

                String supposedLatin = null;
                if (IngestorWordUtils.scriptType(tokens[2]) != Scripts.LATIN){
                    continue;
                }else{
                    supposedLatin = tokens[2];
                }


                if (supposedUrdu== null ||  supposedLatin == null){
                    continue;
                }

                ps.setString(1, supposedUrdu);
                ps.setString(2, supposedLatin);
                ps.setInt(3, id);
                ps.execute();
                LOGGER.info(" urdu={}, latin={}",   supposedUrdu, supposedLatin);

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
