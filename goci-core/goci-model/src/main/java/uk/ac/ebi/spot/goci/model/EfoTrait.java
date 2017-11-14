package uk.ac.ebi.spot.goci.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p>
 *         Model object representing an EFO trait which is normally assigned at the SNP level
 */

@Entity
public class EfoTrait {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String trait;

    @NotBlank
    private String uri;

    private String shortForm;

    @ManyToMany(mappedBy = "efoTraits")
    private Collection<Study> studies;

    @ManyToMany(mappedBy = "efoTraits")
    private Collection<Association> associations;

    // JPA no-args constructor
    public EfoTrait() {
    }

    public EfoTrait(String trait,
                    String uri,
                    String shortForm,
                    Collection<Study> studies,
                    Collection<Association> associations) {
        this.trait = trait;
        this.uri = uri;
        this.shortForm = shortForm;
        this.studies = studies;
        this.associations = associations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrait() {
        return trait;
    }

    public void setTrait(String trait) {
        this.trait = trait;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Collection<Study> getStudies() {
        return studies;
    }

    public void setStudies(Collection<Study> studies) {
        this.studies = studies;
    }

    public Collection<Association> getAssociations() {
        return associations;
    }

    public void setAssociations(Collection<Association> associations) {
        this.associations = associations;
    }

    public String getShortForm() {
        return shortForm;
    }

    public void setShortForm(String shortForm) {
        this.shortForm = shortForm;
    }
}
