package uk.ac.ebi.spot.goci.pussycat.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.spot.goci.pussycat.utils.StringUtils;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceLoader;

/**
 * An abstract pussycat session that simply provides session ID definitions
 *
 * @author Tony Burdett
 * @date 03/08/12
 */
@Component
public abstract class AbstractPussycatSession implements PussycatSession {
    private String sessionID;
    private Collection<Renderlet> renderlets;

    private Logger log = LoggerFactory.getLogger("rendering");

    protected AbstractPussycatSession() {
//        this.sessionID = generateSessionID();
//        this.renderlets = getAvailableRenderlets();
    }

    @PostConstruct
    public void init(){
        this.sessionID = generateSessionID();
    }

    protected Logger getLog() {
        return log;
    }

    @Override public String getSessionID() {
        return sessionID;
    }

    public Collection<Renderlet> getAvailableRenderlets() {
        if (renderlets == null) {
            ServiceLoader<Renderlet> renderletLoader = ServiceLoader.load(Renderlet.class);
            Collection<Renderlet> loadedRenderlets = new HashSet<Renderlet>();
            for (Renderlet renderlet : renderletLoader) {
                loadedRenderlets.add(renderlet);
            }
            getLog().debug("Loaded " + loadedRenderlets.size() + " renderlets");
            this.renderlets = loadedRenderlets;
        }
        return renderlets;
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
