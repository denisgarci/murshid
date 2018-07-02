package com.murshid.ingestor.chaturvedi;

import com.google.common.collect.ImmutableMap;
import com.murshid.ingestor.platts.PlattsCaller;
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

public class WriteInDBChaturvedi {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteInDBChaturvedi.class);

    private static ImmutableMap<Integer, Integer> boundaries = new ImmutableMap.Builder<Integer, Integer>()
            .put(0, 22161)
            .put(1, 20367)
            .put(2, 10222).build();

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
            con.setAutoCommit(false);


            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO chaturvedi (section, entry, hindi_word, word_index, latin_word, part_of_speech, " +
                            "meaning, accidence, extra_meaning) VALUES (?, ?, ?, ?, ?, ? , ?, ?, ?)");


            int index = 0;
            //for (int section : Integer.s boundaries.keySet()) {
            for (int section : IntStream.range(2, 3).toArray()) {
                logSection = section;
                int size = boundaries.get(section);
                int start = 0;
                if (section== 2) start = 4091;
                for (entry = start; entry <= size; entry++) {
                    index++;
                    URL url = caller.createUrlWithParams(section, entry);
                    Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
                    Response response = builder.post(null);

                    String entity = response.readEntity(String.class);
                    document = Jsoup.parse(entity);
                    Element element = document.select("html > body > div2>  p:first-of-type").first();
                    if (element == null) continue;
                    Element firstHindiEntryElement = element.selectFirst("d");
                    String firstHindiEntry = firstHindiEntryElement.wholeText().trim();
                    Element latinWOrdElement = element.selectFirst("tn");
                    String latinWOrdString = null;
                    if (latinWOrdElement !=  null) latinWOrdString = latinWOrdElement.wholeText();
                    Element accidenceElement = element.selectFirst("i");
                    String accidenceElementString = null;
                    if (accidenceElement != null) {
                        accidenceElementString = accidenceElement.wholeText();
                    }

                    Element lastElement = accidenceElement != null? accidenceElement : latinWOrdElement;
                    if (lastElement == null) lastElement = firstHindiEntryElement;

                    Node text = lastElement.nextSibling();
                    StringBuilder sb = new StringBuilder();
                    while(text instanceof TextNode){
                        sb.append(((TextNode) text).text());
                        text = text.nextSibling();
                    }
                    String meaning = sb.toString();
                    if (!meaning.isEmpty()) {
                        meaning = meaning.substring(1).trim();
                        if (meaning.endsWith(";—")){
                            meaning = meaning.substring(0, meaning.length()-2);
                        }
                        if (meaning.endsWith("; ~")){
                            meaning = meaning.substring(0, meaning.length()-3);
                        }
                        if (meaning.endsWith("; (")){
                            meaning = meaning.substring(0, meaning.length()-3);
                        }
                        if (meaning.endsWith(":")){
                            meaning = meaning.substring(0, meaning.length()-1);
                        }
                        if (meaning.endsWith("; —")){
                            meaning = meaning.substring(0, meaning.length()-3);
                        }
                        if (meaning.endsWith(", —")){
                            meaning = meaning.substring(0, meaning.length()-3);
                        }
                    }

                    LOGGER.info("section={} entry={} hindiWOrd={} latinWord={} accidence={} meaning={}", section, entry, firstHindiEntry, latinWOrdString, accidenceElementString, meaning);

                    ps.setInt(1, section);
                    ps.setInt(2, entry);
                    ps.setString(3, firstHindiEntry);
                    ps.setString(4, null);
                    ps.setString(5, latinWOrdString);
                    ps.setString(6, null);
                    ps.setString(7, meaning);
                    ps.setString(8, accidenceElementString);
                    ps.setString(9, element.ownText());

                    ps.execute();

                    if (index % 50 == 0) {
                        con.commit();
                        LOGGER.info("section {} entry {} of {}", section, entry, size);
                    }


                }
                con.commit();
            }

        } catch (Exception ex) {
            LOGGER.error(" error in document for section={} entry={} **********", logSection, entry, ex);
            LOGGER.error(document.toString());
        } finally {
            if (con != null) {
                try {
                    con.commit();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
