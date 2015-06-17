package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Collection;

/**
 * Created by Laurent on 15/05/15.
 */
@Entity
public class Location {

    @Id
    @GeneratedValue
    private Long id;

    private String chromosomeName;

    private String chromosomePosition;

    @ManyToOne
    private Region region;

    // JPA no-args constructor
    public Location() {}

    public Location( String chromosomeName,
                     String chromosomePosition,
                     Region region) {
        this.chromosomeName = chromosomeName;
        this.chromosomePosition = chromosomePosition;
        this.region = region;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChromosomeName() {
        return chromosomeName;
    }

    public void setChromosomeName(String chromosomeName) {
        this.chromosomeName = chromosomeName;
    }

    public String getChromosomePosition() {
        return chromosomePosition;
    }

    public void setChromosomePosition(String chromosomePosition) {
        this.chromosomePosition = chromosomePosition;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }


    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", chromosomeName='" + chromosomeName + '\'' +
                ", chromosomePosition='" + chromosomePosition + '\'' +
                '}';
    }

}
