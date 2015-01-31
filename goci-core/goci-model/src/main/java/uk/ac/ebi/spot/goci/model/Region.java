package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Model of name information associated with a SNP
 */
@Entity
public class Region {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // JPA no-args constructor
    public Region() {
    }

    public Region(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
