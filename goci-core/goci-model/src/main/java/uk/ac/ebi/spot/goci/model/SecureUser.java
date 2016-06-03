package uk.ac.ebi.spot.goci.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Collection;

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

    @OneToMany(mappedBy = "user")
    private Collection<Event> events;

    // JPA no-args constructor
    public SecureUser() {
    }

    public SecureUser(String email,
                      String passwordHash,
                      SecureRole role,
                      Collection<Event> events) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.events = events;
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

    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }
}
