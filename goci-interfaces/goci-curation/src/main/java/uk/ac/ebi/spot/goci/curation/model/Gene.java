package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p/>
 *         A model object representing a gene and its attributes including associated single nucleotide polymorphisms
 */

@Entity
public class Gene {

    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    private String geneName;

    // TODO EACH GENE WILL BE LINKED TO NUMBER OF SNPS, OVERENGINEERING????
    //Association with SNPs
//    @ManyToMany(mappedBy="genes",fetch=FetchType.LAZY)
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
