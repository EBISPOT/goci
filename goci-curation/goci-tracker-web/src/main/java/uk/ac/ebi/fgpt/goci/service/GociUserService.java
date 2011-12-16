package uk.ac.ebi.fgpt.goci.service;

import uk.ac.ebi.fgpt.goci.model.GociUser;

import java.util.Collection;

/**
 * A service that can be used to generate or retrieve users from supplied input parameters.  Normally, the details for
 * each user will be recovered from some persistant configuration and includes the permissions this user has and their
 * name and email
 *
 * @author Tony Burdett
 * @date 26/10/11
 */
public interface GociUserService {
    /**
     * Creates a new user given the users username, first name, surname, email address and permissions.  A unique REST
     * API key for this user will be generated.  Note that users are only unique by user ID and REST API key, so the
     * same person can have multiple user accounts with the same email address and username, if required.
     *
     * @param firstName    the users first name
     * @param surname      the users surname
     * @param emailAddress the users email address
     * @param permissions  the permissions to assign this user
     * @return the newly created user
     */
    GociUser createNewUser(String firstName,
                           String surname,
                           String emailAddress,
                           GociUser.Permissions permissions);

    /**
     * Gets a user with the given user ID.  If there is no user with this ID, this will return null.
     *
     * @param userID the users unique ID
     * @return the user with this ID
     */
    GociUser getUser(String userID);

    /**
     * Gets all users known to Goci.  This does not include any anonymous guest users
     *
     * @return all known Goci users
     */
    Collection<GociUser> getUsers();

    /**
     * Gets a {@link GociUser} given a username.  If there is no user with this username, this will return null.  As
     * multiple users can have the same username, if this is the case this will return one of the users with this
     * username, and makes no guarantees about which user this will be.  If you require to obtain one specific user you
     * should use the unique fields user ID or REST API key.
     *
     * @param userName the user name for a known user
     * @return the GociUser object that describes this user
     * @throws IllegalArgumentException if the username is not a known username
     */
    GociUser getUserByUserName(String userName) throws IllegalArgumentException;

    /**
     * Gets a {@link uk.ac.ebi.fgpt.goci.model.GociUser} given their email address. If there is no user with this
     * username known, this returns an anonymous guest user.  As multiple users can have the same email address, if this
     * is the case this will return one of the users with this email address, and makes no guarantees about which user
     * this will be.  If you require to obtain one specific user you should use the unique fields user ID or REST API
     * key.
     *
     * @param userEmailAddress the email address of the user to recover
     * @return the GociUser object that describes this user
     */
    GociUser getUserByEmail(String userEmailAddress);

    /**
     * Gets a {@link uk.ac.ebi.fgpt.goci.model.GociUser} given a <code>String</code> rest api key used to access this
     * service. If there is no user with this rest api key known, something may have gone wrong with the key generation
     * process or else the user has tried to submit a bogus REST API Key.  In this case, an {@link
     * IllegalArgumentException} will be thrown.
     *
     * @param restApiKey the rest api key used to access this a service
     * @return the object that describes this user
     * @throws IllegalArgumentException if the rest api key is not known
     */
    GociUser getUserByRestApiKey(String restApiKey) throws IllegalArgumentException;

    /**
     * Updates the email address assigned to the supplied user.  The user supplied should already exist in this service:
     * existence is checked by searching for a user with the same ID.  You should therefore never update user ID
     * fields.
     *
     * @param existingUser the user to update
     * @param newEmail     the new email address to assign to this user
     * @return the reference to the newly saved user
     * @throws IllegalArgumentException if the user supplied does not exist
     */
    GociUser updateUserEmail(GociUser existingUser, String newEmail) throws IllegalArgumentException;

}
