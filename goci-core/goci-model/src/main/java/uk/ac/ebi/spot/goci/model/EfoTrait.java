package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

    private String trait;

    private String uri;

    // JPA no-args constructor
    public EfoTrait() {
    }

    public EfoTrait(String trait, String uri) {
        this.trait = trait;
        this.uri = uri;
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

    @Override
    public String toString() {
        return "EfoTrait{" +
                "id=" + id +
                ", trait='" + trait + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
