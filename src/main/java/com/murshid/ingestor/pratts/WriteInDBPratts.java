package com.murshid.ingestor.pratts;

import com.google.common.collect.ImmutableMap;
import com.murshid.ingestor.utils.ClientBuilderUtil;
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
import java.util.stream.IntStream;

public class WriteInDBPratts {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteInDBPratts.class);

    private static ImmutableMap<Integer, Integer> boundaries = new ImmutableMap.Builder<Integer, Integer>()
            .put(0, 15765)
            .put(1, 15386)
            .put(2, 15419)
            .put(3, 14394)
            .put(4, 13627)
            .put(5, 14637)
            .put(6, 13935)
            .put(7, 13572)
            .put(8, 12397)
            .put(9, 11582).build();

    public static void main(String[] args) throws Exception {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String dbUrl = "jdbc:mysql://localhost:3306/pratts";
        String user = "root";
        String password = "";

        PrattsCaller prattsCaller = new PrattsCaller();


        Client client = ClientBuilderUtil.createClient();

        int bodysize = 0;
        try {

            con = DriverManager.getConnection(dbUrl, user, password);
            con.setAutoCommit(false);


            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO entries (keystring, head, section, entry, body) VALUES (?, ?, ?, ?, ?)");


            int index = 0;
            //for (int section : Integer.s boundaries.keySet()) {
            for (int section : IntStream.range(4, 6).toArray()) {
                int size = boundaries.get(section);
                int start = 0;
                for (int entry = start; entry <= size; entry++) {
                    index++;
                    URL url = prattsCaller.createUrlWithParams(section, entry);
                    Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
                    Response response = builder.post(null);

                    String entity = response.readEntity(String.class);
                    Document document = Jsoup.parse(entity);
                    Element element = document.selectFirst("div2");
                    if (element == null) continue;
                    String keystring = element.attributes().get("id").toString();
                    String head = element.selectFirst("span").selectFirst("span").wholeText();
                    Element meaning = element.selectFirst("p");
                    if (meaning == null) continue;
                    String body = meaning.wholeText();

                    bodysize = body.length();
//                    LOGGER.info("key =" + id);
//                    LOGGER.info("head =" + head);
//                    LOGGER.info("body =" + body);
//                    LOGGER.info("section {} index {} entry {}", 0, i, element.toString());
//                    LOGGER.info("********************");


                    ps.setString(1, keystring);
                    ps.setString(2, head);
                    ps.setInt(3, section);
                    ps.setInt(4, entry);
                    ps.setString(5, body);


                    ps.execute();

                    if (index % 50 == 0) {
                        con.commit();
                        LOGGER.info("section {} entry {} of {}", section, entry, size);
                    }


                }
                con.commit();
            }

        } catch (SQLException ex) {
            LOGGER.info("bodysize = " + bodysize);
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
