package com.murshid.ingestor.wikitionary;

import com.google.common.collect.Lists;
import com.murshid.ingestor.utils.FunctionUtil;
import com.murshid.ingestor.wikitionary.models.WikiEntry;
import com.murshid.ingestor.wikitionary.models.WikiPosParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.sql.*;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class WikitionaryDBWriterByLetter implements Callable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikitionaryDBWriterByLetter.class);
    private static final Logger FILE_LOGGER = LoggerFactory.getLogger("ingestion_errors");

    public String initial;

    public static void main(String[] args) throws Exception{
        new WikitionaryDBWriterByLetter("श").call();
    }

    public WikitionaryDBWriterByLetter(String initial) {
        this.initial = initial;
    }

    @Override
    public Object call() throws Exception {
        LOGGER.info("processing letter {}", initial);

        Connection con = null;
        ResultSet rs;

        String dbUrl = "jdbc:mysql://localhost:3306/murshid";
        String user = "root";
        String password = "";

        WikitionaryCaller caller = new WikitionaryCaller();

        String currentWord = null;

        Boolean success = true;

        try {

            con = DriverManager.getConnection(dbUrl, user, password);
            con.setAutoCommit(false);


            PreparedStatement attemptsPs = con.prepareStatement(
                    "INSERT INTO attempts (entry, language, source, attempted_at, successful) values (?, 'HINDI', 'WIKITIONARY', current_timestamp, ?)");


            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO wikitionary (hindi_word, word_index, ipa_pronunciation, part_of_speech, accidence, urdu_spelling, meaning, etymology)  VALUES (?, ?, ?, ?, ?, ?,?, ?) ");


            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT hindi_word from spell_check where initial = '" + initial + " and hindi_word='शोला' '" +
                                     "' and hindi_word not in (select entry from attempts where source='wikitionary' ) ");

            Statement count = con.createStatement();
            ResultSet rsCount = count.executeQuery("SELECT count(*) from spell_check where initial = '" + initial + " and hindi_word='शोला' '" +
                    "' and hindi_word not in (select entry from attempts where source='wikitionary'  ) ");
            rsCount.next();
            LOGGER.info("{} records remain to be processed for initial {} ", rsCount.getInt(1), initial);


            int transactionIndex = 0;
            while (rs.next()) {
                String word = rs.getString("hindi_word");
                currentWord = word;

                String retryMsg = "Crawling failed for canonicalWord " + word + " retrying [{}x]";
                String failureMsg = "Could not properly crawl canonicalWord " + word;
                org.jsoup.nodes.Document document = FunctionUtil.retryFn(() -> WikitionaryCaller.documentForWord(caller, word),
                                            e -> e instanceof SocketTimeoutException || e instanceof javax.ws.rs.ProcessingException ,
                                            Duration.ofSeconds(1).toMillis(), retryMsg, failureMsg);
                Optional<WikiEntry> entry = WikiUtils.populateEntry(word, document);

                List<WikiEntry> entriesToWrite;
                if (entry.isPresent()) {
                    entriesToWrite = Lists.newArrayList(entry.get());
                } else {
                    entriesToWrite = WikiUtils.populateEtymologyEntries(word, document);
                }

                if (!entriesToWrite.isEmpty()) {
                    int index = 0;
                    for (WikiEntry wikiEntry : entriesToWrite) {
                        for (WikiPosParagraph par : wikiEntry.posParagraphs) {
                            for (int parIndex = 0; parIndex < par.meanings.size(); parIndex++) {
                                ps.setString(1, wikiEntry.hindiEntry);
                                ps.setInt(2, index);
                                Optional<String> ipaPronunciation = wikiEntry.IPAPronunciation;
                                ps.setString(3, ipaPronunciation.orElse(null));
                                ps.setString(4, par.partOfSpeech.name());
                                ps.setString(5, par.accidence.isEmpty() ? null : par.accidence.toString());
                                ps.setString(6, par.urduSpelling.orElse(null));
                                ps.setString(7, par.meanings.get(parIndex));
                                ps.setNString(8, wikiEntry.etymology.orElse(null));
                                ps.execute();

                                index++;

                                attemptsPs.setString(1, word);
                                attemptsPs.setBoolean(2, true);
                                attemptsPs.execute();

                            }
                        }
                    }
                } else {
                    attemptsPs.setString(1, word);
                    attemptsPs.setBoolean(2, false);
                    attemptsPs.execute();
                }

                transactionIndex++;
                if (transactionIndex % 100 == 0) {
                    con.commit();
                }

            }
            LOGGER.info("done processing initial {}", initial);
            con.commit();


        } catch (Exception ex) {
            FILE_LOGGER.error(" exception in initial={}, currentWord={}", initial, currentWord, ex);
            success = false;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }



}
