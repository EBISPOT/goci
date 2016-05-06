package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.CurrentUser;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.repository.SecureUserRepository;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by emma on 10/02/15.
 *
 * @author emma
 *         <p>
 *         Implementation of springs UserDetailsService
 */
@Service
public class CurrentUserDetailsService implements UserDetailsService {

    // Repository used to find users
    private SecureUserRepository secureUserRepository;

    @Autowired
    public CurrentUserDetailsService(SecureUserRepository secureUserRepository) {
        this.secureUserRepository = secureUserRepository;
    }

    @Override
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check user with email exists

        if (secureUserRepository.findByEmail(email) != null) {
            SecureUser secureUser = secureUserRepository.findByEmail(email);
            return new CurrentUser(secureUser);
        }
        else {
            throw new UsernameNotFoundException("User with email: " + email + " " +
                                                        "was not found");
        }
    }

    /**
     * Update a study status
     *
     * @param request request from which to obtain current user
     * @return SecureUser that represents currently logged in user
     */
    public SecureUser getUserFromRequest(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        return secureUserRepository.findByEmail(name);
    }
}
