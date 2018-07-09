package com.murshid.ingestor.bahri;

import com.murshid.ingestor.utils.ClientBuilderUtil;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import java.sql.*;

public class RemoveDuplicates {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveDuplicates.class);


    public static void main(String[] args) throws Exception {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String dbUrl = "jdbc:mysql://localhost:3306/murshid";
        String user = "root";
        String password = "";

        BahriCaller caller = new BahriCaller();


        Client client = ClientBuilderUtil.createClient();

        Document document = null;
        int logSection =0, entry = 0;
        try {

            con = DriverManager.getConnection(dbUrl, user, password);
            //con.setAutoCommit(false);

            PreparedStatement bigSelect = con.prepareStatement("select count(*), meaning , hindi_word " +
                    " from bahri " +
                    " group by hindi_word, meaning having count(*) > 1 order by hindi_word");

            PreparedStatement smallSelect = con.prepareStatement("select id from bahri where hindi_word = ? and meaning = ? ");


            PreparedStatement delete = con.prepareStatement("DELETE FROM bahri where id = ?");


            rs = bigSelect.executeQuery();



            while(rs.next()){
                String hindiWord = rs.getString("hindi_word");
                String meaning = rs.getString("meaning");

                smallSelect.setString(1, hindiWord);
                smallSelect.setString(2, meaning);
                ResultSet range = smallSelect.executeQuery();

                range.next();
                LOGGER.info("skipping id = {}", range.getInt("id"));
                while(range.next()){
                    delete.setInt(1, range.getInt("id"));
                    LOGGER.info("deleting id = {}", range.getInt("id"));
                    delete.execute();

                }

                range.close();
            }

        } catch (Exception ex) {
            LOGGER.error(" error in document for section={} entry={} **********", logSection, entry, ex);
            LOGGER.error(document.toString());
        } finally {
            if (con != null) {
                try {
                    //con.commit();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
