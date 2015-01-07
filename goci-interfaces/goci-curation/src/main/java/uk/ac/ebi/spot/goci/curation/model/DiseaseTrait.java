package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Entity;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Mdoel object representing a disease trait which is assigned normally at the study level
 */


@Entity
public class DiseaseTrait extends Trait {
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

    @Override
    public String toString() {
        return "DiseaseTrait{" +
                "trait='" + trait + '\'' +
                '}';
    }
}
