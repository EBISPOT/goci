package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.SecureRole;

/**
 * Created by emma on 12/05/2016.
 *
 * @author emma
 *         <p>
 *         Secure role builder
 */
public class SecureRoleBuilder {

    private SecureRole secureRole = new SecureRole();

    public SecureRoleBuilder setId(Long id) {
        secureRole.setId(id);
        return this;
    }

    public SecureRoleBuilder setRole(String role) {
        secureRole.setRole(role);
        return this;
    }

    public SecureRole build() {
        return secureRole;
    }
}