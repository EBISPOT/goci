package uk.ac.ebi.fgpt.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import uk.ac.ebi.fgpt.goci.dao.GociUserDAO;
import uk.ac.ebi.fgpt.goci.model.GociUser;
import uk.ac.ebi.fgpt.goci.model.GuestUser;
import uk.ac.ebi.fgpt.goci.model.SimpleGociUser;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

/**
 * A default implementation of a user service that can be supplied with a {@link uk.ac.ebi.fgpt.goci.dao.GociUserDAO} in
 * order to retrieve users from an underlying datasource.
 *
 * @author Tony Burdett
 * Date 27/10/11
 */
public class DefaultGociUserService implements GociUserService {

    private GociUserDAO userDAO;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public GociUserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(GociUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public GociUser createNewUser(String firstName,
                                  String surname,
                                  String emailAddress,
                                  GociUser.Permissions permissions) {
        getLog().debug("Creating new user for " + emailAddress);
        if (permissions == GociUser.Permissions.GUEST) {
            return new SimpleGociUser(firstName,
                    surname,
                    emailAddress,
                    "",
                    permissions);
        } else {
            // first, create a rest api key for our user
            String restApiKey = generateRestApiKey(emailAddress);

            // now, we need to make a new user with permissions
            SimpleGociUser permissibleUser =
                    new SimpleGociUser(firstName,
                            surname,
                            emailAddress,
                            restApiKey,
                            permissions);

            getLog().debug("Generated new user! Details are:\n" +
                    "username: " + permissibleUser.getUserName() + "\n" +
                    "name: " + permissibleUser.getFirstName() + " " + permissibleUser.getSurname() +
                    "\n" +
                    "email: " + permissibleUser.getEmail() + "\n" +
                    "restApiKey: " + permissibleUser.getRestApiKey());

            return getUserDAO().saveUser(permissibleUser);
        }
    }

    /**
     * Gets the user with this ID, that is known to Goci.  This service treats the user ID from the trusted DAO as the
     * canonical ID - users IDs from the verification DAO are not used.  As such, if you request a user from the
     * verification DAO, that has never logged into Goci, you should expect to get null here.
     *
     * @param userID the users unique ID
     * @return the known user, that has previously logged into goci
     */
    public GociUser getUser(String userID) {
        return getUserDAO().getUser(userID);
    }

    /**
     * Gets all known users in Goci.  This default implementation only returns users that have, at some point, logged
     * into Goci, and are therefore stored in the trusted DAO.  Many users present in the verification DAO will not show
     * up here.
     *
     * @return all users that have previously logged into Goci
     */
    public Collection<GociUser> getUsers() {
        return getUserDAO().getUsers();
    }

    /**
     * Gets the user with this user name.  This default implementation uses two DAOs to obtain users, one trusted source
     * (which is normally a backing database for Goci) and one verification source (which could be an LDAP directory or
     * some other listing of "allowed" users).  If the user is not available from the trusted source, they are checked
     * against the verification source and if found here, automatically added to the trusted source.  A REST API key for
     * that user is also automatically generated.
     *
     * @param userName the user name for this user, which must at least be present in the verification datasource
     * @return a user from the trusted datasource, automatically added if needs be
     */
    public GociUser getUserByUserName(String userName) {
        // is this user in our database?
        Collection<GociUser> targetUsers = getUserDAO().getUserByUserName(userName);
        if (targetUsers.isEmpty()) {
            throw new IllegalArgumentException("No user with the username '" + userName + "' found");
        } else {
            return targetUsers.iterator().next();
        }
    }

    /**
     * Gets the user with this email address.  This default implementation uses two DAOs to obtain users, one trusted
     * source (which is normally a backing database for Goci) and one verification source (which could be an LDAP
     * directory or some other listing of "allowed" users).  If the user is not available from the trusted source, they
     * are checked against the verification source and if found here, automatically added to the trusted source.  A REST
     * API key for that user is also automatically generated.
     *
     * @param userEmailAddress the email address for this user, which must at least be present in the verification
     *                         datasource
     * @return a user from the trusted datasource, automatically added if needs be
     */
    public GociUser getUserByEmail(String userEmailAddress) {
        // is this user in our database?
        Collection<GociUser> targetUsers = getUserDAO().getUserByEmail(userEmailAddress);
        if (targetUsers.isEmpty()) {
            return new GuestUser(userEmailAddress);
        } else {
            // if there are several users with the same email, iterate over them...
            return targetUsers.iterator().next();
        }
    }

    /**
     * Gets the user with this REST API key.  Unlike the other methods on this service implementation, users are never
     * automatically added by REST API key - this MUST be correct, checked against the trusted DAO.  If there is no user
     * in the trusted DAO with this key, an {@link IllegalArgumentException} will be thrown.
     *
     * @param restApiKey the rest api key used to access this a service
     * @return the user with this REST API key, if found
     */
    public GociUser getUserByRestApiKey(String restApiKey) {
        // only query the database - ldap doesn't store rest api keys, so if it's not in the DB it's bogus
        try {
            GociUser result = getUserDAO().getUserByRestApiKey(restApiKey);
            if (result != null) {
                return result;
            } else {
                throw new IllegalArgumentException(
                        "No user with this REST API key (" + restApiKey + ") could be found");
            }
        } catch (EmptyResultDataAccessException e) {
            // no user, special case
            getLog().warn("Invalid REST API key - no user found");
            throw new IllegalArgumentException("Invalid REST API key", e);
        } catch (IncorrectResultSizeDataAccessException e) {
            // already caught no user, so this must be >1 users with the same REST API key
            getLog().error(
                    "UserDAO returned an invalid result: REST API keys must be unique, " +
                            "but " + e.getActualSize() + " users share this key");
            throw new IllegalArgumentException(
                    "REST API keys must be unique, but " + e.getActualSize() + " users share this key", e);
        }
    }

    public GociUser updateUserEmail(GociUser existingUser, String newEmail) {
        getLog().debug("Updating user email from '" + existingUser.getEmail() + "' to '" + newEmail + "'");
        GociUser fetchedUser = getUserDAO().getUser(existingUser.getId());
        if (fetchedUser == null) {
            throw new IllegalArgumentException("The supplied user does not exist: " +
                    "no user with matching ID (" + existingUser.getId() + ") found");
        } else {
            // create a new user that is basically a copy of the old one
            SimpleGociUser newUser = new SimpleGociUser(fetchedUser.getFirstName(),
                    fetchedUser.getSurname(),
                    newEmail,
                    fetchedUser.getRestApiKey(),
                    fetchedUser.getPermissions());
            // set new user id - as long as this equals the old ID, user will be updated
            newUser.setId(fetchedUser.getId());
            // now save
            return getUserDAO().saveUser(newUser);
        }
    }

    protected GociUser storeNewUser(GociUser user) throws UnsupportedOperationException {
        getLog().debug("Storing new user " + user.getUserName());
        // first, create a rest api key for our user
        String restApiKey = generateRestApiKey(user.getEmail());

        // now, we need to make a new user with permissions
        SimpleGociUser simpleUser;
        if (user.getEmail().equals("tburdett@ebi.ac.uk")) {
            // todo - backdoor for testing, to grant admin privileges to me
            simpleUser =
                    new SimpleGociUser(user.getFirstName(),
                            user.getSurname(),
                            user.getEmail(),
                            restApiKey,
                            GociUser.Permissions.ADMINISTRATOR);
        } else {
            simpleUser =
                    new SimpleGociUser(user.getFirstName(),
                            user.getSurname(),
                            user.getEmail(),
                            restApiKey,
                            user.getPermissions());
        }

        getLog().debug("Generated new user! Details are:\n" +
                "username: " + simpleUser.getUserName() + "\n" +
                "name: " + simpleUser.getFirstName() + " " + simpleUser.getSurname() +
                "\n" +
                "email: " + simpleUser.getEmail() + "\n" +
                "restApiKey: " + simpleUser.getRestApiKey() + "\n" +
                "permissions: " + simpleUser.getPermissions());

        return getUserDAO().saveUser(simpleUser);
    }

    protected String generateRestApiKey(String email) {
        String timestamp = Long.toString(System.currentTimeMillis());
        getLog().debug("Generating new REST API key for " + email);
        String keyContent = email + timestamp;
        try {
            // encode the email using SHA-1
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] digest = messageDigest.digest(keyContent.getBytes("UTF-8"));

            // now translate the resulting byte array to hex
            String restKey = getHexRepresentation(digest);
            getLog().debug("REST API key was generated: " + restKey);
            return restKey;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available, required to generate REST api key");
        }
    }

    protected String getHexRepresentation(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final String hexes = "0123456789ABCDEF";
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(hexes.charAt((b & 0xF0) >> 4)).append(hexes.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public void addStandardUsers() {
        if (getUserDAO().getUserByEmail("tburdett@ebi.ac.uk").isEmpty()) {
            getUserDAO().saveUser(new SimpleGociUser("Tony",
                    "Burdett",
                    "tburdett@ebi.ac.uk",
                    generateRestApiKey("tburdett@ebi.ac.uk"),
                    GociUser.Permissions.CURATOR));
        }
    }
}
