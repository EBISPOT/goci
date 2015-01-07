package uk.ac.ebi.spot.goci.ui.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/11/14
 */
@Entity
@Table(name = "GWASASSOCIATIONS")
public class Snp {
    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID")
    private Long id;

    @Column(name = "SNP")
    private String rsId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STUDYID")
    public Study study;

    Snp() {
    }

    Snp(String rsId, Study study) {
        this.rsId = rsId;
        this.study = study;
    }

    public Long getId() {
        return id;
    }

    public String getRsId() {
        return rsId;
    }

    @Override public String toString() {
        return "SNP{" +
                "id=" + id +
                ", rsId='" + rsId + '\'' +
                ", study='" + study + '\'' +
                '}';
    }
}
