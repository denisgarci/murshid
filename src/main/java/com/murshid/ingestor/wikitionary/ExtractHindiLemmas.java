package com.murshid.ingestor.wikitionary;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

/**
 * Extracts Hindi headwords with IPA pronunciation, found in a wikitionary page
 * https://en.wiktionary.org/w/index.php?title=Category:Hindi_terms_with_IPA_pronunciation&from=%E0%A4%85
 *
 * and subsequent pages given by the "next page" link, until there are no more.
 *
 * Inserts those words in hindi_words, if they are not already there
 */
public class ExtractHindiLemmas {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractHindiLemmas.class);


    public static void main(String[] args) throws Exception{



        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://localhost:3306/pratts";
        String user = "root";
        String password = "";

        try {

            con = DriverManager.getConnection(url, user, password);


            PreparedStatement select = con.prepareStatement("SELECT word from hindi_words where word = ? ");

            PreparedStatement insert = con.prepareStatement(
                    "INSERT INTO hindi_words (word, active, initial) values  (?, ?, ?) ");


            WikitionaryCaller caller = new WikitionaryCaller();
            Document letterPage = WikitionaryCaller.documentOfHindiLemmas(caller);

            while(letterPage != null) {
                Elements catgroups = letterPage
                        .getElementById("mw-pages")
                        .select("div.mw-content-ltr")
                        .select("div.mw-category-group");

                catgroups.forEach(cg -> {
                    Elements lis = cg.selectFirst("ul").select("li");
                    lis.forEach(li -> {
                        String word = li.selectFirst("a").wholeText();
                        LOGGER.info("hindiWord =" + word);
                        insertIfNotPresent(word, select, insert);
                    });
                });

                Optional<Element> nextPage = getNextPage(letterPage);


                if (nextPage.isPresent()) {
                    letterPage = WikitionaryCaller.documentForUrl(caller, "https://en.wiktionary.org" +
                                                                                 nextPage.get().attr("href"));

                }else {
                    letterPage = null;
                }
            }



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

    private static void insertIfNotPresent(String word, PreparedStatement select, PreparedStatement insert) {
        try {
            select.setString(1, word);
            ResultSet rs = select.executeQuery();
            if (!rs.next()) {
                insert.setString(1, word);
                insert.setInt(2, 3);
                insert.setString(3, word.substring(0, 1));
                insert.execute();
                LOGGER.info("inserted hindiWord={}", word);
            }
        }catch (SQLException ex){
            LOGGER.error("error in insertIfNotPresent", ex);
        }
    }

    static Optional<Element> getNextPage(Document document){
        Element nextPage = document
                .getElementById("mw-pages").select("[title=Category:Hindi lemmas]")
                .select(":matchesOwn(next page)").first();

        return Optional.ofNullable(nextPage);
    }


}
