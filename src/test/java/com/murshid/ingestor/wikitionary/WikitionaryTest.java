package com.murshid.ingestor.wikitionary;


import com.murshid.models.Accidence;
import com.murshid.ingestor.wikitionary.models.WikiEntry;
import com.murshid.ingestor.wikitionary.models.WikiPartOfSpeech;
import com.murshid.ingestor.wikitionary.models.WikiPosParagraph;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;



public class WikitionaryTest {

    @Test
    public void posParagraphExtraction() throws Exception {

        String word = "रब";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<Element> h2Hindi = WikiUtils.obtainHindiHead(document);

        assertTrue(h2Hindi.isPresent());

        //pronunciation
        Optional<String> pronunciation = WikiUtils.ipaPronunciation(h2Hindi.get());
        assertTrue(pronunciation.isPresent());
        assertTrue(pronunciation.get().contains("ɾəb"));


        Optional<Element> pos = WikiUtils.extractPosHeader(h2Hindi.get(), WikiPartOfSpeech.NOUN);

        assertTrue(pos.isPresent());

        Optional<WikiPosParagraph> paragraphOpt = WikiUtils.obtainPosParagraph(pos.get());
        assertTrue(paragraphOpt.isPresent());
        WikiPosParagraph paragraph = paragraphOpt.get();

        assertTrue(paragraph.urduSpelling.isPresent());
        assertEquals(paragraph.urduSpelling.get(), "رب" );
        assertFalse(paragraph.meanings.isEmpty());
        assertEquals(paragraph.meanings.get(0), "God");
        assertFalse(paragraph.accidence.isEmpty());
        assertTrue(paragraph.accidence.contains(Accidence.MASCULINE));
    }


    @Test
    public void etymologiesExtractionBuut() throws Exception {

        String word = "बूट";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        List<WikiEntry> wikiEntries = WikiUtils.populateEtymologyEntries(word, document);

        assertFalse(wikiEntries.isEmpty());
        assertEquals(wikiEntries.size(), 2);
        assertTrue(wikiEntries.get(0).etymology.isPresent());
        assertEquals(wikiEntries.get(0).etymology.get(), "Borrowing from English boot.");

        assertTrue(wikiEntries.get(1).etymology.isPresent());
        assertEquals(wikiEntries.get(1).etymology.get(), "From Sanskrit विटप (viṭapa).");
    }

    @Test
    public void etymologiesExtractionQasuur() throws Exception {

        String word = "क़सूर";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        List<WikiEntry> wikiEntries = WikiUtils.populateEtymologyEntries(word, document);

        assertFalse(wikiEntries.isEmpty());
        assertEquals(wikiEntries.size(), 2);
        assertTrue(wikiEntries.get(0).etymology.isPresent());
        assertEquals(wikiEntries.get(0).etymology.get(), "Borrowed from Arabic قُصُور‎ (quṣūr).");

        assertTrue(wikiEntries.get(1).etymology.isPresent());
        assertEquals(wikiEntries.get(1).etymology.get(), "Ultimately named for Kusha (Ramayana).");
    }

    @Test
    public void multilanguageEntryExtraction() throws Exception {

        String word = "उ";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> wikiEntry = WikiUtils.populateEntry(word, document);

        assertTrue(wikiEntry.isPresent());
        assertEquals(wikiEntry.get().posParagraphs.size(), 1);
    }

    @Test
    public void extractionWithQuotations() throws Exception {

        String word = "जी";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> wikiEntry = WikiUtils.populateEntry(word, document);

        assertTrue(wikiEntry.isPresent());
        assertEquals(wikiEntry.get().posParagraphs.size(), 2);
    }

    @Test
    public void extractOo() throws Exception {

        String word = "ओ";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        List<WikiEntry> wikiEntry = WikiUtils.populateEtymologyEntries(word, document);

        assertFalse(wikiEntry.isEmpty());
    }

    @Test
    public void extractHooga() throws Exception {

        String word = "होगी";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> wikiEntry = WikiUtils.populateEntry(word, document);

        assertTrue(wikiEntry.isPresent());
    }

