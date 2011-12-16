package uk.ac.ebi.fgpt.goci.web.view;

/**
 * A bean that encapsulates the information required to sign up a new user to the GOCI tracking system.
 *
 * @author Tony Burdett
 * @date 12/12/11
 */
public class UserRequestBean {
    private String firstName;
    private String surname;
    private String email;

    private UserRequestBean() {
    }

    public UserRequestBean(String firstName, String surname, String email) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
