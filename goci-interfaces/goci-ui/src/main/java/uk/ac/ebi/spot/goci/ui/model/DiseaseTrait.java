package uk.ac.ebi.spot.goci.ui.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
* Created by dwelter on 18/12/14.
*/
@Entity
@Table(name = "GWASDISEASETRAITS")
public class DiseaseTrait {

    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID")
    public Long id;

    @Column(name = "DISEASETRAIT")
    public String diseaseTrait;

    // JPA no-args constructor
    public DiseaseTrait() {
    }

    public DiseaseTrait(Long id, String diseaseTrait) {
        this.id = id;
        this.diseaseTrait = diseaseTrait;
    }

    public String getDiseaseTrait() {
        return diseaseTrait;
    }

    @Override
    public String toString() {
        return "DiseaseTrait{" +
                "id=" + id +
                "diseaseTrait='" + diseaseTrait + '\'' +
                '}';
    }
}
