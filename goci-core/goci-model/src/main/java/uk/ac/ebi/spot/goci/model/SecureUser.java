package uk.ac.ebi.spot.goci.model;

import javax.persistence.*;

/**
 * Created by emma on 09/02/15.
 * @author emma
 *
 * Model object representing a user
 */
@Entity
public class SecureUser {

    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String passwordHash;

    @OneToOne
    private Role role;

    // JPA no-args constructor
    public SecureUser() {
    }

    public SecureUser(String email, String passwordHash, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", role=" + role +
                '}';
    }
}
