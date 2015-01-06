package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * Created by emma on 21/11/14.
 *
 * @author emma
 *         <p>
 *         Model object representing a single nucleotide polymorphisms and its attributes
 */

@Entity
public class SingleNucleotidePolymorphism {
    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    private String rsId;

    private String chromosomeName;

    private String chromosomePosition;

    private Timestamp lastUpdateDate;

    @OneToOne
    private Region region;

    @ManyToMany
    @JoinTable(name = "SNP_GENE",
               joinColumns = @JoinColumn(name = "SNP_ID"),
               inverseJoinColumns = @JoinColumn(name = "GENE_ID"))
    private Collection<Gene> genes;

    // JPA no-args constructor
    public SingleNucleotidePolymorphism() {
    }

    public SingleNucleotidePolymorphism(String rsId,
                                        String chromosomeName,
                                        String chromosomePosition,
                                        Timestamp lastUpdateDate,
                                        Region region,
                                        Collection<Gene> genes) {
        this.rsId = rsId;
        this.chromosomeName = chromosomeName;
        this.chromosomePosition = chromosomePosition;
        this.lastUpdateDate = lastUpdateDate;
        this.region = region;
        this.genes = genes;
    }

    public Long getId() {
        return id;
    }

    public String getRsId() {
        return rsId;
    }

    public String getChromosomeName() {
        return chromosomeName;
    }

    public String getChromosomePosition() {
        return chromosomePosition;
    }

    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    public Region getRegion() {
        return region;
    }

    public Collection<Gene> getGenes() {
        return genes;
    }


    @Override
    public String toString() {
        return "SingleNucleotidePolymorphism{" +
                "id=" + id +
                ", rsId='" + rsId + '\'' +
                ", chromosomeName='" + chromosomeName + '\'' +
                ", chromosomePosition='" + chromosomePosition + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                ", region=" + region +
                ", genes=" + genes +
                '}';
    }


}
