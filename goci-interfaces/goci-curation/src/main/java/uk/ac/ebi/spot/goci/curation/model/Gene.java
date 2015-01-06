package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.Collection;

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

    @ManyToMany
    private Collection<SingleNucleotidePolymorphism> snps;

    // JPA no-args constructor
    public Gene() {
    }

    public Gene(String geneName, Collection<SingleNucleotidePolymorphism> snps) {
        this.geneName = geneName;
        this.snps = snps;
    }

    public Long getId() {
        return id;
    }

    public String getGeneName() {
        return geneName;
    }

    public Collection<SingleNucleotidePolymorphism> getSnps() {
        return snps;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "id=" + id +
                ", geneName='" + geneName + '\'' +
                ", snps=" + snps +
                '}';
    }
}
