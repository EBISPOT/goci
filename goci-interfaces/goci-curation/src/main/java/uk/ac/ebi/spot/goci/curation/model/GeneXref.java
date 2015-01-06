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
public class GeneXref {

    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    private Long geneId;

    private Long associationId;

    // JPA no-args constructor
    public GeneXref() {
    }

    public GeneXref(Long geneId, Long associationId) {
        this.geneId = geneId;
        this.associationId = associationId;
    }

    public Long getId() {
        return id;
    }

    public Long getGeneId() {
        return geneId;
    }

    public Long getAssociationId() {
        return associationId;
    }

    @Override
    public String toString() {
        return "GeneXref{" +
                "id=" + id +
                ", geneId='" + geneId + '\'' +
                ", associationId='" + associationId + '\'' +
                '}';
    }
}
