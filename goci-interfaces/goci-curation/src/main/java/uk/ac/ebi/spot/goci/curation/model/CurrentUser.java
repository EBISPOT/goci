package uk.ac.ebi.spot.goci.curation.model;

import org.springframework.security.core.authority.AuthorityUtils;
import uk.ac.ebi.spot.goci.model.SecureRole;
import uk.ac.ebi.spot.goci.model.SecureUser;

/**
 * Created by emma on 10/02/15.
 * @author emma
 *
 * Describes our current user, with enough information to authorise our user
 */
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    // Wrap user model object
    private SecureUser secureUser;


    /*
     UserDetails.username is populated from User.email
     UserDetails.password is populated from User.passwordHash
     The User.role, converted to String, is wrapped in
     GrantedAuthority object by AuthorityUtils helper class.
     It will be available as the only element of the list when UserDetails.getAuthorities() is called.
     */

    public CurrentUser(SecureUser secureUser) {
        super(secureUser.getEmail(), secureUser.getPasswordHash(), AuthorityUtils.createAuthorityList(secureUser.getRole().toString()));
        this.secureUser = secureUser;
    }

    public SecureUser getSecureUser() {
        return secureUser;
    }

    public Long getId() {
        return secureUser.getId();

    }

    public SecureRole getRole() {
        return secureUser.getRole();
    }

}