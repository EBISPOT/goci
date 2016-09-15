package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    private Long merged;

    private String functionalClass;

    private Date lastUpdateDate;

    @ManyToMany
    @JoinTable(name = "SNP_LOCATION",
               joinColumns = @JoinColumn(name = "SNP_ID"),
               inverseJoinColumns = @JoinColumn(name = "LOCATION_ID"))
    private Collection<Location> locations;

    @OneToMany(mappedBy = "snp")
    private Collection<GenomicContext> genomicContexts;

    @OneToMany(mappedBy = "snp")
    private Collection<RiskAllele> riskAlleles;

    @ManyToOne
    @JoinTable(name = "SNP_MERGED_SNP",
               joinColumns = @JoinColumn(name = "SNP_ID_MERGED"),
               inverseJoinColumns = @JoinColumn(name = "SNP_ID_CURRENT"))
    private SingleNucleotidePolymorphism currentSNP;


    // JPA no-args constructor
    public SingleNucleotidePolymorphism() {
    }

    public SingleNucleotidePolymorphism(String rsId,
                                        Long merged,
                                        String functionalClass,
                                        Date lastUpdateDate,
                                        Collection<Location> locations,
                                        Collection<GenomicContext> genomicContexts,
                                        Collection<RiskAllele> riskAlleles) {
        this.rsId = rsId;
        this.merged = merged;
        this.functionalClass = functionalClass;
        this.lastUpdateDate = lastUpdateDate;
        this.locations = locations;
        this.genomicContexts = genomicContexts;
        this.riskAlleles = riskAlleles;
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

    public Long getMerged() { return merged; }

    public void setMerged(Long merged) {
        this.merged = merged;
    }

    public SingleNucleotidePolymorphism getCurrentSnp() { return currentSNP; }

    public void setCurrentSnp(SingleNucleotidePolymorphism currentSNP) { this.currentSNP = currentSNP; }

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

    public Collection<Location> getLocations() {
        return locations;
    }

    public void setLocations(Collection<Location> locations) {
        this.locations = locations;
    }

    public Collection<GenomicContext> getGenomicContexts() {
        return genomicContexts;
    }

    public void setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        this.genomicContexts = genomicContexts;
    }

    public Collection<RiskAllele> getRiskAlleles() {
        return riskAlleles;
    }

    public void setRiskAlleles(Collection<RiskAllele> riskAlleles) {
        this.riskAlleles = riskAlleles;
    }
}
