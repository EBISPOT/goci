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

    // JPA no-args constructor
    public GenomicContext() {
    }

    public GenomicContext(boolean isIntergenic,
                          boolean isUpstream,
                          boolean isDownstream,
                          Long distance,
                          SingleNucleotidePolymorphism snp, Gene gene) {
        this.isIntergenic = isIntergenic;
        this.isUpstream = isUpstream;
        this.isDownstream = isDownstream;
        this.distance = distance;
        this.snp = snp;
        this.gene = gene;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isIntergenic() {
        return isIntergenic;
    }

    public void setIntergenic(boolean isIntergenic) {
        this.isIntergenic = isIntergenic;
    }

    public boolean isUpstream() {
        return isUpstream;
    }

    public void setUpstream(boolean isUpstream) {
        this.isUpstream = isUpstream;
    }

    public boolean isDownstream() {
        return isDownstream;
    }

    public void setDownstream(boolean isDownstream) {
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

    @Override public String toString() {
        return "GenomicContext{" +
                "id=" + id +
                ", isIntergenic=" + isIntergenic +
                ", isUpstream=" + isUpstream +
                ", isDownstream=" + isDownstream +
                ", distance=" + distance +
                ", snp=" + snp +
                ", gene=" + gene +
                '}';
    }
}
