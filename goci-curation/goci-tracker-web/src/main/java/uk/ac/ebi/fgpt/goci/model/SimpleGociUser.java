package uk.ac.ebi.fgpt.goci.model;

/**
 * A basic implementation of a {@link GociUser}.
 *
 * @author Tony Burdett
 * @date 26/10/11
 */
public class SimpleGociUser implements GociUser {
    private String id;
    private String userName;
    private String firstName;
    private String surname;
    private String email;
    private String restApiKey;
    private Permissions permissions;

    public SimpleGociUser(String firstName,
                          String surname,
                          String email,
                          String restApiKey,
                          Permissions permissions) {
        this.userName = email.substring(0, email.indexOf("@"));
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.restApiKey = restApiKey;
        this.permissions = permissions;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getRestApiKey() {
        return restApiKey;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void upgradePermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleGociUser that = (SimpleGociUser) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (permissions != that.permissions) return false;
        if (restApiKey != null ? !restApiKey.equals(that.restApiKey) : that.restApiKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (restApiKey != null ? restApiKey.hashCode() : 0);
        result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
        return result;
    }
}
