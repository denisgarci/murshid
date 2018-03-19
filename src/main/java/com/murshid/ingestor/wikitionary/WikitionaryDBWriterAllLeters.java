package com.murshid.ingestor.wikitionary;

import com.murshid.ingestor.rekhta.RekhtaCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WikitionaryDBWriterAllLeters {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryDBWriterAllLeters.class);



    public static void main(String[] args) throws Exception {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String dbUrl = "jdbc:mysql://localhost:3306/pratts";
        String user = "root";
        String password = "";

        RekhtaCaller caller = new RekhtaCaller();



        try {

            con = DriverManager.getConnection(dbUrl, user, password);
            con.setAutoCommit(false);

            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT distinct initial from hindi_words ");

            int index = 0;
            ExecutorService pool = Executors.newFixedThreadPool(36);
            while(rs.next()) {
                String initial = rs.getString(1);
                pool.submit(new WikitionaryDBWriterByLetter(initial));
            }
            pool.shutdown();
        } catch (SQLException ex) {
            //LOGGER.info("bodysize = " + bodysize);
            ex.printStackTrace();

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
