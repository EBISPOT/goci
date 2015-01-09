package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         A model object representing a gene and its attributes including associated single nucleotide polymorphisms
 */

@Entity
public class Gene {
    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    private String geneName;

    // JPA no-args constructor
    public Gene() {
    }

    public Gene(String geneName) {
        this.geneName = geneName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "id=" + id +
                ", geneName='" + geneName + '\'' +
                '}';
    }
}
