package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p/>
 *         Abstract parent class for traits, as we have traits assigned at study (Disease trait and EFO trait)
 *         and snp level (EFO trait)
 */
@MappedSuperclass
public abstract class Trait {

    @Id
    @GeneratedValue
    private Long id;

    // JPA no-args constructor
    public Trait() {
    }

    protected Trait(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Trait{" +
                "id=" + id +
                '}';
    }
}
