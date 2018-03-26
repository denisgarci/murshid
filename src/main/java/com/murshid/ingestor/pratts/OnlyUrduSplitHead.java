package com.murshid.ingestor.pratts;

import com.google.common.base.Strings;
import com.murshid.ingestor.enums.Scripts;
import com.murshid.ingestor.utils.IngestorWordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class OnlyUrduSplitHead {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnlyUrduSplitHead.class);

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
                    "UPDATE entries SET urdu = ?  where id = ?");

            Statement select = con.createStatement();
            rs = select.executeQuery(
                    "SELECT split_head, id from entries where hindi  is null and urdu is null and latin is null and split_head is not null");

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String head = rs.getString("split_head");

                String tokens[] = head.split(" ");
                int numUrdus = 0;
                String urdu = "";
                for (String token : tokens) {
                    if (!Strings.isNullOrEmpty(token) && IngestorWordUtils.scriptType(token) == Scripts.NASTALIQ) {
                        numUrdus++;
                        urdu = token;
                    }
                }
                if (numUrdus != 1) { continue; }

                LOGGER.info("head={}, urdu={}", head, urdu);
                ps.setString(1, urdu);
                ps.setInt(2, id);
                ps.execute();


                i++;

                if (i % 100 == 0) {
                    System.out.println("updated " + i + " records");
                    con.commit();
                }

            }
            System.out.println("finished, total =" + i);
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
