package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p/>
 *         Model object representing status assigned to studies
 */


@Entity
public class CurationStatus {
    @Id
    @GeneratedValue
    private Long id;

    private String status;

    private String seqnbr;

    // JPA no-args constructor
    public CurationStatus() {
    }

    public CurationStatus(Long id, String curationStatus, String seqnbr) {
        this.id = id;
        this.status = curationStatus;
        this.seqnbr = seqnbr;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getSeqnbr() {
        return seqnbr;
    }

    @Override
    public String toString() {
        return "CurationStatus{" +
                "id=" + id +
                ", curationStatus='" + status + '\'' +
                ", seqnbr='" + seqnbr + '\'' +
                '}';
    }
}
