package uk.ac.ebi.spot.goci.curation.model;

import org.hibernate.validator.constraints.NotEmpty;
import uk.ac.ebi.spot.goci.model.SecureRole;

import javax.validation.constraints.NotNull;

/**
 * Created by emma on 09/02/15.
 *
 * @author emma
 *         <p/>
 *         Model class used in curation system as a form to create users
 */
public class UserCreateForm {

    @NotEmpty
    private String email = "";

    @NotEmpty
    private String password = "";

    @NotEmpty
    private String passwordRepeated = "";

    @NotNull
    private SecureRole secureRole;

    public UserCreateForm() {
    }

    public UserCreateForm(String email, String password, String passwordRepeated, SecureRole secureRole) {
        this.email = email;
        this.password = password;
        this.passwordRepeated = passwordRepeated;
        this.secureRole = secureRole;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeated() {
        return passwordRepeated;
    }

    public void setPasswordRepeated(String passwordRepeated) {
        this.passwordRepeated = passwordRepeated;
    }

    public SecureRole getSecureRole() {
        return secureRole;
    }

    public void setSecureRole(SecureRole secureRole) {
        this.secureRole = secureRole;
    }
}
