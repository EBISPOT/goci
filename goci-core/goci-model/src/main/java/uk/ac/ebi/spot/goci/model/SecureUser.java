package uk.ac.ebi.spot.goci.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by emma on 09/02/15.
 *
 * @author emma
 *         <p>
 *         Model object representing a user
 */
@Entity
public class SecureUser {

    @Id
    @GeneratedValue
    private Long id;

    // Email must be unique
    @Column(unique = true)
    private String email;

    private String passwordHash;

    @OneToOne
    private SecureRole role;

    // JPA no-args constructor
    public SecureUser() {
    }

    public SecureUser(String email, String passwordHash, SecureRole secureRole) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = secureRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public SecureRole getRole() {
        return role;
    }

    public void setRole(SecureRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "SecureUser{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }
}
