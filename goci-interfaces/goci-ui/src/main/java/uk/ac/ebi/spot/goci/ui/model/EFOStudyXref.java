package uk.ac.ebi.spot.goci.ui.model;

import javax.persistence.*;

/**
 * Created by emma on 08/12/14.
 *
 * @author emma
 *         <p/>
 *         Model of EFO study cross reference table
 */
@Entity
@Table(name = "GWASEFOSTUDYXREF")
public class EFOStudyXref {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "TRAITID")
    private Long traitID;

    @Column(name = "STUDYID")
    private Long studyID;

    // JPA no-args constructor
    public EFOStudyXref() {

    }

    public EFOStudyXref(Long traitID, Long studyID) {
        this.traitID = traitID;
        this.studyID = studyID;
    }

    public Long getId() {
        return id;
    }

    public Long getTraitID() {
        return traitID;
    }

    public Long getStudyID() {
        return studyID;
    }

    @Override
    public String toString() {
        return "EFOStudyXref{" +
                "id=" + id +
                ", traitID=" + traitID +
                ", studyID=" + studyID +
                '}';
    }
}
