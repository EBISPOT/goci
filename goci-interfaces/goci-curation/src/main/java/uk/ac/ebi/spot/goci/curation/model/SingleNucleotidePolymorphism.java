package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    // TODO HOW DO WE DEFINE RELATIONSHIP WITH GENE AND REGION
    // Associated region
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "GWASREGIONXREF",
//            joinColumns = {@JoinColumn(name = "GWASSNPID", referencedColumnName = "ID")},
//            inverseJoinColumns = {@JoinColumn(name = "REGIONID", referencedColumnName = "ID")}
//    )
    @OneToOne
    private Region region;

    // Associated genes
//    @ManyToMany
//    @JoinTable(
//            name = "GWASGENEXREF",
//            joinColumns = {@JoinColumn(name = "GWASSNPID", referencedColumnName = "ID")},
//            inverseJoinColumns = {@JoinColumn(name = "GENEID", referencedColumnName = "ID")}
//    )
    @ManyToMany
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
