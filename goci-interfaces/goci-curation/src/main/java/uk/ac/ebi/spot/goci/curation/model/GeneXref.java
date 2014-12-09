package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p/>
 *         Model of gene cross reference table
 */

@Entity
@Table(name = "GWASGENEXREF")
public class GeneXref {

    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID")
    private Long id;

    @Column(name = "GENEID")
    private Long geneID;

    @Column(name = "GWASSSNPID")
    private Long associationID;

    // JPA no-args constructor
    public GeneXref() {
    }

    public GeneXref(Long geneID, Long associationID) {
        this.geneID = geneID;
        this.associationID = associationID;
    }

    public Long getId() {
        return id;
    }

    public Long getGeneID() {
        return geneID;
    }

    public Long getAssociationID() {
        return associationID;
    }

    @Override
    public String toString() {
        return "GeneXref{" +
                "id=" + id +
                ", geneID='" + geneID + '\'' +
                ", associationID='" + associationID + '\'' +
                '}';
    }
}
