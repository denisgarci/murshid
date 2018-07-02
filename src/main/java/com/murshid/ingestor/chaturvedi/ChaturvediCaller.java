package com.murshid.ingestor.chaturvedi;

import com.google.common.collect.ImmutableMap;
import com.murshid.ingestor.utils.ClientBuilderUtil;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ChaturvediCaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaturvediCaller.class);

    private static String PLATTS_URL = "http://dsalsrv02.uchicago.edu";
    //?c.8:1:226.platts"
//    private static ImmutableMap<Integer, Integer> boundaries= new ImmutableMap.Builder<Integer, Integer>()
//            .put(0, 15765)
//            .put(1, 15386)
//            .put(2, 15419)
//            .put(3, 14394)
//            .put(4, 13627)
//            .put(5, 14637)
//            .put(6, 13935)
//            .put(7, 13572)
//            .put(8, 12397)
//            .put(9, 11582).build();
    static Map<Integer, Integer> boundaries = new HashMap<>();


    public static void findBoundariesForAll() {
        //new PlattsCaller().invokeAll();
        ChaturvediCaller plattsCaller = new ChaturvediCaller();
        for (int section=0; section<=9; section++){
            int noHasBoundary = plattsCaller.findNotHasBoundary(section);
            int lastEntry =  plattsCaller.findEnd(section, 1, noHasBoundary);
            boundaries.put(section, lastEntry);
            LOGGER.info("las entry for section " + section +  " is " + lastEntry);
        }
    }


    public static void main(String[] args) throws Exception{
        //new PlattsCaller().invokeAll();
        ChaturvediCaller caller = new ChaturvediCaller();
        for (int i =0; i < 20; i++ ) {

            URL url = caller.createUrlWithParams(1, 100 + i);

            Client client = ClientBuilderUtil.createClient();

            Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
            Response response = builder.post(null);

            String entity = response.readEntity(String.class);
            Document document = Jsoup.parse(entity);
            Element element = document.selectFirst("div2");
            LOGGER.info(element.toString());
        }
    }

    public void invokeAll() throws Exception{
        for (int i=0; i<=9; i++){

            LOGGER.info("***************** sending section number " + i);
            Element element = null;
            int index = 1;
            do {



                //entries are 1-based
                URL url = createUrlWithParams(i, index);

                Client client = ClientBuilderUtil.createClient();

                Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
                Response response = builder.post(null);


                String entity = response.readEntity(String.class);
                Document document = Jsoup.parse(entity);
                element = document.selectFirst("div2");
                if (element != null){
                    LOGGER.info("       element # " + index + " of section " + i);
                    index = index + 2000;
                }
            }while (element != null);

        }
    }


    /**
     * determine if the dictionary has an entry for that section and entry
     */
    private boolean hasInfo(int section, int entry){
        try {
            URL url = createUrlWithParams(section, entry);
            Client client = ClientBuilderUtil.createClient();

            Invocation.Builder builder = client.target(url.toURI()).request(MediaType.TEXT_HTML_TYPE);
            Response response = builder.post(null);


            String entity = response.readEntity(String.class);
            Document document = Jsoup.parse(entity);
            Element element = document.selectFirst("div2");
            return element != null;
        }catch(URISyntaxException ex){
            LOGGER.error("exception building URI on hasInfo", ex);
            return false;
        }
    }


    private int findNotHasBoundary(int section){
        int index = 1;
        while(hasInfo(section, index)){
            index = index * 2;
        }
        return index;
    }

    private int findEnd(int section, int biggestHas, int biggestNotHas){
        boolean thisHas = hasInfo(section, biggestHas);
        boolean nextHas = hasInfo(section, biggestHas + 1);

        if (thisHas && !nextHas){
            return biggestHas;
        } else if (thisHas && nextHas){
            LOGGER.info("biggestHas = "  + biggestHas + " biggestNoHas " + biggestNotHas);
            int delta = (biggestNotHas - biggestHas) / 2;
            return findEnd(section, biggestHas + delta, biggestNotHas);
        } else { //none has, rewind
            int delta = (biggestNotHas - biggestHas) / 2;
            LOGGER.info("biggestHas = "  + biggestHas + " biggestNoHas " + biggestNotHas);
            return findEnd(section,  biggestHas - delta, biggestHas);
        }
    }


    public URL createUrlWithParams(int section, int entry) {

        try {
            URIBuilder builder = new URIBuilder(PLATTS_URL);
            String relativePath = "/cgi-bin/philologic/getobject.pl";
            builder.setPath(relativePath);
            builder.setCustomQuery("c." + section + ":1:" + entry + ".caturvedi");
            return builder.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            String msg = String.format("Error creating URL for Pratts ingestion with parameters %d %d. The exception is %s ", section, entry, e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }
}
