package uk.ac.ebi.spot.goci.ui.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
* Created by dwelter on 18/12/14.
*/
@Entity
@Table(name = "GWASDISEASETRAITS")
public class Trait {

    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID")
    public Long id;

    @Column(name = "DISEASETRAIT")
    public String trait;

    // JPA no-args constructor
    public Trait() {
    }

    public Trait(Long id, String trait) {
        this.id = id;
        this.trait = trait;
    }

    public String getTrait() {
        return trait;
    }

    @Override
    public String toString() {
        return "DiseaseTrait{" +
                "id=" + id +
                "trait='" + trait + '\'' +
                '}';
    }
}
