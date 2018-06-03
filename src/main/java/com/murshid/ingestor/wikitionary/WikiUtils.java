package com.murshid.ingestor.wikitionary;

import com.google.common.collect.Lists;
import com.murshid.ingestor.wikitionary.models.WikiAccidence;
import com.murshid.ingestor.wikitionary.models.WikiEntry;
import com.murshid.ingestor.wikitionary.models.WikiPartOfSpeech;
import com.murshid.ingestor.wikitionary.models.WikiPosParagraph;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WikiUtils {

    private static final Logger FILE_LOGGER = LoggerFactory.getLogger("ingestion_errors");
    private static final Logger LOGGER = LoggerFactory.getLogger(WikiUtils.class);

    public static Optional<WikiPosParagraph> obtainPosParagraph(Element paragraphPosHeader){
        WikiPosParagraph result = new WikiPosParagraph();

        Element paragraph = paragraphPosHeader.parent().nextElementSibling();
        if (paragraph == null){
            return Optional.empty();
        }

        Element urArabB = paragraph.selectFirst("b.ur-Arab");
        if (urArabB == null){
            result.urduSpelling = Optional.empty();
        }else{
            result.urduSpelling = Optional.of(urArabB.selectFirst("a").ownText());
        }

        //meanings
        Element ol = paragraph.nextElementSibling();
        if (ol == null){
            throw new RuntimeException("no <ol> of meanings after paragraph");
        }

        ol.children().forEach(li ->{
                deleteQuotes(li);
                result.meanings.add(cleanMeaning(li));
            });

        //accidence
        Elements genders =  paragraph.select("span.gender");
        if (genders != null) {
            genders.forEach(acc -> {
                Elements abbrs = acc.select("abbr");
                if (abbrs!= null){
                    abbrs.forEach(abbr -> {
                        String accidenceLabel = abbr.attr("title");
                        result.accidence.add(WikiAccidence.fromLabel(accidenceLabel));
                    });
                }
            });
        }
        return Optional.of(result);
    }

    private static String cleanMeaning(Element li){
        Elements dls = li.select("dl");
        if (dls != null){
            dls.remove();
        }
        String text = li.wholeText();
        return  text.replace("\n", "");

    }

    /**
     * Determines if all characters in a given String can be encoded as UTF-8.
     * Useful to detect weird-encoded charactets inside etymologies.
     * We should not attemtpt to insert non-UTF-8 strings into the database.
     */
    public static boolean canBeEncodedAsUTF8 (String string){
        CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

        boolean canDecodeAll = true;
        char[] chars = string.toCharArray();
        for (char c: chars){
            if (!encoder.canEncode(c)){
                canDecodeAll = false;
                break;
            }
        }

        return canDecodeAll;
    }

    /**
     * Deletes quotes from li meaning elements
     */
    private static void deleteQuotes(Element liMeaning){
        Elements innerLists = liMeaning.select("ul");
        innerLists.forEach(il -> {
            il.select("div.wiktQuote").remove();
            il.select("div.h-quotation").remove();
            il.select("div.citation-whole").remove();

        } );
        liMeaning.select("div.h-usage-example").remove();
        liMeaning.select("span.nyms").remove();
    }

    public static Optional<String> ipaPronunciation(Element hindiH2){
        Element sibling = hindiH2;

        do{
            sibling = sibling.nextElementSibling();
            if (sibling == null){
                return Optional.empty();
            }
            if (sibling.is("h3")){
                if (sibling.selectFirst("span").hasAttr("id")){
                    String id = sibling.selectFirst("span").attr("id");
                    if (id.startsWith("Pronunciation")){
                        Element ul = sibling.nextElementSibling();
                        Element li = ul.selectFirst("li");
                        if (li == null){
                            return Optional.empty();
                        }
                        Elements ipas = li.select("span.IPA");
                        StringBuilder sb = new StringBuilder();
                        for (Element ipa: ipas){
                            sb.append(ipa.ownText());
                            sb.append(", ");
                        }
                        if (sb.length() > 2){
                            sb.delete(sb.length()-2, sb.length());
                        }
                        return Optional.of(sb.toString());
                    }
                }
            } else if (sibling.is("h2")){ //we reached another language, quit
                break;
            }
        }while(sibling != null);

        return Optional.empty();
    }

    public static Optional<String> singleEtymology(Element hindiH2){
        Element sibling = hindiH2;

        do{
            sibling = sibling.nextElementSibling();
            if (sibling == null){
                return Optional.empty();
            }
            if (sibling.is("h3")){
                if (sibling.selectFirst("span").hasAttr("id")){
                    String id = sibling.selectFirst("span").attr("id");
                    if (id.startsWith("Etymology")) {
                        Element p =  sibling.nextElementSibling();
                        if (p.is("p")){
                            String text = p.wholeText();
                            if (canBeEncodedAsUTF8(text)){
                                return Optional.of(text);
                            }else{
                                return Optional.empty();
                            }
                        }
                    }
                }
            }
        }while(sibling != null);

        return Optional.empty();
    }

    /**
     * Attempts to populate those entries where Etymilogy(es) are the main entry instead of the POS
     * @param entry             the Hindi hindiWordIndex
     * @param document          the whole JSoup document
     * @return                  a list with 0 or more WikiEntries
     */
    public static List<WikiEntry> populateEtymologyEntries(String entry, Document document){
        List<WikiEntry> wikiEntries = new ArrayList<>();
        try {

            Optional<Element> hindiH2 = WikiUtils.obtainHindiHead(document);
            if (!hindiH2.isPresent()) {
                return Lists.newArrayList();
            }

            List<Element> etymologyHeaders = extractEtymologyHeaders(hindiH2.get());

            for (Element etyHeader : etymologyHeaders) {
                for (WikiPartOfSpeech pos: WikiPartOfSpeech.values()) {
                    Optional<Element> posHeader = WikiUtils.extractH4POSUnderEtymology(etyHeader, pos);
                    if (posHeader.isPresent()) {
                        WikiEntry wikiEntry = new WikiEntry();
                        wikiEntry.hindiEntry = entry;
                        wikiEntry.IPAPronunciation = WikiUtils.ipaPronunciation(hindiH2.get());
                        wikiEntry.etymology = WikiUtils.extractEtymologyUnderEtymologyHeader(etyHeader);
                        Optional<WikiPosParagraph> posParagraph = obtainPosParagraph(posHeader.get());
                        if (posParagraph.isPresent()) {
                            posParagraph.get().partOfSpeech = pos;
                            wikiEntry.posParagraphs.add(posParagraph.get());
                        }
                        wikiEntries.add(wikiEntry);
                    }
                }
            }

        }catch (RuntimeException ex){
            FILE_LOGGER.error("error processing hindiWordIndex={} in the Etymologies manner", entry, ex.getMessage());
        }
        return wikiEntries;
    }

    public static Optional<WikiEntry> populateEntry(String entry, Document document){
        WikiEntry wikiEntry = new WikiEntry();
        try {

            wikiEntry.hindiEntry = entry;

            Optional<Element> hindiH2 = WikiUtils.obtainHindiHead(document);
            if (!hindiH2.isPresent()) {
                return Optional.empty();
            }

            wikiEntry.etymology = WikiUtils.singleEtymology(hindiH2.get());

            for (WikiPartOfSpeech pos : WikiPartOfSpeech.values()) {
                Optional<Element> paragraphHeader = WikiUtils.extractPosHeader(hindiH2.get(), pos);
                if (paragraphHeader.isPresent()) {
                    Optional<WikiPosParagraph> posParagraph = obtainPosParagraph(paragraphHeader.get());
                    if (posParagraph.isPresent()) {
                        posParagraph.get().partOfSpeech = pos;
                        wikiEntry.posParagraphs.add(posParagraph.get());
                    }
                }
            }

            wikiEntry.IPAPronunciation = WikiUtils.ipaPronunciation(hindiH2.get());

            if (wikiEntry.posParagraphs.isEmpty()) {
                return Optional.empty();
            }
        }catch (RuntimeException ex){
            FILE_LOGGER.error("error processing hindiWordIndex={}", entry, ex);
            return Optional.empty();
        }
        return Optional.of(wikiEntry);
    }

    /**
     * Extracts the anchor <h2> with the id="Hindi"
     */
    public static Optional<Element> obtainHindiHead(Document document){
        Element spanTitle = document.getElementById("Hindi");
        if (spanTitle == null) {
            return Optional.empty();
        }else{
            return Optional.of(spanTitle.parent());
        }
    }

    /**
     * Extracts the h3 "Etymology" h3 header under a Hindi h2 header
     */
    public static List<Element> extractEtymologyHeaders(Element hindiHeader){

        List<Element> etyElements = new ArrayList<>();
        for (int i=0; i<10; i++){
            Element sibling = hindiHeader;
            Element result = null;
            do {
                sibling = sibling.nextElementSibling();
                if (sibling == null) {
                    break;
                }
                if (sibling.is("h2")){ //we arrived to another language, so quit
                    break;
                }
                if (sibling.is("h3")){
                    result = sibling.selectFirst("span[id=" + buildId("Etymology", i) + "]");
                    if (result != null){
                        etyElements.add(result.parent());
                    }
                }

            }while (result == null);
        }
        return etyElements;
    }

    /**
     * Extracts the h3 "part of speech" h3 header under a Hindi h2 header
     */
    public static Optional<Element> extractPosHeader(Element hindiHeader, WikiPartOfSpeech partOfSpeech){

        for (int i=0; i<10; i++){
            Element sibling = hindiHeader;
            Element result = null;
            do {
                sibling = sibling.nextElementSibling();
                if (sibling == null) {
                    break;
                }
                if (sibling.is("h2")){ //we arrived to another language, so quit
                    break;
                }
                if (sibling.is("h3")){
                    result = sibling.selectFirst("span[id=" + buildId(partOfSpeech.getLabel(), i) + "]");
                    if (result != null){
                        return Optional.of(result);
                    }
                }

            }while (result == null);
        }
        return Optional.empty();
    }

    /**
     * Extracts the etymology string itself below an Etymology header
     */
    public static Optional<String> extractEtymologyUnderEtymologyHeader(Element etyHeader){

        Element p = etyHeader.nextElementSibling();
        do{
            if (p != null && p.is("p")) {
                break;
            }else {
                p = p.nextElementSibling();
            }
        }while (p!= null);

        if (p == null){
            return Optional.empty();
        }

        String text = p.wholeText();
        if (canBeEncodedAsUTF8(text)){
            return Optional.of(text);
        }else{
            return Optional.empty();
        }
    }

    /**
     * Extracts the h3 "part of speech" h4 header under an Etymology h3 header
     */
    public static Optional<Element> extractH4POSUnderEtymology(Element etyHeader, WikiPartOfSpeech partOfSpeech){

        for (int i=0; i<10; i++){
            Element sibling = etyHeader;
            Element result = null;
            do {
                sibling = sibling.nextElementSibling();
                if (sibling == null) {
                    break;
                }
                if (sibling.is("h3")){ //we arrived to etymology, so leave
                    break;
                }
                if (sibling.is("h4")){
                    result = sibling.selectFirst("span[id=" + buildId(partOfSpeech.getLabel(), i) + "]");
                    if (result != null){
                        return Optional.of(result);
                    }
                }

            }while (result == null);
        }
        return Optional.empty();
    }

    private static String buildId(String name, int index){
        name = name.replace(" ", "_");
        if (index== 0){
            return name;
        }else{
            return name.concat("_") + index;
        }
    }


}
