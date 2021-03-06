package com.murshid.ingestor.chaturvedi;

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

public class ComplementDictionaryEntries {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComplementDictionaryEntries.class);


    public static void main(String[] args) throws Exception {

        Connection con = null;
        ResultSet rs = null;

        String dbUrl = "jdbc:mysql://localhost:3306/murshid";
        String user = "root";
        String password = "";

        try {

            con = DriverManager.getConnection(dbUrl, user, password);
            con.setAutoCommit(false);


            PreparedStatement bigSelect = con.prepareStatement("select id, hindi_word, part_of_speech from master_dictionary");

            PreparedStatement cvSelect = con.prepareStatement("select * from caturvedi where hindi_word = ? and part_of_speech=? ");

            PreparedStatement ps = con.prepareStatement(
                    "INSERT  into dictionary_entries (word_index, dictionary_source, master_dictionary_id)   values (?, ?, ?)");


            rs = bigSelect.executeQuery();

            int index = 0;
            while(rs.next()){
                String hindiWord = rs.getString("hindi_word");
                String partOfSpeech = rs.getString("part_of_speech");
                int id = rs.getInt("id");

                cvSelect.setString(1, hindiWord);
                cvSelect.setString(2, partOfSpeech);
                ResultSet cvRs = cvSelect.executeQuery();
                if (cvRs.next()){
                    ps.setInt(1, 0);
                    ps.setString(2, "CATURVEDI");
                    ps.setInt(3, id);
                    ps.execute();
                }
                cvRs.close();
                con.commit();
            }

        } catch (Exception ex) {
            LOGGER.error(" error in document for section={} entry={} **********",  ex);
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
