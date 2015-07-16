package uk.ac.ebi.spot.goci.model;

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
    private SingleNucleotidePolymorphism snp;

    @ManyToOne
    private Gene gene;

    private String source;

    private String mappingMethod;

    // JPA no-args constructor
    public GenomicContext() {
    }

    public GenomicContext(Boolean isIntergenic,
                          Boolean isUpstream,
                          Boolean isDownstream,
                          Long distance,
                          SingleNucleotidePolymorphism snp, Gene gene, String source, String mappingMethod) {
        this.isIntergenic = isIntergenic;
        this.isUpstream = isUpstream;
        this.isDownstream = isDownstream;
        this.distance = distance;
        this.snp = snp;
        this.gene = gene;
        this.source = source;
        this.mappingMethod = mappingMethod;
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

    @Override public String toString() {
        return "GenomicContext{" +
                "id=" + id +
                ", isIntergenic=" + isIntergenic +
                ", isUpstream=" + isUpstream +
                ", isDownstream=" + isDownstream +
                ", distance=" + distance +
                ", snp=" + snp +
                ", gene=" + gene +
                ", source='" + source + '\'' +
                ", mappingMethod='" + mappingMethod + '\'' +
                '}';
    }
}
