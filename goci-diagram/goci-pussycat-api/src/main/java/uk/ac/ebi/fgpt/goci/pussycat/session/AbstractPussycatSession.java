package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.pussycat.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * An abstract pussycat session that simply provides session ID definitions
 *
 * @author Tony Burdett
 * @date 03/08/12
 */
public abstract class AbstractPussycatSession implements PussycatSession {
    private String sessionID;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected AbstractPussycatSession() {
        this.sessionID = generateSessionID();
    }

    protected Logger getLog() {
        return log;
    }

    @Override public String getSessionID() {
        return sessionID;
    }

    private String generateSessionID() {
        String timestamp = Long.toString(System.currentTimeMillis());
        getLog().debug("Generating new session ID for session created at " + timestamp);
        try {
            // encode the email using SHA-1
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] digest = messageDigest.digest(timestamp.getBytes("UTF-8"));

            // now translate the resulting byte array to hex
            String restKey = StringUtils.getHexRepresentation(digest);
            getLog().debug("Session ID was generated: " + restKey);
            return restKey;
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available, required to generate session ID");
        }
    }
}
