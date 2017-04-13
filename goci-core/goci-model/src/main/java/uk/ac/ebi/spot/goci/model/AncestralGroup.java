package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

/**
 * Created by Dani on 13/04/2017.
 */

@Entity
public class AncestralGroup {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String ancestal_group;

    @JsonIgnore
    @ManyToMany(mappedBy = "ancestralGroups")
    private Collection<Ancestry> ancestries;

    // JPA no-args constructor
    public AncestralGroup() {
    }

    public AncestralGroup(String ancestal_group,
                    Collection<Ancestry> ancestries) {
        this.ancestal_group = ancestal_group;
        this.ancestries = ancestries;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAncestal_group() {
        return ancestal_group;
    }

    public void setAncestal_group(String ancestal_group) {
        this.ancestal_group = ancestal_group;
    }

    public Collection<Ancestry> getAncestries() {
        return ancestries;
    }

    public void setAncestries(Collection<Ancestry> ancestries) {
        this.ancestries = ancestries;
    }
}
