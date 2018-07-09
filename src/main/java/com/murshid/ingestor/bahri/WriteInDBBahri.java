package com.murshid.ingestor.bahri;

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

public class WriteInDBBahri {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteInDBBahri.class);

    private static ImmutableMap<Integer, Integer> boundaries = new ImmutableMap.Builder<Integer, Integer>()
            .put(0, 31177).build();

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
            con.setAutoCommit(false);


            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO bahri (section, entry, hindi_word, word_index, latin_word, part_of_speech, " +
                            "meaning, accidence, extra_meaning) VALUES (?, ?, ?, ?, ?, ? , ?, ?, ?)");


            int index = 0;
            //for (int section : Integer.s boundaries.keySet()) {
            for (int section : IntStream.range(0, 1).toArray()) {
                logSection = section;
                int size = boundaries.get(section);
                int start = 0;
                if (section== 0) start = 27297;
                for (entry = start; entry <= size; entry++) {
                    index++;
                    URL url = caller.createUrlWithParams(section, entry);
                    Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
                    Response response = builder.post(null);

                    String entity = response.readEntity(String.class);
                    document = Jsoup.parse(entity);
                    Element element = document.select("html > body > hw").first();
                    if (element == null) continue;
                    Element firstHindiEntryElement = element.selectFirst("span.head");
                    String firstHindiEntry = firstHindiEntryElement.wholeText().trim();
                    Element latinWOrdElement = element.selectFirst("tn");
                    String latinWOrdString = null;
                    if (latinWOrdElement !=  null) latinWOrdString = latinWOrdElement.wholeText();
                    Element accidenceElement = element.nextElementSibling();
                    String accidenceElementString = null;
                    if (accidenceElement.is("i") ) {
                        accidenceElementString = accidenceElement.wholeText();
                    }else{
                        accidenceElement = null;
                    }

                    Element lastElement = accidenceElement != null? accidenceElement : latinWOrdElement;
                    if (lastElement == null) lastElement = firstHindiEntryElement;

                    String textSinceHw= extractTextSinceElement(element);
                    String textSinceLast = extractTextSinceElement(lastElement);

                    String meaning = extractTails(textSinceLast);
                    String extraMeaning = extractTails(textSinceHw);

                    LOGGER.info("section={} entry={} hindiWOrd={} latinWord={} accidence={} meaning={}", section, entry, firstHindiEntry, latinWOrdString, accidenceElementString, meaning);

                    ps.setInt(1, section);
                    ps.setInt(2, entry);
                    ps.setString(3, firstHindiEntry);
                    ps.setString(4, null);
                    ps.setString(5, latinWOrdString);
                    ps.setString(6, null);
                    ps.setString(7, meaning);
                    ps.setString(8, accidenceElementString);
                    ps.setString(9, extraMeaning);

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

    private static String extractTails(String meaning){
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
        return meaning;
    }

    private static String extractTextSinceElement(Element element){
        Node sibling = element.nextSibling();
        StringBuilder sb = new StringBuilder();
        while(sibling != null){
            if (sibling instanceof Element) {
                Element nextElement = (Element) sibling;
                if (nextElement.is("table")) break;
                if(nextElement.is("p")) break;
                sb.append(nextElement.text());
            }else if (sibling instanceof TextNode){
                TextNode nextTextNode = (TextNode) sibling;
                //if (nextTextNode.text().equals(" ~ ")) break;
                sb.append(nextTextNode.text());
            }
            sibling = sibling.nextSibling();
        }

        return sb.toString();

    }




}
