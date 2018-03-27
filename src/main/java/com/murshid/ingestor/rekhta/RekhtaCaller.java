package com.murshid.ingestor.rekhta;

import com.murshid.ingestor.utils.ClientBuilderUtil;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RekhtaCaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(RekhtaCaller.class);

    private static String REKHTA_URL = "https://www.rekhta.org/urdudictionary/?keyword=";


    public static void main(String[] args) throws Exception{
        //new PrattsCaller().invokeAll();
        RekhtaCaller caller = new RekhtaCaller();

        URL url = caller.createUrlWithParams("हैकल");

        Client client = ClientBuilderUtil.createClient();

        Invocation.Builder builder = client.target(url.toURI())
                .request(MediaType.TEXT_HTML_TYPE)
                .header("content-encoding", "gzip")
                .header("content-lenght", 25182);
        Response response = builder.get();


        String entity = response.readEntity(String.class);
        Document document = Jsoup.parse(entity);
        Optional<Element> rekhtaElementOpt = extracRekhtaMeaning(document);
        if (rekhtaElementOpt.isPresent()){
            List<RekhtaEntry> entries = extractEntry(rekhtaElementOpt.get());
            LOGGER.info("entries = ", entries);

        }
    }

    public Optional<List<RekhtaEntry>> fromStringEntry(String entry) throws Exception{
        RekhtaCaller caller = new RekhtaCaller();

        URL url = caller.createUrlWithParams(entry);

        Client client = ClientBuilderUtil.createClient();

        Invocation.Builder builder = client.target(url.toURI())
                .request(MediaType.TEXT_HTML_TYPE)
                .header("content-encoding", "gzip")
                .header("content-lenght", 25182);
        Response response = builder.get();


        String entity = response.readEntity(String.class);
        Document document = Jsoup.parse(entity);
        Optional<Element> rekhtaElementOpt = extracRekhtaMeaning(document);
        if (rekhtaElementOpt.isPresent()){
            List<RekhtaEntry> entries = extractEntry(rekhtaElementOpt.get());
            return Optional.of(entries);
        }
        return Optional.empty();
    }


    private static List<RekhtaEntry> extractEntry(Element rekhtaElement){
        Element ul = rekhtaElement.selectFirst("ul");
        Elements listEntries = ul.select("li");
        List<RekhtaEntry> entries = new ArrayList<>();

        for (int i=0; i< entries.size(); i++){
            Element le = listEntries.get(i);
            RekhtaEntry re = new RekhtaEntry();
            Element left = le.selectFirst("div.dict_card_left");
            Element latin = left.selectFirst("h4");
            Element hindi =  left.selectFirst("h5");
            Element urdu =  left.selectFirst("p.meaningUrduText");
            re.latin = latin.wholeText();
            re.hindiWord = hindi.wholeText();
            re.wordIndex = i;
            re.urdu = urdu.wholeText();
            Element right = le.selectFirst("div.dict_card_right");
            re.meaning = right.selectFirst("h4").wholeText();
            entries.add(re);
        }
        return entries;
    }



    private static Optional<Element> extracRekhtaMeaning(Document document){
        Elements elements = document.select("div.wordMeanings");
        if (elements.isEmpty()){
            return Optional.empty();
        }else{
            Element element = elements.first();
            while (element != null){
                Element searchHeading = element.selectFirst("div.search_heading");
                if (searchHeading == null){
                    return Optional.empty();
                }else{
                    if (searchHeading.wholeText().contains("REKHTA DICTIONARY")){
                        return Optional.of(element);
                    }
                }
                element = element.nextElementSibling();
            }
        }
        return Optional.empty();
    }



    public URL createUrlWithParams(String keyword) {

        try {
            URIBuilder builder = new URIBuilder(REKHTA_URL);
            String relativePath = "/urdudictionary";
            builder.setPath(relativePath);
            builder.setParameter("keyword", keyword);
            return builder.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            String msg = String.format("Error creating URL for Rekhta search parameter %s . The exception is %s ", keyword, e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }
}
