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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISEASEID")
    public Trait trait;

    Study() {
    }

    Study(String pubmedId, Trait trait) {
        this.pubmedId = pubmedId;
        this.trait = trait;
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
                ", Trait='" + trait + '\'' +
                '}';
    }
}
