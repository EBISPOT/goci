package uk.ac.ebi.fgpt.goci.model;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * Date 28/10/11
 */
public class GuestUser implements GociUser {
    private final String email;
    private String ID;

    public GuestUser(String email) {
        this.email = email;
    }

    public String getUserName() {
        return email.substring(0, email.indexOf("@"));
    }

    public String getFirstName() {
        return "";
    }

    public String getSurname() {
        return "guest";
    }

    public String getEmail() {
        return email;
    }

    public String getRestApiKey() {
        return "";
    }

    public String getId() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public Permissions getPermissions() {
        return Permissions.GUEST;
    }

    public void upgradePermissions(Permissions permissions) {
        if (permissions != Permissions.GUEST) {
            // this isn't allowed
            throw new IllegalArgumentException("Cannot upgrade permissions of guest users - this requires a new key");
        }
    }

    public int compareTo(Object o) {
        if (o instanceof GociUser) {
            return getEmail().compareTo(((GociUser) o).getEmail());
        }
        else {
            throw new ClassCastException(o.getClass().getSimpleName() + " cannot be compared to " +
                                                 getClass().getSimpleName());
        }
    }

}
