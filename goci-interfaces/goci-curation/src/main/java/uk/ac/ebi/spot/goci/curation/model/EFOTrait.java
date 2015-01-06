package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Entity;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p>
 *         Model object representing an EFO trait which is normally assigned at the SNP level
 */

@Entity
public class EFOTrait extends Trait {
    private String trait;

    private String uri;

    // JPA no-args constructor
    public EFOTrait() {
    }

    public EFOTrait(Long id, String trait, String uri) {
        super(id);
        this.trait = trait;
        this.uri = uri;
    }

    public String getTrait() {
        return trait;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "EFOTrait{" +
                "trait='" + trait + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
