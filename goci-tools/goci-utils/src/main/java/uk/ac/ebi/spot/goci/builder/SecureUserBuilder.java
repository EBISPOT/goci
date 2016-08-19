package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.SecureRole;
import uk.ac.ebi.spot.goci.model.SecureUser;

import java.util.Collection;

/**
 * Created by emma on 12/05/2016.
 *
 * @author emma
 *         <p>
 *         Secure User builder
 */
public class SecureUserBuilder {

    private SecureUser secureUser = new SecureUser();

    public SecureUserBuilder setId(Long id) {
        secureUser.setId(id);
        return this;
    }

    public SecureUserBuilder setEmail(String email) {
        secureUser.setEmail(email);
        return this;
    }

    public SecureUserBuilder setPasswordHash(String passwordHash) {
        secureUser.setPasswordHash(passwordHash);
        return this;
    }

    public SecureUserBuilder setRole(SecureRole role) {
        secureUser.setRole(role);
        return this;
    }

    public SecureUserBuilder setEvents(Collection<Event> events) {
        secureUser.setEvents(events);
        return this;
    }

    public SecureUser build() {
        return secureUser;
    }
}