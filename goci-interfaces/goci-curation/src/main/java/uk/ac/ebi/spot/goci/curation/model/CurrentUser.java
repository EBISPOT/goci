package uk.ac.ebi.spot.goci.curation.model;

import org.springframework.security.core.authority.AuthorityUtils;
import uk.ac.ebi.spot.goci.model.Role;
import uk.ac.ebi.spot.goci.model.User;

/**
 * Created by emma on 10/02/15.
 */
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    // Wrap user model object
    private User user;


    /*
     UserDetails.username is populated from User.email
     UserDetails.password is populated from User.passwordHash
     The User.role, converted to String, is wrapped in
     GrantedAuthority object by AuthorityUtils helper class.
     It will be available as the only element of the list when UserDetails.getAuthorities() is called.
     */

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();

    }

    public Role getRole() {
        return user.getRole();
    }

}