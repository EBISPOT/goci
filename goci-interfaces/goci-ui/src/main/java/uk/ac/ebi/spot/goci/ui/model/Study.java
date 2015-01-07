package uk.ac.ebi.spot.goci.ui.model;

import javax.persistence.*;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/11/14
 */
@Entity
@Table(name = "GWASSTUDIES")
public class Study {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "PMID")
    private String pubmedId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DISEASEID")
    public DiseaseTrait diseaseTrait;

    Study() {
    }

    Study(String pubmedId, DiseaseTrait diseaseTrait) {
        this.pubmedId = pubmedId;
        this.diseaseTrait = diseaseTrait;
    }

    public Long getId() {
        return id;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    @Override public String toString() {
        return "Study{" +
                "id=" + id +
                ", pubmedId='" + pubmedId + '\'' +
                ", diseaseTrait='" + diseaseTrait + '\'' +
                '}';
    }
}
