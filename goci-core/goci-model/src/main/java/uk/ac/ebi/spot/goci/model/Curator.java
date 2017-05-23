package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p>
 *         Model object representing a curator and its attributes
 */
@Entity
public class Curator {
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String userName;

    @OneToMany(mappedBy = "curator")
    @JsonIgnore
    private Collection<Note> notes;

    // JPA no-args constructor
    public Curator() {
    }

    public Curator(Long id, String firstName, String lastName, String email, String userName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
