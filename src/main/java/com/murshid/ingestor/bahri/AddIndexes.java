package com.murshid.ingestor.bahri;

import com.murshid.ingestor.utils.ClientBuilderUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.sql.*;

public class AddIndexes {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddIndexes.class);


    public static void main(String[] args) throws Exception {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String dbUrl = "jdbc:mysql://localhost:3306/murshid";
        String user = "root";
        String password = "";

        try {

            con = DriverManager.getConnection(dbUrl, user, password);
            //con.setAutoCommit(false);


            PreparedStatement bigSelect = con.prepareStatement("select hindi_word, count(*) from bahri where word_index is null " +
                    " group by hindi_word having count(*) > 1");

            PreparedStatement smallSelect = con.prepareStatement("select id from bahri where hindi_word = ?");

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE bahri  SET word_index = ? where id = ?");


            rs = bigSelect.executeQuery();

            int index = 0;
            while(rs.next()){
                String hinduiWord = rs.getNString("hindi_word");

                smallSelect.setString(1, hinduiWord);
                ResultSet rsWord = smallSelect.executeQuery();
                int subInxex = 0;
                while(rsWord.next()){
                    int id = rsWord.getInt("id");
                    ps.setInt(1, subInxex);
                    ps.setInt(2, id);
                    ps.execute();
                    subInxex ++;
                }
                rsWord.close();
            }

        } catch (Exception ex) {
            LOGGER.error(" error ",  ex);
        } finally {
            if (con != null) {
                try {
            //        con.commit();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