    @Test
    public void extractMultipleAccidence() throws Exception {

        String word = "मायने";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> wikiEntry = WikiUtils.populateEntry(word, document);

        assertTrue(wikiEntry.isPresent());
        assertEquals(wikiEntry.get().posParagraphs.size(), 1);
        List<Accidence>  accidences = wikiEntry.get().posParagraphs.get(0).accidence;
        assertTrue(accidences.contains(Accidence.MASCULINE));
        assertTrue(accidences.contains(Accidence.PLURAL_NUMBER));

    }

    @Test
    public void pronunciationExtractedToEntry() throws Exception {

        String word = "फैलना";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> wikiEntry = WikiUtils.populateEntry(word, document);

        assertTrue(wikiEntry.isPresent());
        assertTrue(wikiEntry.get().IPAPronunciation.isPresent());
        assertEquals(wikiEntry.get().IPAPronunciation.get(), "[pʰɛl.naː]");
    }

    @Test
    public void extractBang() throws Exception {

        String word = "अंग्रेज़";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> wikiEntry = WikiUtils.populateEntry(word, document);

        assertTrue(wikiEntry.isPresent());
        assertEquals(wikiEntry.get().posParagraphs.size(), 2);
    }

    @Test
    public void abilityToEncode() throws Exception {

        String hindiString =  "अंग्रेज़";
        String urduString = "انجانا";
        String latinString = "amor";
        String weirdString = "Borrowing from Persian شهر\u200E (šahr), from Middle Persian \uD802\uDF71\uD802\uDF72\uD802\uDF65\uD802\uDF69\u200E (šahr), from Old Persian \uD800\uDFA7\uD800\uDFC1\uD800\uDFC2\uD800\uDFB6 (xšaça).";

        assertTrue(WikiUtils.canBeEncodedAsUTF8(hindiString));
        assertTrue(WikiUtils.canBeEncodedAsUTF8(urduString));
        assertTrue(WikiUtils.canBeEncodedAsUTF8(latinString));

        assertFalse(WikiUtils.canBeEncodedAsUTF8(weirdString));
    }

    @Test
    public void extractRan() throws Exception {

        String word = "अंग्रेज़";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        List<WikiEntry> wikiEntry = WikiUtils.populateEtymologyEntries(word, document);

    }

    @Test
    public void extractTat() throws Exception {

        String word = "तट";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> wikiEntry = WikiUtils.populateEntry(word, document);

        assertTrue(wikiEntry.isPresent());
        assertEquals(wikiEntry.get().posParagraphs.get(0).meanings.size(), 3);
        assertEquals(wikiEntry.get().posParagraphs.get(0).meanings.get(2), "strand, riverside, bank");
    }

    @Test
    public void extractZamaanat() throws Exception {

        String word = "ज़मानत";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> wikiEntry = WikiUtils.populateEntry(word, document);

        assertTrue(wikiEntry.isPresent());

        assertEquals(wikiEntry.get().posParagraphs.size(), 1);
        assertEquals(wikiEntry.get().posParagraphs.get(0).meanings.size(), 1);
        assertEquals(wikiEntry.get().posParagraphs.get(0).meanings.get(0), "bail");

    }

    @Test
    public void extractDaya() throws Exception {

        String word = "दया";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> wikiEntry = WikiUtils.populateEntry(word, document);

        assertTrue(wikiEntry.isPresent());
        assertFalse(wikiEntry.get().IPAPronunciation.isPresent());
        assertEquals(wikiEntry.get().posParagraphs.size(), 1);
    }

    @Test
    public void verbParagraphExtraction() throws Exception {

        String word = "बिखरना";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<Element> h2Hindi = WikiUtils.obtainHindiHead(document);

        assertTrue(h2Hindi.isPresent());

        Optional<Element> pos = WikiUtils.extractPosHeader(h2Hindi.get(), WikiPartOfSpeech.VERB);

        assertTrue(pos.isPresent());

        Optional<WikiPosParagraph> paragraphOpt = WikiUtils.obtainPosParagraph(pos.get());
        assertTrue(paragraphOpt.isPresent());
        WikiPosParagraph paragraph = paragraphOpt.get();

        assertNotNull(paragraph);
        assertFalse(paragraph.meanings.isEmpty());
        assertTrue(paragraph.meanings.get(0).contains("scatter"));
    }

