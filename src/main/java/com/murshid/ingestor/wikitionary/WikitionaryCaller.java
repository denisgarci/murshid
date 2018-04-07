package com.murshid.ingestor.wikitionary;

import com.murshid.ingestor.utils.ClientBuilderUtil;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class WikitionaryCaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryCaller.class);
    private static final Logger FILE_LOGGER = LoggerFactory.getLogger(WikitionaryCaller.class);

    private static String URL = "https://en.wiktionary.org";

    public static void main(String[] args) throws Exception{
        //new PrattsCaller().invokeAll();
        WikitionaryCaller caller = new WikitionaryCaller();

        //String hindiWord = "हैकल";
        String word = "भेदभाव";
        //String hindiWord = "को";

        URL url = caller.createUrlForWord(word);

        Client client = ClientBuilderUtil.createClient();

        Invocation.Builder builder = client.target(url.toURI())
                .request(MediaType.TEXT_HTML_TYPE);
//                .header("content-encoding", "gzip")
//                .header("content-lenght", 25182);
        Response response = builder.get();


        String entity = response.readEntity(String.class);
        Document document = Jsoup.parse(entity);
        //LOGGER.info("Wikitionary for {} is: {}", hindiWord,  skEntry);
    }

    public static Document documentForWord(WikitionaryCaller caller, String word) {
        URL url = null;
        try {
            url = caller.createUrlForWord(word);

            Client client = ClientBuilderUtil.createClient();

            Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
            Response response = builder.get();


            String entity = response.readEntity(String.class);
            return Jsoup.parse(entity);
        }catch (URISyntaxException ex){
            FILE_LOGGER.error("error calling URL={} for wor={} ", url, word, ex);
            return null;
        }

    }

    public static Document documentForUrl(WikitionaryCaller caller, String urlString) throws Exception{
        URL url = new URL(urlString);

        Client client = ClientBuilderUtil.createClient();

        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
        Response response = builder.get();


        String entity = response.readEntity(String.class);
        Document document = Jsoup.parse(entity);

        return document;

    }

    public static Document documentIPAListForWord(WikitionaryCaller caller, String letter) throws Exception{
        URL url = caller.createUrlForIPAWordList(letter);

        Client client = ClientBuilderUtil.createClient();

        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
        Response response = builder.get();


        String entity = response.readEntity(String.class);
        Document document = Jsoup.parse(entity);

        return document;

    }

    public static Document documentOfHindiLemmas(WikitionaryCaller caller) throws Exception{

        URL url = null;
        try {
            URIBuilder builder = new URIBuilder(URL);
            String relativePath = "/wiki/Category:Hindi_lemmas";
            builder.setPath(relativePath);
            url = builder.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            String msg = String.format("Error creating URL for Wikitionary Hindi Lemmas", e.getMessage());
            throw new RuntimeException(msg, e);
        }

        Client client = ClientBuilderUtil.createClient();

        Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
        Response response = builder.get();


        String entity = response.readEntity(String.class);
        Document document = Jsoup.parse(entity);

        return document;
    }









    public URL createUrlForIPAWordList(String letter) {
        try {

            //https://en.wiktionary.org/w/index.php?title=Category:Hindi_terms_with_IPA_pronunciation&from=%E0%A4%85
            URIBuilder builder = new URIBuilder(URL);
            String relativePath = "/w/index.php";
            builder.setParameter("title", "Category:Hindi_terms_with_IPA_pronunciation");
            builder.setParameter("from", "letter");
            return builder.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            String msg = String.format("Error creating URL for Wikitionary IPA list search parameter %s . The exception is %s ", letter, e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }


    public URL createUrlForWord(String word) {

        try {
            URIBuilder builder = new URIBuilder(URL);
            String relativePath = "/wiki/" + word;
            builder.setPath(relativePath);
            return builder.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            String msg = String.format("Error creating URL for Wikitionary hindiWord search parameter %s . The exception is %s ", word, e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }
}
