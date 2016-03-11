package uk.ac.ebi.spot.goci.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

/**
* Created by dwelter on 08/03/16.
*/
public class Platform {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String platform;

    @ManyToMany(mappedBy = "platform")
    private Collection<Study> studies;

    // JPA no-args constructor
    public Platform() {
    }

    public Platform(String platform,
                    Collection<Study> studies) {
        this.platform = platform;
        this.studies = studies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Collection<Study> getStudies() {
        return studies;
    }

    public void setStudies(Collection<Study> studies) {
        this.studies = studies;
    }

}
