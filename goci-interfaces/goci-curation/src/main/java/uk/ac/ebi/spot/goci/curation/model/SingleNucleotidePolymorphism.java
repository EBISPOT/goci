package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

    @ManyToMany
    @JoinTable(name = "SNP_REGION",
               joinColumns = @JoinColumn(name = "SNP_ID"),
               inverseJoinColumns = @JoinColumn(name = "REGION_ID"))
    private Collection<Region> regions;

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
                                        Collection<Region> regions,
                                        Collection<Gene> genes) {
        this.rsId = rsId;
        this.chromosomeName = chromosomeName;
        this.chromosomePosition = chromosomePosition;
        this.lastUpdateDate = lastUpdateDate;
        this.regions = regions;
        this.genes = genes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRsId() {
        return rsId;
    }

    public void setRsId(String rsId) {
        this.rsId = rsId;
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

    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Collection<Region> getRegions() {
        return regions;
    }

    public void setRegions(Collection<Region> regions) {
        this.regions = regions;
    }

    public Collection<Gene> getGenes() {
        return genes;
    }

    public void setGenes(Collection<Gene> genes) {
        this.genes = genes;
    }

    @Override
    public String toString() {
        return "SingleNucleotidePolymorphism{" +
                "id=" + id +
                ", rsId='" + rsId + '\'' +
                ", chromosomeName='" + chromosomeName + '\'' +
                ", chromosomePosition='" + chromosomePosition + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                ", regions=" + regions +
                ", genes=" + genes +
                '}';
    }
}
