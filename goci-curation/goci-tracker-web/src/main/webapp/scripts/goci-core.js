var restApiKey;

/**
 * Authenticates the current user based on the presence of a cookie in their browser.  You should supply callback
 * methods to this function determining what initialisation should be performed in the event of authentication
 * succeeding or failing.
 *
 * This function takes an object containing three callback functions named noUser, guestUser and knownUser.  You should
 * pass the relevant functions that should be invoked on each event.  For example, you may wish to display a login
 * dialog as the noUser callback function.  The guestUser and knownUser functions are passed a RestApiKey argument, if
 * present, whereas the noUser function is passed no arguments.
 *
 * Your call should look like:
 *
 * authenticate({error: myLoginFunction, success: myUserFunction});
 */
function authenticate(options) {
    // first visit?
    var priorVisit = getCookie("gociTracker");
    if (priorVisit == undefined) {
        // this is the users first visit, don't make them log in right away
        var expires = new Date();
        expires.setDate(2);
        expires.setMonth(0);
        expires.setFullYear(2058);
        setCookie("gociTracker", "", expires);
        loginGuest(requestPublications());
    }
    else {
        // read rest api key from cookie, if present
        restApiKey = undefined;
        var restApiKey = getCookie("gociTrackerRestApiKey");
        if (restApiKey == undefined) {
            // if we didn't find the restApiKey in a cookie, try to request a new one
            options.error();
        }
        else {
            // or verify the existing one
            verifyRestApiKey(restApiKey, options);
        }
    }
}

/**
 * Verifies the rest api key supplied is correct and can be used to identify a known user.  If the verification fails,
 * it triggers the deletion of the existing cookie and creation of a new rest api key (if possible).  If there is no
 * email for the user and the rest api key is invalid, make sure we show the login page.
 *
 * @param restApiKey
 */
function verifyRestApiKey(restApiKey, options) {
    $.getJSON('api/users/query?restApiKey=' + restApiKey, function(json) {
        if (json == undefined) {
            // the restApiKey we found in a cookie is not verified or out of date, so request a new key
            options.error();
        }
        else {
            // init
            loginUser(json, restApiKey, options.success);
        }
    });
}

/**
 * Callback function that request a users REST API key, given a json object representing a user. On success, the rest
 * api key will be set as a very long-lived cookie, so the user won't have to log in with their email every time.  An
 * alert will be raised if no rest api key could be retrieved or generated for this user.
 *
 * @param userJson
 */
function obtainRestApiKey(userJson, options) {
    // got a real user, send nested request for REST API key
    if (userJson.id == null || userJson.id == "") {
        // got a valid user without an ID, so must be a guest
        loginGuest(options.success);
    }
    else {
        $.getJSON('api/users/' + userJson.id + '/restApiKey', function(json) {
            if (json.restApiKey == undefined || json.restApiKey == "") {
                loginGuest(options.success);
            }
            else {
                loginUser(userJson, json.restApiKey, options.success);
            }
        });
    }
}

function loginGuest(callback) {
    // show the guest greeting at the top
    $("#goci-user-greeting").hide();
    $("#goci-guest-greeting").show();

    // and init as a guest
    callback(undefined)
}

function loginUser(userJson, restApiKey, callback) {
    // display the users name at the top
    $("#user-full-name").html(userJson.firstName + " " + userJson.surname);
    $("#goci-guest-greeting").hide();
    $("#goci-user-greeting").show();

    // set rest api cookie
    var expires = new Date();
    expires.setDate(2);
    expires.setMonth(0);
    expires.setFullYear(2058);
    setCookie("gociTrackerRestApiKey", restApiKey, expires);

    this.restApiKey = restApiKey;
    callback(restApiKey);
}


/**
 * Gets a cookie set on the users system with the supplied name
 *
 * @param cookieName the name of the cookie
 */
function getCookie(cookieName) {
    if (document.cookie.length > 0) {
        c_start = document.cookie.indexOf(cookieName + "=");
        if (c_start != -1) {
            c_start = c_start + cookieName.length + 1;
            c_end = document.cookie.indexOf(";", c_start);
            if (c_end == -1) {
                c_end = document.cookie.length;
            }
            return document.cookie.substring(c_start, c_end);
        }
    }
    return undefined;
}

function setCookie(cookieName, cookieValue, expires) {
    document.cookie =
        cookieName + "=" + cookieValue + ";" +
            "expires=" + expires + ";";
}

/**
 * Removes a cookie that has previously been set on the users system with the supplied name
 *
 * @param cookieName the name of the cookie to remove
 */
function removeCookie(cookieName) {
    if (getCookie(cookieName) != undefined) {
        var expires = new Date();
        setCookie(cookieName, "expired", expires);
    }
    return false;
}