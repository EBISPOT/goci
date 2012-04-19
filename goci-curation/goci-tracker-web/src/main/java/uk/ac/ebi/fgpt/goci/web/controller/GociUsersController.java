package uk.ac.ebi.fgpt.goci.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.fgpt.goci.exception.UserCreationException;
import uk.ac.ebi.fgpt.goci.model.GociUser;
import uk.ac.ebi.fgpt.goci.service.GociUserService;
import uk.ac.ebi.fgpt.goci.web.view.RestApiKeyResponseBean;
import uk.ac.ebi.fgpt.goci.web.view.UserRequestBean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;

/**
 * A POJO controller class designed to function in the spring web-mvc framework.  This class contains some logic to
 * unwrap particular simple requests, retrieving {@link uk.ac.ebi.fgpt.goci.model.GociUser}s as appropriate.  Otherwise,
 * it functions basically as a means to delegate calls to underlying service implementations.
 *
 * @author Tony Burdett
 * Date 28/10/11
 */
@Controller
@RequestMapping("/users")
public class GociUsersController {
    private GociUserService userService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public GociUserService getUserService() {
        return userService;
    }

    @Autowired
    public void setUserService(GociUserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody GociUser createUser(@RequestBody UserRequestBean userRequestBean)
            throws UserCreationException {
        getLog().debug("Request to create a new user received");
        if (getUserService().getUserByEmail(userRequestBean.getEmail()).getId() == null) {
            return getUserService().createNewUser(userRequestBean.getFirstName(),
                                                  userRequestBean.getSurname(),
                                                  userRequestBean.getEmail(),
                                                  GociUser.Permissions.CURATOR);
        }
        else {
            getLog().error("Failed to create new user; found an existing, non-guest user with email " + userRequestBean.getEmail());
            throw new UserCreationException(
                    "Unable to create new user: user with email '" + userRequestBean.getEmail() + "' already exists");
        }
    }

    @RequestMapping(value = "/{userID}", method = RequestMethod.GET)
    public @ResponseBody GociUser getUser(@PathVariable String userID) {
        return getUserService().getUser(userID);
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Collection<GociUser> getUsers() {
        return getUserService().getUsers();
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public @ResponseBody GociUser searchForUser(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "restApiKey", required = false) String restApiKey) {
        if (restApiKey != null) {
            return getUserByRestApiKey(restApiKey);
        }
        else if (email != null) {
            return getUserByEmail(email);
        }
        else {
            throw new IllegalArgumentException("Must supply at least the email address of the user to query for");
        }
    }

    /**
     * Gets the {@link GociUser} with this email address by delegating to a {@link uk.ac.ebi.fgpt.goci.service.GociUserService}.
     * The normal strategy is to create new users if the email has not been seen before, subject to a verification
     * operation.  If verification fails, this will normally return an anonymous guest user.
     *
     * @param email the email address the user is logging in with
     * @return a simple bean wrapping the user's rest api key
     */
    public GociUser getUserByEmail(String email) {
        getLog().debug("Attempting to acquire user with email " + email);
        try {
            String decodedEmail = URLDecoder.decode(email, "UTF-8");
            return getUserService().getUserByEmail(decodedEmail);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding should be supported, but wasn't.  JVM configuration error?", e);
        }
    }

    /**
     * Gets the {@link GociUser} that has been assigned this restApiKey by delegating to a {@link
     * uk.ac.ebi.fgpt.goci.service.GociUserService}.  This is generally a verification method, to confirm that you can
     * retrieve the correct user given a rest api key.  If the rest api key is wrong or out of date, this method will
     * return null.
     *
     * @param restApiKey the rest api key that identifies the user we want
     * @return the user with this key, or null if there is none
     */
    public GociUser getUserByRestApiKey(String restApiKey) {
        getLog().debug("Attempting to acquire user with rest api key " + restApiKey);
        try {
            return getUserService().getUserByRestApiKey(restApiKey);
        }
        catch (IllegalArgumentException e) {
            getLog().debug("No such user for REST API key " + restApiKey);
            return null;
        }
    }

    @RequestMapping(value = "/{userID}/restApiKey", method = RequestMethod.GET)
    public @ResponseBody RestApiKeyResponseBean getRestApiKeyForUser(@PathVariable String userID) {
        getLog().debug("Requesting REST API key for user " + userID);
        GociUser user = getUserService().getUser(userID);
        getLog().debug("Got user " + user.getEmail());
        return new RestApiKeyResponseBean(user);
    }
}
