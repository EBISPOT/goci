package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 03/02/15
 */
@Entity
public class GenomicContext {
    @Id
    @GeneratedValue
    private Long id;

    private Boolean isIntergenic;

    private Boolean isUpstream;

    private Boolean isDownstream;

    private Long distance;

    @ManyToOne
    @JsonBackReference
    private SingleNucleotidePolymorphism snp;

    @ManyToOne
    @JsonManagedReference
    private Gene gene;

    @ManyToOne
    private Location location;

    private String source;

    private String mappingMethod;

    private Boolean isClosestGene;

    // JPA no-args constructor
    public GenomicContext() {
    }

    public GenomicContext(Boolean isIntergenic,
                          Boolean isUpstream,
                          Boolean isDownstream,
                          Long distance,
                          SingleNucleotidePolymorphism snp,
                          Gene gene,
                          Location location,
                          String source,
                          String mappingMethod, Boolean isClosestGene) {
        this.isIntergenic = isIntergenic;
        this.isUpstream = isUpstream;
        this.isDownstream = isDownstream;
        this.distance = distance;
        this.snp = snp;
        this.gene = gene;
        this.location = location;
        this.source = source;
        this.mappingMethod = mappingMethod;
        this.isClosestGene = isClosestGene;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsIntergenic() {
        return isIntergenic;
    }

    public void setIsIntergenic(Boolean isIntergenic) {
        this.isIntergenic = isIntergenic;
    }

    public Boolean getIsUpstream() {
        return isUpstream;
    }

    public void setIsUpstream(Boolean isUpstream) {
        this.isUpstream = isUpstream;
    }

    public Boolean getIsDownstream() {
        return isDownstream;
    }

    public void setIsDownstream(Boolean isDownstream) {
        this.isDownstream = isDownstream;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public SingleNucleotidePolymorphism getSnp() {
        return snp;
    }

    public void setSnp(SingleNucleotidePolymorphism snp) {
        this.snp = snp;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public String getMappingMethod() {
        return mappingMethod;
    }

    public void setMappingMethod(String mappingMethod) {
        this.mappingMethod = mappingMethod;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getIsClosestGene() {
        return isClosestGene;
    }

    public void setIsClosestGene(Boolean isClosestGene) {
        this.isClosestGene = isClosestGene;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
