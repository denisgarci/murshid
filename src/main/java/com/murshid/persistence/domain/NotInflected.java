package com.murshid.persistence.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.murshid.models.enums.PartOfSpeech;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "notInflected")
public class NotInflected implements Serializable, Cloneable, FluentModel{

    @Embeddable
    public static class NotInflectedKey implements Serializable {
        @JsonProperty("hindi")
        @Column(name = "hindi")
        public String hindi;

        @JsonProperty("hindi_index")
        @Column(name = "hindi_index")
        public int hindiIndex;

        public int getHindiIndex() {
            return hindiIndex;
        }

        public NotInflectedKey setHindiIndex(int hindiIndex) {
            this.hindiIndex = hindiIndex;
            return this;
        }

        public String getHindi() {
            return hindi;
        }

        public void setHindi(String hindi) {
            this.hindi = hindi;
        }
    }

    @EmbeddedId
    private NotInflectedKey notInflectedKey;

    @JoinColumn(name = "master_dictionary_id")
    @ManyToOne
    private MasterDictionary masterDictionary;

    @Enumerated(EnumType.STRING)
    @Column(name ="part_of_speech", nullable = false)
    private PartOfSpeech partOfSpeech;

    public String getUrdu() {
        return urdu;
    }

    public void setUrdu(String urdu) {
        this.urdu = urdu;
    }

    @Column(name = "urdu", nullable = false)
    private String urdu;


    public MasterDictionary getMasterDictionary() {
        return masterDictionary;
    }

    public NotInflected setMasterDictionary(MasterDictionary masterDictionary) {
        this.masterDictionary = masterDictionary;
        return this;
    }

    public NotInflectedKey getNotInflectedKey() {
        return notInflectedKey;
    }

    public NotInflected setNotInflectedKey(NotInflectedKey notInflectedKey) {
        this.notInflectedKey = notInflectedKey;
        return this;
    }


    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public NotInflected setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        return this;
    }


    @Override
    public Object clone() {
        try {
            NotInflected master = (NotInflected) super.clone();
            NotInflectedKey inflectedKey = new NotInflectedKey();
            inflectedKey.setHindi(this.notInflectedKey.hindi);
            inflectedKey.setHindiIndex(this.notInflectedKey.hindiIndex + 1);
            master
                    .setNotInflectedKey(inflectedKey)
                    .setPartOfSpeech(this.getPartOfSpeech())
                    .setMasterDictionary(this.getMasterDictionary())
                    .setUrdu(this.urdu);
            return master;
        }catch (CloneNotSupportedException ex){
            return null;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (!(o instanceof NotInflected)) { return false; }

        NotInflected master = (NotInflected) o;

        return new EqualsBuilder()
                .append(getNotInflectedKey().getHindi(), master.getNotInflectedKey().getHindi())
                .append(getPartOfSpeech(), master.getPartOfSpeech())
                .append(getMasterDictionary(), getMasterDictionary())
                .isEquals();
    }

    @Override
    public String toString() {
        return "InflectedRepositoryDB{" +
                "  masterDictonaryKey='" + getMasterDictionary() + '\'' +
                "  hindi='" + notInflectedKey.getHindi() + '\'' +
                ", urdu='" + urdu + '\'' +
                ", hindiIndex=" + notInflectedKey.hindiIndex +
                ", partOfSpeech=" + partOfSpeech +
                '}';
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getNotInflectedKey().getHindi())
                .append(getPartOfSpeech())
                .append(getMasterDictionary())
                .toHashCode();
    }

    public String getKey(){
        return getNotInflectedKey().getHindi().concat("_").concat(Integer.toString(getNotInflectedKey().getHindiIndex()));
    }

    @Override
    public FluentModel self() {
        return this;
    }
}
