package com.murshid.ingestor.chaturvedi;

import com.google.common.collect.ImmutableMap;
import com.murshid.ingestor.utils.ClientBuilderUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.sql.*;
import java.util.stream.IntStream;

public class RemoveDuplicates {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveDuplicates.class);


    public static void main(String[] args) throws Exception {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String dbUrl = "jdbc:mysql://localhost:3306/murshid";
        String user = "root";
        String password = "";

        ChaturvediCaller caller = new ChaturvediCaller();


        Client client = ClientBuilderUtil.createClient();

        Document document = null;
        int logSection =0, entry = 0;
        try {

            con = DriverManager.getConnection(dbUrl, user, password);
            //con.setAutoCommit(false);

            PreparedStatement bigSelect = con.prepareStatement("select count(*), meaning , hindi_word " +
                    " from chaturvedi " +
                    " group by hindi_word, meaning having count(*) > 1 order by hindi_word");

            PreparedStatement smallSelect = con.prepareStatement("select id from chaturvedi where hindi_word = ? and meaning = ? ");

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO chaturvedi (section, entry, hindi_word, word_index, latin_word, part_of_speech, " +
                            "meaning, accidence, extra_meaning) VALUES (?, ?, ?, ?, ?, ? , ?, ?, ?)");


            PreparedStatement delete = con.prepareStatement("DELETE FROM chaturvedi where id = ?");


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
