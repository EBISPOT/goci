package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by emma on 09/02/15.
 *
 * @author emma
 *         <p>
 *         Model object representing user roles Current roles are admin, curator, submitter
 */
@Entity
public class SecureRole {
    @Id
    @GeneratedValue
    private Long id;

    private String role;

    // JPA no-args constructor
    public SecureRole() {
    }

    public SecureRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                '}';
    }
}
