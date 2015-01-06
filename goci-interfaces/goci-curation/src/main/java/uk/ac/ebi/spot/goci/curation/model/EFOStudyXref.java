package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;

/**
 * Created by emma on 08/12/14.
 *
 * @author emma
 *         <p/>
 *         Model of EFO study cross reference table
 */
@Entity
public class EFOStudyXref {

    @Id
    @GeneratedValue
    private Long id;

    private Long traitId;

    private Long studyId;

    // JPA no-args constructor
    public EFOStudyXref() {

    }

    public EFOStudyXref(Long traitId, Long studyId) {
        this.traitId = traitId;
        this.studyId = studyId;
    }

    public Long getId() {
        return id;
    }

    public Long getTraitId() {
        return traitId;
    }

    public Long getStudyId() {
        return studyId;
    }

    @Override
    public String toString() {
        return "EFOStudyXref{" +
                "id=" + id +
                ", traitID=" + traitId +
                ", studyId=" + studyId +
                '}';
    }
}
