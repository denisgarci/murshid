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

public class OverwriteWholeText {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverwriteWholeText.class);


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


            PreparedStatement bigSelect = con.prepareStatement("select entry, section from caturvedi where extra_meaning is null");

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE caturvedi  SET extra_meaning = ? where entry = ? and section = ?");


            rs = bigSelect.executeQuery();

            int index = 0;
            while(rs.next()){
                entry = rs.getInt("entry");
                int section = rs.getInt("section");
                    index++;

                    URL url = caller.createUrlWithParams(section, entry);
                    Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
                    Response response = builder.post(null);

                    String entity = response.readEntity(String.class);
                    document = Jsoup.parse(entity);
                    Element element = document.select("html > body > div2>  p:first-of-type").first();
                    if (element == null) continue;



                    String extraMeaning = element.wholeText();
                    if (!StringUtils.isEmpty(extraMeaning)) extraMeaning = extraMeaning.substring(0, Math.min(extraMeaning.length(), 8000));
                    ps.setString(1, extraMeaning);
                    ps.setInt(2, entry);
                    ps.setInt(3, section);

                    ps.execute();

                    if (index % 50 == 0) {
                        //con.commit();
                        LOGGER.info("section {} entry {} extraMeaning {}", section, entry, extraMeaning);
                    }


                //con.commit();
            }

        } catch (Exception ex) {
            LOGGER.error(" error in document for section={} entry={} **********", logSection, entry, ex);
            LOGGER.error(document.toString());
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
