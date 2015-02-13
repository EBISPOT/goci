package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.CurrentUser;
import uk.ac.ebi.spot.goci.model.User;
import uk.ac.ebi.spot.goci.repository.UserRepository;

/**
 * Created by emma on 10/02/15.
 */
@Service
public class CurrentUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public CurrentUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check user with email exists
        try {
            User user = userRepository.findByEmail(email);
            return new CurrentUser(user);
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User with email: " + email + " " +
                    "was not found");
        }


    }
}
