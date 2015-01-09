package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p/>
 *         Model object representing an EFO trait which is normally assigned at the SNP level
 */

@Entity
@Table(name = "GWASEFOTRAITS")
public class EFOTrait {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "EFOTRAIT")
    private String trait;

    @Column(name = "EFOURI")
    private String uri;

    // JPA no-args constructor
    public EFOTrait() {
    }

    public EFOTrait(String trait, String uri) {
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
        return "EFOTrait{" +
                "id=" + id +
                ", trait='" + trait + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
