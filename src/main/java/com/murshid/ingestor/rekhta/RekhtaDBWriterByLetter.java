package com.murshid.ingestor.rekhta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class RekhtaDBWriterByLetter implements Callable{

    private static final Logger LOGGER = LoggerFactory.getLogger(RekhtaDBWriterByLetter.class);

    public String initial;

    public RekhtaDBWriterByLetter(String initial){
        this.initial = initial;
    }

    @Override
    public Object call() throws Exception {
        LOGGER.info("processing letter {}", initial);

        Connection con = null;
        ResultSet rs;

        String dbUrl = "jdbc:mysql://localhost:3306/pratts";
        String user = "root";
        String password = "";

        RekhtaCaller caller = new RekhtaCaller();

        try {

            con = DriverManager.getConnection(dbUrl, user, password);
            con.setAutoCommit(false);


            PreparedStatement attemptsPs = con.prepareStatement(
                    "INSERT INTO attempts (entry, language, source, attempted_at, successful) values (?, 'urdu', 'rekhta', current_timestamp, ?)");


            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO rekhta (latin, urdu, hindi_word, word_index, meaning) VALUES (?, ?, ?, ?, ?)");


            Statement select = con.createStatement();
            rs = select.executeQuery("SELECT word from urdu_words where initial = '" +    initial      + "' and word not in (select entry from attempts) ");

            Statement count = con.createStatement();
            ResultSet rsCount = count.executeQuery("SELECT count(*) from urdu_words where initial = '" +    initial      + "' and word not in (select entry from attempts) ");
            rsCount.next();
            LOGGER.info("{} records remain to be processed for initial {} ", rsCount.getInt(1), initial);

            int index = 0;
            while(rs.next()){
                String word = rs.getString("hindiWord");
                Optional<List<RekhtaWebEntry>> result =  caller.fromStringEntry(word);
                if (result.isPresent()){
                    List<RekhtaWebEntry> entries = result.get();

                    for (RekhtaWebEntry entry: entries){
                        index ++;
                        ps.setString(1, entry.latin);
                        ps.setString(2, entry.urdu);
                        ps.setString(3, entry.hindiWord);
                        ps.setInt(4, entry.wordIndex);
                        ps.setString(4, entry.meaning);
                        ps.execute();
                    }
                    attemptsPs.setString(1, word);
                    attemptsPs.setBoolean(2, true);
                    attemptsPs.execute();

                    if (index % 10 == 0) {
                        con.commit();
                        LOGGER.info("added {} entries successfully for initial {} ", index, initial);
                    }

                }else{
                    attemptsPs.setString(1, word);
                    attemptsPs.setBoolean(2, false);
                    attemptsPs.execute();
                }



            }
            LOGGER.info("done processing initial {}", initial);
            con.commit();


        } catch (SQLException ex) {
            LOGGER.info(" exception in initial {}", initial);
            ex.printStackTrace();

        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
