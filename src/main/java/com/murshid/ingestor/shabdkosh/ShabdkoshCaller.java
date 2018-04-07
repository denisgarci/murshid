package com.murshid.ingestor.shabdkosh;

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

public class ShabdkoshCaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShabdkoshCaller.class);

    private static String URL = "http://www.shabdkosh.com";

    public static void main(String[] args) throws Exception{
        //new PrattsCaller().invokeAll();
        ShabdkoshCaller caller = new ShabdkoshCaller();

        //String hindiWord = "हैकल";
        String word = "हैट";

        URL url = caller.createUrlWithParams(word);

        Client client = ClientBuilderUtil.createClient();

        Invocation.Builder builder = client.target(url.toURI())
                .request(MediaType.TEXT_HTML_TYPE);
//                .header("content-encoding", "gzip")
//                .header("content-lenght", 25182);
        Response response = builder.get();


        String entity = response.readEntity(String.class);
        Document document = Jsoup.parse(entity);
        SKEntry skEntry = extracSKMeaning(word, document);
        LOGGER.info("SKEntry for {} is: {}", word,  skEntry);
    }

//    public Optional<List<RekhtaWebEntry>> fromStringEntry(String entry) throws Exception{
//        ShabdkoshCaller caller = new ShabdkoshCaller();
//
//        URL url = caller.createUrlForWord(entry);
//
//        Client client = ClientBuilderUtil.createClient();
//
//        Invocation.Builder builder = client.target(url.toURI())
//                .request(MediaType.TEXT_HTML_TYPE)
//                .header("content-encoding", "gzip")
//                .header("content-lenght", 25182);
//        Response response = builder.get();
//
//
//        String entity = response.readEntity(String.class);
//        Document document = Jsoup.parse(entity);
//        Optional<Element> rekhtaElementOpt = extracSKMeaning(document);
//        if (rekhtaElementOpt.isPresent()){
//            List<RekhtaWebEntry> entries = extractEntry(rekhtaElementOpt.get());
//            return Optional.of(entries);
//        }
//        return Optional.empty();
//    }


//    private static List<RekhtaWebEntry> extractEntry(Element rekhtaElement){
//        Element ul = rekhtaElement.selectFirst("ul");
//        Elements listEntries = ul.select("li");
//        List<RekhtaWebEntry> entries = new ArrayList<>();
//        listEntries.forEach(le -> {
//            RekhtaWebEntry re = new RekhtaWebEntry();
//            Element left = le.selectFirst("div.dict_card_left");
//            Element latin = left.selectFirst("h4");
//            Element hindiWord =  left.selectFirst("h5");
//            Element urdu =  left.selectFirst("p.meaningUrduText");
//            re.latin = latin.wholeText();
//            re.hindiWord = hindiWord.wholeText();
//            re.urdu = urdu.wholeText();
//            Element right = le.selectFirst("div.dict_card_right");
//            re.meaning = right.selectFirst("h4").wholeText();
//            entries.add(re);
//        });
//        return entries;
//    }



    private static SKEntry extracSKMeaning(String word, Document document){

        SKEntry skEntry = new SKEntry();
        skEntry.word = word;

        Element left = document.getElementById("content").getElementById("left");
        //Element row = content.selectFirst("row");

        for(PartOfSpeech pos: PartOfSpeech.values()){

            Element element = left.selectFirst("h3." + pos.toString());
            if (element == null) continue;

            SKSection skSection = new SKSection();
            skSection.partOfSpeech = pos;


            Element orderedList = element.nextElementSibling().selectFirst("ol.eirol");
            Elements items = orderedList.select("li");
            items.forEach(li ->{
                SKItem skItem = new SKItem();
                Element link = li.selectFirst("a");
                skItem.meaning = link.wholeText();
                skItem.accidence = li.ownText();
                skSection.items.add(skItem);
            });
            skEntry.sections.add(skSection);

        }

        return skEntry;
    }



    public URL createUrlWithParams(String word) {

        try {
            URIBuilder builder = new URIBuilder(URL);
            String relativePath = "/hi/translate";
            builder.setPath(relativePath);
            builder.setParameter("e", word);
            builder.setParameter("l", "hi");
            return builder.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            String msg = String.format("Error creating URL for Shabdkosh search parameter %s . The exception is %s ", word, e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }
}
