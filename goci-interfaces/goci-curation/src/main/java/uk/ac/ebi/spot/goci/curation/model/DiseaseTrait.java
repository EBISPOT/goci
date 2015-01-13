package uk.ac.ebi.spot.goci.curation.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Mdoel object representing a disease trait which is assigned normally at the study level
 */


@Entity
public class DiseaseTrait {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String trait;

    // JPA no-args constructor
    public DiseaseTrait() {
    }

    public DiseaseTrait(Long id, String trait) {
        this.id = id;
        this.trait = trait;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
                "id=" + id +
                ", trait='" + trait + '\'' +
                '}';
    }
}
