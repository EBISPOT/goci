package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 21/11/14.
 *
 * @author emma
 *         <p/>
 *         Model object representing a single nucleotide polymorphisms and its attributes
 */

@Entity
@Table(name = "GWASSNP")
public class SingleNucleotidePolymorphism {

    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID")
    private Long id;

    @Column(name = "SNP")
    private String rsID;

    // TODO ADD THESE ONCE TABLE IS ACTIVE
    @Column(name = "CHROMOSOME_NAME")
   private String chromosomeName;

    @Column(name = "CHROMOSOME_POS")
    private String chromosomePosition;


    @Column(name = "LASTUPDATEDATE")
    private Timestamp lastUpdateDate;

    // TODO HOW DO WE DEFINE RELATIONSHIP WITH GENE AND REGION
    // Associated region
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "GWASREGIONXREF",
            joinColumns = {@JoinColumn(name = "GWASSNPID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "REGIONID", referencedColumnName = "ID")}
    )
    private Region region;

    // Associated genes
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "GWASGENEXREF",
            joinColumns = {@JoinColumn(name = "GWASSNPID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "GENEID", referencedColumnName = "ID")}
    )
    private Collection<Gene> genes;

    // JPA no-args constructor
    public SingleNucleotidePolymorphism() {
    }

    public SingleNucleotidePolymorphism(String rsID, String chromosomeName, String chromosomePosition, Timestamp lastUpdateDate, Region region, Collection<Gene> genes) {
        this.rsID = rsID;
        this.chromosomeName = chromosomeName;
        this.chromosomePosition = chromosomePosition;
        this.lastUpdateDate = lastUpdateDate;
        this.region = region;
        this.genes = genes;
    }

    public Long getId() {
        return id;
    }

    public String getRsID() {
        return rsID;
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
                ", rsID='" + rsID + '\'' +
                ", chromosomeName='" + chromosomeName + '\'' +
                ", chromosomePosition='" + chromosomePosition + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                ", region=" + region +
                ", genes=" + genes +
                '}';
    }



}
