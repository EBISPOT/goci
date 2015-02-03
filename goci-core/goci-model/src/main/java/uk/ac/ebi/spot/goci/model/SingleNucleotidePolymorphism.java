package uk.ac.ebi.spot.goci.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

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
    private Long id;

    private String rsId;

    private String chromosomeName;

    private String chromosomePosition;

    private Long merged;

    private String functionalClass;

    private Date lastUpdateDate;

    @ManyToMany
    @JoinTable(name = "SNP_REGION",
            joinColumns = @JoinColumn(name = "SNP_ID"),
            inverseJoinColumns = @JoinColumn(name = "REGION_ID"))
    private Collection<Region> regions;

    @OneToMany(mappedBy = "snp")
    private Collection<GenomicContext> genomicContexts;

    // JPA no-args constructor
    public SingleNucleotidePolymorphism() {
    }

    public SingleNucleotidePolymorphism(String rsId,
                                        String chromosomeName,
                                        String chromosomePosition,
                                        Long merged,
                                        String functionalClass,
                                        Date lastUpdateDate,
                                        Collection<Region> regions) {
        this.rsId = rsId;
        this.chromosomeName = chromosomeName;
        this.chromosomePosition = chromosomePosition;
        this.merged = merged;
        this.functionalClass = functionalClass;
        this.lastUpdateDate = lastUpdateDate;
        this.regions = regions;
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

    public Long getMerged() {
        return merged;
    }

    public void setMerged(Long merged) {
        this.merged = merged;
    }

    public String getFunctionalClass() {
        return functionalClass;
    }

    public void setFunctionalClass(String functionalClass) {
        this.functionalClass = functionalClass;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Collection<Region> getRegions() {
        return regions;
    }

    public void setRegions(Collection<Region> regions) {
        this.regions = regions;
    }

    public Collection<GenomicContext> getGenomicContexts() {
        return genomicContexts;
    }

    public void setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        this.genomicContexts = genomicContexts;
    }

    @Override
    public String toString() {
        return "SingleNucleotidePolymorphism{" +
                "id=" + id +
                ", rsId='" + rsId + '\'' +
                ", chromosomeName='" + chromosomeName + '\'' +
                ", chromosomePosition='" + chromosomePosition + '\'' +
                ", merged=" + merged +
                ", functionalClass='" + functionalClass + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }
}
