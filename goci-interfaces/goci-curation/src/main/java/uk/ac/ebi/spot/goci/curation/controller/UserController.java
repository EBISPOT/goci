package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.UserCreateForm;
import uk.ac.ebi.spot.goci.model.SecureRole;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.repository.SecureRoleRepository;
import uk.ac.ebi.spot.goci.repository.SecureUserRepository;

import java.util.List;

/**
 * Created by emma on 09/02/15.
 *
 * @author emma
 *         <p/>
 *         User controller interprets user input and transform it into a user model that is represented to the user by the associated HTML page.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private SecureUserRepository secureUserRepository;
    private SecureRoleRepository roleRepository;

    @Autowired
    public UserController(SecureUserRepository secureUserRepository) {
        this.secureUserRepository = secureUserRepository;
    }

    // Return empty form
    @RequestMapping(value = "/create", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getUserForm(Model model) {
        model.addAttribute("user_create_form", new UserCreateForm());
        return "user_create";

    }

    // Create user from returned form
    @RequestMapping(value = "/create", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String createUserFromForm(Model model, UserCreateForm userCreateForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user_create_form", userCreateForm);
            return "user_create";
        }
        try {
            SecureUser newSecureUser = createUser(userCreateForm);
            // Save our new user
            secureUserRepository.save(newSecureUser);

        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("email.exists", "Email already exists");
            return "user_create";
        }
        return "redirect:/login";

    }


    /* General purpose methods */

    // Takes information in form and create a user
    private SecureUser createUser(UserCreateForm form) {
        SecureUser secureUser = new SecureUser();
        secureUser.setEmail(form.getEmail());
        secureUser.setPasswordHash(new BCryptPasswordEncoder().encode(form.getPassword()));
        secureUser.setRole(form.getSecureRole());
        return secureUser;
    }

    // Roles, used in role dropdown on form
    @ModelAttribute("userRoles")
    public List<SecureRole> populateUserRoles(Model model) {
        return roleRepository.findAll();
    }

}
