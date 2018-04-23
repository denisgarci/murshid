package com.murshid.ingestor.platts;

import com.murshid.ingestor.enums.Scripts;
import com.murshid.ingestor.utils.IngestorWordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static com.murshid.ingestor.platts.AnalysisBodyForHindiUrdu.replaceFirstCapitals;

public class FirstLatinToLatin {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstLatinToLatin.class);

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
                    "UPDATE pratts SET latin=? where id = ?");

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT body, id from pratts where latin  is null ");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String body = rs.getString("body");

                String body2 = replaceFirstCapitals(body);
                if (!body.equals(body2)){
                    body = body2;
                }

                body2 = body2.substring(2);

                String tokens[] = body2.split(" ");

                String latin = firstLatin(tokens);

                ps.setString(1, latin);
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

    private static String firstLatin(String[] words ){
        for (String string: words){
            if (IngestorWordUtils.scriptType(string) == Scripts.LATIN){
                return string;
            }
        }
        return null;
    }

}
