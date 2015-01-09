package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p/>
 *         Mdoel object representing a disease trait which is assigned normally at the study level
 */


@Entity
@Table(name = "GWASDISEASETRAITS")
public class DiseaseTrait  {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "DISEASETRAIT")
    private String trait;

    // JPA no-args constructor
    public DiseaseTrait() {
    }


    public DiseaseTrait(String trait) {
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
