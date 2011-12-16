package uk.ac.ebi.fgpt.goci.dao;

import uk.ac.ebi.fgpt.goci.model.GociUser;

import java.util.Collection;

/**
 * A DAO interface for accessing Users from some underlying Datasource
 *
 * @author Tony Burdett
 * @date 27/10/11
 */
public interface GociUserDAO {
    /**
     * Gets a {@link GociUser} given the user ID.
     *
     * @param userID the user ID of the user to retrieve
     * @return the goci user with this ID
     */
    GociUser getUser(String userID);

    /**
     * Gets all {@link uk.ac.ebi.fgpt.goci.model.GociUser}s known to the system.
     *
     * @return all users Goci knows about
     */
    Collection<GociUser> getUsers();

    /**
     * Gets a {@link uk.ac.ebi.fgpt.goci.model.GociUser} given a <code>String</code> rest api key used to access this
     * service.  This returns a single user as rest api keys are unique per user
     *
     * @param restApiKey the restAPIKey used to access this a service
     * @return the object that describes this user
     */
    GociUser getUserByRestApiKey(String restApiKey);

    /**
     * Gets a {@link uk.ac.ebi.fgpt.goci.model.GociUser} given their username.  Often, the username will be the same
     * as the local part of the users email address.  This returns a collection, as many users can have the same
     * username.
     *
     * @param userName the user name for a known user
     * @return the GociUser object that describes this user
     */
    Collection<GociUser> getUserByUserName(String userName);

    /**
     * Gets a {@link uk.ac.ebi.fgpt.goci.model.GociUser} given their email address.  This returns a collection as
     * potentially, many users may have the same email address
     *
     * @param userEmailAddress the email address of the user to recover
     * @return the GociUser object that describes this user
     */
    Collection<GociUser> getUserByEmail(String userEmailAddress);

    /**
     * Persists new users to the backing datasource.  Generally, after creating new {@link
     * uk.ac.ebi.fgpt.goci.model.GociUser} you should save it with this method and then use the returned reference
     * instead of the original object: this allows some implementations of this interface to rereference equal users to
     * the existing objects.
     *
     * @param user the user to save
     * @return a reference to the (potentially modified) version of the user being saved
     * @throws IllegalArgumentException if the user supplied cannot be saved because it is an illegal type
     */
    GociUser saveUser(GociUser user) throws IllegalArgumentException;
}
