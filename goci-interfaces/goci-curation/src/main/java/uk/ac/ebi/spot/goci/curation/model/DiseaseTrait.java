package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Column;
import javax.persistence.Entity;
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

    public void setTrait(String trait) {
        this.trait = trait;
    }

    @Override
    public String toString() {
        return "DiseaseTrait{" +
                "trait='" + trait + '\'' +
                '}';
    }
}