    @Test
    public void adjectiveParagraphExtraction() throws Exception {

        String word = "ख़ूबसूरत";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<Element> h2Hindi = WikiUtils.obtainHindiHead(document);

        assertTrue(h2Hindi.isPresent());


        //pronunciation
        Optional<String> pronunciation = WikiUtils.ipaPronunciation(h2Hindi.get());
        assertTrue(pronunciation.isPresent());
        assertTrue(pronunciation.get().contains("kʰuːb.suː.ɾət̪"));

        Optional<Element> pos = WikiUtils.extractPosHeader(h2Hindi.get(), WikiPartOfSpeech.ADJECTIVE);

        assertTrue(pos.isPresent());

        Optional<WikiPosParagraph> paragraphOpt = WikiUtils.obtainPosParagraph(pos.get());
        assertTrue(paragraphOpt.isPresent());
        WikiPosParagraph paragraph = paragraphOpt.get();

        assertNotNull(paragraph);
        assertTrue(paragraph.urduSpelling.isPresent());
        assertEquals(paragraph.urduSpelling.get(), "خوبصورت" );
        assertFalse(paragraph.meanings.isEmpty());
        assertTrue(paragraph.meanings.get(0).contains("beautiful"));
    }

    @Test
    public void pronounParagraphExtraction() throws Exception {

        String word = "अपना";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<Element> h2Hindi = WikiUtils.obtainHindiHead(document);

        assertTrue(h2Hindi.isPresent());

        Optional<Element> pos = WikiUtils.extractPosHeader(h2Hindi.get(), WikiPartOfSpeech.PRONOUN);

        assertTrue(pos.isPresent());

        Optional<WikiPosParagraph> paragraphOpt = WikiUtils.obtainPosParagraph(pos.get());
        assertTrue(paragraphOpt.isPresent());
        WikiPosParagraph paragraph = paragraphOpt.get();

        assertNotNull(paragraph);
        assertFalse(paragraph.meanings.isEmpty());
        assertTrue(paragraph.meanings.get(0).contains("own"));
    }

    @Test
    public void adverbParagraphExtraction() throws Exception {

        String word = "यहाँ";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<Element> h2Hindi = WikiUtils.obtainHindiHead(document);

        assertTrue(h2Hindi.isPresent());

        Optional<Element> pos = WikiUtils.extractPosHeader(h2Hindi.get(), WikiPartOfSpeech.ADVERB);

        assertTrue(pos.isPresent());

        Optional<WikiPosParagraph> paragraphOpt = WikiUtils.obtainPosParagraph(pos.get());
        assertTrue(paragraphOpt.isPresent());
        WikiPosParagraph paragraph = paragraphOpt.get();

        assertNotNull(paragraph);
        assertFalse(paragraph.meanings.isEmpty());
        assertTrue(paragraph.meanings.get(0).equals("here"));
    }

    @Test
    public void conjunctionAdverbParagraphExtraction() throws Exception {

        String word = "और";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<Element> h2Hindi = WikiUtils.obtainHindiHead(document);

        assertTrue(h2Hindi.isPresent());

        //pronunciation
        Optional<String> pronunciation = WikiUtils.ipaPronunciation(h2Hindi.get());
        assertTrue(pronunciation.isPresent());
        assertTrue(pronunciation.get().contains("[ɔːɾ]"));


        //adverb
        Optional<Element> pos = WikiUtils.extractPosHeader(h2Hindi.get(), WikiPartOfSpeech.ADVERB);

        assertTrue(pos.isPresent());

        Optional<WikiPosParagraph> paragraphOpt = WikiUtils.obtainPosParagraph(pos.get());
        assertTrue(paragraphOpt.isPresent());
        WikiPosParagraph paragraph = paragraphOpt.get();

        assertNotNull(paragraph);
        assertFalse(paragraph.meanings.isEmpty());
        assertTrue(paragraph.meanings.get(0).equals("more"));

        //conjunction
        pos = WikiUtils.extractPosHeader(h2Hindi.get(), WikiPartOfSpeech.CONJUNCTION);

        assertTrue(pos.isPresent());

        paragraphOpt = WikiUtils.obtainPosParagraph(pos.get());
        assertTrue(paragraphOpt.isPresent());
        paragraph = paragraphOpt.get();

        assertNotNull(paragraph);
        assertTrue(paragraph.urduSpelling.isPresent());
        assertEquals(paragraph.urduSpelling.get(), "اور" );
        assertFalse(paragraph.meanings.isEmpty());
        assertTrue(paragraph.meanings.get(0).equals("and"));

    }

