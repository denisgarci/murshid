package com.murshid.ingestor.pratts;

import com.murshid.ingestor.enums.Scripts;
import com.murshid.ingestor.utils.IngestorWordUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static com.murshid.ingestor.pratts.AnalysisBodyForHindiUrdu.replaceFirstCapitals;

public class AnalysisBody2ForUrduUrduHindiLatin {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisBody2ForUrduUrduHindiLatin.class);

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
                    "UPDATE pratts SET hindi_word=? , urdu = ?, latin = ?  where id = ?");

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

                if (tokens.length < 4){
                    LOGGER.info("id={} does not have enough tokens in the body", id);
                    continue;
                }

                if (StringUtils.isEmpty(tokens[1]) || StringUtils.isEmpty(tokens[2]) || StringUtils.isEmpty(tokens[3]) || StringUtils.isEmpty(tokens[4])){
                    LOGGER.info("id={} has tokens too short", id);
                    continue;
                }

                String supposedUrdu1 = null;
                if (IngestorWordUtils.scriptType(tokens[1]) != Scripts.NASTALIQ){
                    continue;
                }else{
                    supposedUrdu1 = tokens[1];
                }

                String supposedUrdu2 = null;
                if (IngestorWordUtils.scriptType(tokens[2]) != Scripts.NASTALIQ){
                    continue;
                }else{
                    supposedUrdu2 = tokens[2];
                }

                String supposedHindi1 = null;
                if (IngestorWordUtils.scriptType(tokens[3]) != Scripts.DEVANAGARI){
                    continue;
                }else{
                    supposedHindi1 = tokens[3];
                }

                String supposedHindi2 = null;
                if (IngestorWordUtils.scriptType(tokens[4]) != Scripts.DEVANAGARI){
                    continue;
                }else{
                    supposedHindi2 = tokens[4];
                }

                String supposedLatin = null;
                if (IngestorWordUtils.scriptType(tokens[5]) != Scripts.LATIN){
                    continue;
                }else{
                    supposedLatin = tokens[5];
                }


                if (supposedUrdu1== null ||  supposedUrdu2== null || supposedHindi1== null || supposedHindi2== null || supposedLatin == null){
                    continue;
                }

                if (!supposedLatin.contains("-")) continue;

                String urdu = supposedUrdu1.trim().concat(" ").concat(supposedUrdu2.trim());
                String hindi = supposedHindi1.trim().concat(" ").concat(supposedHindi2.trim());

                //LOGGER.info("body={}", body2);
                LOGGER.info("urdu={} hindi={} latin={}", urdu, hindi, supposedLatin );

                //if (true) continue;

                ps.setString(1, hindi);
                ps.setString(2, urdu);
                ps.setString(3, supposedLatin);
                ps.setInt(4, id);
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
