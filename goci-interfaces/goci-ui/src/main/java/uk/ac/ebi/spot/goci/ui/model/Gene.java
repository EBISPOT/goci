package uk.ac.ebi.spot.goci.ui.model;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         A model object representing a gene and its attributes including associated single nucleotide polymorphisms
 */

@Entity
@Table(name = "GWASGENE")
public class Gene {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "GENE")
    private String geneName;

    // TODO EACH GENE WILL BE LINKED TO NUMBER OF SNPS, OVERENGINEERING????
    //Association with SNPs
    @ManyToMany(mappedBy = "genes", fetch = FetchType.LAZY)
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public Collection<SingleNucleotidePolymorphism> getSnps() {
        return snps;
    }

    public void setSnps(Collection<SingleNucleotidePolymorphism> snps) {
        this.snps = snps;
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
