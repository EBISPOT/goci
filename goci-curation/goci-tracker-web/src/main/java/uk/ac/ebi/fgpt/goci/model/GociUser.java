package uk.ac.ebi.fgpt.goci.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * A user of the GOCI Tracking system.  Each user is uniquely identified by their user ID, and users are essentially
 * unique by REST API key.  The same person may request multiple different REST API keys and will therefore have
 * multiple user accounts with the same email.  In order to carry out curation-type operations, the user must have a
 * permission level of at least {@link Permissions#CURATOR}.
 *
 * @author Tony Burdett
 * @date 26/10/11
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
public interface GociUser {
    /**
     * Get the unique ID assigned to this user.  All known users must have an ID, but for anonymous guest users this can
     * be null
     *
     * @return the user's unique ID
     */
    String getId();

    /**
     * Gets the username of this user
     *
     * @return the users name
     */
    String getUserName();

    /**
     * The first name of this user
     *
     * @return the user's first name
     */
    String getFirstName();

    /**
     * The surname of this user
     *
     * @return the user's surname
     */
    String getSurname();

    /**
     * Gets the email address of this user
     *
     * @return the users email address
     */
    String getEmail();

    /**
     * Get the REST API key this user has been assigned
     *
     * @return the user's REST API key, required for accessing protected functions
     */
    @JsonIgnore
    String getRestApiKey();

    /**
     * The permissions attributed to this user.  For authenticated users, this would normally be CURATOR unless special
     * priviledges are granted.
     *
     * @return the user's permissions
     */
    Permissions getPermissions();

    /**
     * Upgrade the permissions assigned to the user.  This will normally occur when additional user permissions have
     * been located and assigned to a user that previously had submitter, or guest, permissions.
     *
     * @param permissions the permissions to upgrade this user to
     */
    void upgradePermissions(Permissions permissions);

    /**
     * An enumeration of possible permissions a {@link GociUser} can have.  Users should generally have guest
     * permissions unless they are registered to the GOCI Tracking system
     */
    public enum Permissions {
        GUEST,
        CURATOR,
        ADMINISTRATOR
    }
}
