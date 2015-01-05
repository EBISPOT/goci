package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p/>
 *         Mdoel object representing a disease trait which is assigned normally at the study level
 */


@Entity
@Table(name = "GWASDISEASETRAITS")
public class DiseaseTrait extends Trait {

    @Column(name = "DISEASETRAIT")
    private String trait;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "diseaseTrait")
    private Study study;

    // JPA no-args constructor
    public DiseaseTrait() {
    }

    public DiseaseTrait(Long id, String trait) {

        super(id);
        this.trait = trait;
    }

    public String getTrait() {
        return trait;
    }

    public Study getStudy() {
        return study;
    }

    @Override
    public String toString() {
        return "DiseaseTrait{" +
                "trait='" + trait + '\'' +
                '}';
    }
}