    @Test
    public void extractPronunciationH2Header() throws Exception {

        String word = "कोह";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<Element> h2Hindi = WikiUtils.obtainHindiHead(document);

        assertTrue(h2Hindi.isPresent());

        Optional<String> pronunciation = WikiUtils.ipaPronunciation(h2Hindi.get());

        assertTrue(pronunciation.isPresent());

        assertEquals(pronunciation.get(), "/koːʱ/");
    }

    @Test
    public void postpositionParagraphExtraction() throws Exception {

        String word = "के";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<Element> h2Hindi = WikiUtils.obtainHindiHead(document);

        assertTrue(h2Hindi.isPresent());

        Optional<Element> pos = WikiUtils.extractPosHeader(h2Hindi.get(), WikiPartOfSpeech.POSTPOSITION);

        assertTrue(pos.isPresent());

        Optional<WikiPosParagraph> paragraphOpt = WikiUtils.obtainPosParagraph(pos.get());
        assertTrue(paragraphOpt.isPresent());
        WikiPosParagraph paragraph = paragraphOpt.get();

        assertNotNull(paragraph);
        assertFalse(paragraph.meanings.isEmpty());
        assertEquals(paragraph.meanings.size(), 5);
        assertTrue(paragraph.meanings.get(0).contains("का"));
    }

    @Test
    public void populateWithNeuterGender() throws Exception {

        String word = "अकारण";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> entry = WikiUtils.populateEntry(word, document);

        assertTrue(entry.isPresent());
    }

    @Test
    public void populatePrefix() throws Exception {

        String word = "अधि-";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> entry = WikiUtils.populateEntry(word, document);

        assertTrue(entry.isPresent());
    }


    @Test
    public void populatePerfectParticiple() throws Exception {

        String word = "बैठा";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> entry = WikiUtils.populateEntry(word, document);

        assertTrue(entry.isPresent());
    }

    @Test
    public void populateEntryProperNoun() throws Exception {

        String word = "एशिया";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<WikiEntry> entry = WikiUtils.populateEntry(word, document);

        assertTrue(entry.isPresent());
    }

    @Test
    public void interjectionAndNounParagraphExtraction() throws Exception {

        String word = "हाय";

        WikitionaryCaller caller = new WikitionaryCaller();
        Document document = WikitionaryCaller.documentForWord(caller, word);

        Optional<Element> h2Hindi = WikiUtils.obtainHindiHead(document);
        assertTrue(h2Hindi.isPresent());

        //pronunciation
        Optional<String> pronunciation = WikiUtils.ipaPronunciation(h2Hindi.get());
        assertTrue(pronunciation.isPresent());
        assertTrue(pronunciation.get().contains("ɦɑːj"));

        //interjection
        Optional<Element> pos = WikiUtils.extractPosHeader(h2Hindi.get(), WikiPartOfSpeech.INTERJECTION);

        assertTrue(pos.isPresent());

        Optional<WikiPosParagraph> paragraphOpt = WikiUtils.obtainPosParagraph(pos.get());
        assertTrue(paragraphOpt.isPresent());
        WikiPosParagraph paragraph = paragraphOpt.get();

        assertNotNull(paragraph);
        assertFalse(paragraph.meanings.isEmpty());
        assertTrue(paragraph.meanings.get(0).contains("alas!"));

        //noun
        pos = WikiUtils.extractPosHeader(h2Hindi.get(), WikiPartOfSpeech.NOUN);

        assertTrue(pos.isPresent());

        paragraphOpt = WikiUtils.obtainPosParagraph(pos.get());
        assertTrue(paragraphOpt.isPresent());
        paragraph = paragraphOpt.get();

        assertNotNull(paragraph);
        assertFalse(paragraph.accidence.isEmpty());
        assertTrue(paragraph.accidence.contains(Accidence.FEMININE) );
        assertFalse(paragraph.meanings.isEmpty());
        assertTrue(paragraph.meanings.get(0).contains("an exclamation of distress; a sigh"));

    }

}
