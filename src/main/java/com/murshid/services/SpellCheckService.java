package com.murshid.services;

import com.google.common.collect.Sets;
import com.murshid.models.enums.Accidence;
import com.murshid.models.enums.PartOfSpeech;
import com.murshid.persistence.domain.HasInflectedHindi;
import com.murshid.persistence.domain.Inflected;
import com.murshid.persistence.domain.SpellCheckEntry;
import com.murshid.persistence.repo.SpellCheckRepository;
import com.murshid.utils.WordUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Named
public class SpellCheckService {

    private SpellCheckRepository spellCheckRepository;

    public SpellCheckEntry upsert(@Nonnull String hindiWord, @Nonnull String urduWord){
        SpellCheckEntry spellCheckEntry = spellCheckRepository.findOne(hindiWord);
        if (spellCheckEntry == null){
            spellCheckEntry = new SpellCheckEntry().setHindiWord(hindiWord).setUrduWord(urduWord).setActive(true).setInitial(hindiWord.charAt(0));
            spellCheckEntry = spellCheckRepository.save(spellCheckEntry);
        }
        return spellCheckEntry;
    }

    public boolean existsWithReplacement(Inflected inflectedHindi){
        if (spellCheckRepository.findOne(inflectedHindi.getInflectedKey().getInflectedHindi()) != null){
            return true;
        }

        String original = inflectedHindi.getInflectedKey().getInflectedHindi();
        Set<Accidence> accidence = Sets.newHashSet(inflectedHindi.getAccidence());
        PartOfSpeech partOfSpeech = inflectedHindi.getPartOfSpeech();
        if (partOfSpeech == PartOfSpeech.INFINITIVE && accidence.equals(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.DIRECT))){
            String beginning = original.substring(0, original.length()-3);
            SpellCheckEntry beginningUrduEntry = spellCheckRepository.findByHindiWord(beginning);
            if (beginningUrduEntry == null){
                return false;
            }
            String beginningUrdu = beginningUrduEntry.getUrduWord();
            String proposed = beginningUrdu.concat("نیں");
            return true;
        }
        return false;
    }

    protected Pair<Inflected, Optional<String>> propose(Inflected inflected, List<Inflected> others){
        PartOfSpeech partOfSpeech = inflected.getPartOfSpeech();
        Set<Accidence> accidence = Sets.newHashSet(inflected.getAccidence());

        //feminine plural infinitives
        if (partOfSpeech == PartOfSpeech.INFINITIVE && accidence.equals(Sets.newHashSet(Accidence.FEMININE, Accidence.PLURAL, Accidence.DIRECT))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected,2, "تیں");
        }

        //feminine imperfective plural participles
        if (partOfSpeech == PartOfSpeech.PARTICIPLE && accidence.equals(Sets.newHashSet(Accidence.FEMININE, Accidence.IMPERFECTIVE, Accidence.PLURAL,  Accidence.DIRECT))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "یں");
        }

        //feminine perfective plural participles
        if (partOfSpeech == PartOfSpeech.PARTICIPLE && accidence.equals(Sets.newHashSet(Accidence.FEMININE, Accidence.PERFECTIVE, Accidence.PLURAL,Accidence.DIRECT))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "یں");
        }

        //masculine participles in -ya
        if (partOfSpeech == PartOfSpeech.PARTICIPLE && accidence.equals(Sets.newHashSet(Accidence.MASCULINE, Accidence.PERFECTIVE, Accidence.SINGULAR, Accidence.DIRECT))
              && inflected.getInflectedKey().getInflectedHindi().endsWith("या")){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "تا");
        }

        //feminine plural vocatives
        if (partOfSpeech == PartOfSpeech.NOUN && accidence.equals(Sets.newHashSet(Accidence.FEMININE, Accidence.VOCATIVE, Accidence.PLURAL))){
            Optional<Inflected> obliquePlural = findBypartOfSpeechAndAccidence(others, PartOfSpeech.NOUN,  Accidence.FEMININE, Accidence.PLURAL, Accidence.OBLIQUE);
            return substringFindConcat(obliquePlural, inflected, 1, "");
        }

        //masculine plural vocatives
        if (partOfSpeech == PartOfSpeech.NOUN && accidence.equals(Sets.newHashSet(Accidence.MASCULINE, Accidence.VOCATIVE, Accidence.PLURAL))){
            Optional<Inflected> obliquePlural = findBypartOfSpeechAndAccidence(others, PartOfSpeech.NOUN,  Accidence.MASCULINE, Accidence.PLURAL, Accidence.OBLIQUE);
            return substringFindConcat(obliquePlural, inflected, 1, "");
        }

        //agentive
        if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence.ABSOLUTIVE))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "کے");
        }

        //future masculine 1ps
        if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.MASCULINE, Accidence.FUTURE))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "وں گا");
        }

        //future feminine 1ps
        if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.FEMININE, Accidence.FUTURE))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "وں گی");
        }

        //future masculine 2ps
        if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.MASCULINE, Accidence.FUTURE))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "ے گا");
        }

        //future feminine 2ps
        if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.FEMININE, Accidence.FUTURE))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "ے گی");
        }

        //future masculine 2pp
        if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.MASCULINE, Accidence.FUTURE))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "و گی");
        }

        //future masculine 1pp
        if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.MASCULINE, Accidence.FUTURE))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "یں گے");
        }

        //future feminine 1pp
        if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.FEMININE, Accidence.FUTURE))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "یں گی");
        }

        //future feminine 2pp
        if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.FEMININE, Accidence.FUTURE))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "و گی");
        }

        //imperative 3pp - long and short forms
        if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.IMPERATIVE))){
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE,   Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);

            //forms in -jiye
            if (inflected.getInflectedKey().getInflectedHindi().endsWith("जिए")){
                return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "جئے");
            }else if (inflected.getInflectedKey().getInflectedHindi().endsWith("जिये")){
                return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "جیئے");
            }
            if (inflected.getInflectedKey().getInflectedHindi().endsWith("िये")){
                return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "یئے");
            }else if (inflected.getInflectedKey().getInflectedHindi().endsWith("ए")){
                return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "ئے");
            }

            //forms in -aaiye
            if (inflected.getInflectedKey().getInflectedHindi().endsWith("ाइए")){
                return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "ائے");
            }else if (inflected.getInflectedKey().getInflectedHindi().endsWith("ाइये")){
                return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "ائیے");
            }
        }

        {
            //subjunctives root in -II
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE, Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            // 1ps
            if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)) && inflected.getInflectedKey().getInflectedHindi().endsWith("ीऊँ")) {
                return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 3, "یؤں");
            }
            // 2ps, 3ps
            if (partOfSpeech == PartOfSpeech.VERB && inflected.getInflectedKey().getInflectedHindi().endsWith("िए")) {
                if (accidence.equals(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)) || accidence.equals(Sets.newHashSet(Accidence._3RD, Accidence.SINGULAR, Accidence.SUBJUNCTIVE))) {
                    return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "ئے");
                }
            }
            // 1pp, 3pp
            if (partOfSpeech == PartOfSpeech.VERB &&  inflected.getInflectedKey().getInflectedHindi().endsWith("िएँ")) {
                if (accidence.equals(Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.SUBJUNCTIVE)) || accidence.equals(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.SUBJUNCTIVE))){
                    return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 3, "ئیں");
                }
            }
            // 2pp
            if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.SUBJUNCTIVE)) && inflected.getInflectedKey().getInflectedHindi().endsWith("िओ")) {
                return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "یؤ");
            }
        }

        {
            //subjunctives root in -AA
            Optional<Inflected> infinitiveMasculineSingularDirect = findBypartOfSpeechAndAccidence(others, PartOfSpeech.INFINITIVE, Accidence.MASCULINE, Accidence.SINGULAR, Accidence.DIRECT);
            // 1ps
            if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._1ST, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)) && inflected.getInflectedKey().getInflectedHindi().endsWith("ाऊँ")) {
                return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 3, "اوں");
            }
            // 2ps, 3ps
            if (partOfSpeech == PartOfSpeech.VERB && inflected.getInflectedKey().getInflectedHindi().endsWith("ाए")) {
                if (accidence.equals(Sets.newHashSet(Accidence._2ND, Accidence.SINGULAR, Accidence.SUBJUNCTIVE)) || accidence.equals(Sets.newHashSet(Accidence._3RD, Accidence.SINGULAR, Accidence.SUBJUNCTIVE))) {
                    return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "اے");
                }
            }
            // 1pp, 3pp
            if (partOfSpeech == PartOfSpeech.VERB &&  inflected.getInflectedKey().getInflectedHindi().endsWith("ाएँ")) {
                if (accidence.equals(Sets.newHashSet(Accidence._1ST, Accidence.PLURAL, Accidence.SUBJUNCTIVE)) || accidence.equals(Sets.newHashSet(Accidence._3RD, Accidence.PLURAL, Accidence.SUBJUNCTIVE))){
                    return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 3, "ایں");
                }
            }
            // 2pp
            if (partOfSpeech == PartOfSpeech.VERB && accidence.equals(Sets.newHashSet(Accidence._2ND, Accidence.PLURAL, Accidence.SUBJUNCTIVE)) && inflected.getInflectedKey().getInflectedHindi().endsWith("ाओ")) {
                return substringFindConcat(infinitiveMasculineSingularDirect, inflected, 2, "او");
            }
        }

        return Pair.of(inflected, Optional.empty());
    }

    protected Optional<Inflected> findBypartOfSpeechAndAccidence(List<Inflected> inflecteds, PartOfSpeech partOfSpeech,  Accidence ... accidence ){
        Set<Accidence> setAccidences = Sets.newHashSet(accidence);
        return inflecteds.stream().filter( i -> i.getPartOfSpeech() == partOfSpeech &&  Sets.newHashSet(i.getAccidence()).equals(setAccidences)).findFirst();
    }


    private Pair<Inflected, Optional<String>> substringFindConcat(Optional<Inflected> canonicalInflected, Inflected hindiInflected, int positions, String urudSuffix){
        if (!canonicalInflected.isPresent()){
            return Pair.of(hindiInflected, Optional.empty());
        }
        if (canonicalInflected.get().getInflectedUrdu() == null){
            return Pair.of(hindiInflected, Optional.empty());
        }
        String fullUrdu = canonicalInflected.get().getInflectedUrdu();
        String beginningUrdu = fullUrdu.substring(0, fullUrdu.length()-positions);
        String proposedUrdu = beginningUrdu.concat(urudSuffix);
        return Pair.of(hindiInflected, Optional.of(proposedUrdu));
    }

    public boolean exists(String hindiWord){
        if (!spellCheckRepository.exists(hindiWord)){
            String anuReplaced = WordUtils.replaceAnusvaara(hindiWord);
            return spellCheckRepository.exists(anuReplaced);
        }else{
            return true;
        }
    }

    public boolean wordsDontExist(String hindiWord){
        String[] tokens = hindiWord.split("\\s+");
        for (String hindi: tokens){
            if (!spellCheckRepository.exists(hindi)){
                return true;
            }
        }
        return false;
    }

    String passMultipleWordsToUrdu(String hindiWords){
        String[] tokens = hindiWords.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String hindi : tokens){
            sb.append(getUrduSpelling(hindi)).append(" ");
        }
        sb.delete(sb.length()-1, sb.length());
        return sb.toString();
    }


    public <T extends HasInflectedHindi> void loadUrdus(List<T> inflectedList){
        inflectedList.forEach( inf -> {
            SpellCheckEntry spellCheckEntry = spellCheckRepository.findByHindiWord(inf.getHindi());
            if (spellCheckEntry!= null){
                inf.setUrdu(spellCheckEntry.getUrduWord());
            }
        });
    }

    private String getUrduSpelling(String hindiWord){
        SpellCheckEntry spellCheckEntry = spellCheckRepository.findByHindiWord(hindiWord);
        if (spellCheckEntry!= null){
            return spellCheckEntry.getUrduWord();
        }else{
            return null;
        }
    }

    @Inject
    public void setSpellCheckRepository(SpellCheckRepository spellCheckRepository) {
        this.spellCheckRepository = spellCheckRepository;
    }

}
