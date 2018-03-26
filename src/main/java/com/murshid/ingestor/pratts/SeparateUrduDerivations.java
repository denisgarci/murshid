package com.murshid.ingestor.pratts;

import com.murshid.ingestor.utils.IngestorWordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SeparateUrduDerivations {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeparateUrduDerivations.class);

    public static void main(String[] args) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://localhost:3306/pratts";
        String user = "root";
        String password = "";

        String[] intentos = {
//                "causal of",
//                "v. n. of",
//                "pl. of",
//                "see",
//                "caus of",
//                "incorr of",
//                "corr. of",
//                "more com",
//                "for A",
//                "act. part. of",
                "perf. part. of",
//                "pass. part. of",
//                "causal of",
//                "part. n. of",
//                "v.n. fr",
//                "inf n. of"
        };

        try {

            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);


            PreparedStatement ps = con.prepareStatement(
                    "UPDATE entries SET split_head = ?  where id = ?");

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT head, id from entries where hindi  is null and urdu is null and latin is null and id = 2182");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String head = rs.getString("head");

                if (!IngestorWordUtils.lastWordIsUrdu(head)) continue;

                String lastUrdu = IngestorWordUtils.getLastWord(head);

                for(String intento: intentos){
                    String intentoConUrdu = intento.concat(" ").concat(lastUrdu);
                    if (IngestorWordUtils.endsWithUrduSafe(head, intentoConUrdu)){
                        String headSansIntento = head.substring(0, head.length() - intentoConUrdu.length());
                        LOGGER.info("original={}", head);
                        LOGGER.info("sin intento={}", headSansIntento);
                    }
                }


//                String splitHead = tokens[0];
//
//                ps.setString(1, splitHead);
//                ps.setInt(2, id);
//                ps.execute();
//                i++;
//
//                if (i % 100 == 0) {
//                    System.out.println("updated " + i + " records");
//                    con.commit();
//                }

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
