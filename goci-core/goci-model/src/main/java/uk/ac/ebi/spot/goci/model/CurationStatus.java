package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p>
 *         Model object representing status assigned to studies.
 */

@Entity
public class CurationStatus {
    @Id
    @GeneratedValue
    private Long id;

    private String status;

    // JPA no-args constructor
    public CurationStatus() {
    }

    public CurationStatus(Long id, String status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    //Setters used in testing
    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}