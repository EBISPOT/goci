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
import uk.ac.ebi.spot.goci.model.User;
import uk.ac.ebi.spot.goci.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emma on 09/02/15.
 */
@Controller
public class UserController {

    private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/user/create", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getUserForm(Model model) {
        model.addAttribute("user_create_form", new UserCreateForm());
        return "user_create";

    }

    @RequestMapping(value = "/user/create", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    public String createUserFromForm(Model model, UserCreateForm userCreateForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user_create_form", userCreateForm);
            return "user_create";
        }
        try {
            User newUser = createUser(userCreateForm);
            // Save our new user
            userRepository.save(newUser);
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("email.exists", "Email already exists");
            return "user_create";
        }
        return "redirect:/login";

    }


    /* General purpose methods */

    // Takes information in form and create a user
    private User createUser(UserCreateForm form) {
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPasswordHash(new BCryptPasswordEncoder().encode(form.getPassword()));
        user.setRole(form.getRole());
        return user;
    }

    // Ethnicity Types
    @ModelAttribute("userRoles")
    public List<String> populateUserRoles(Model model) {
        List<String> roles = new ArrayList<>();
        roles.add("CURATOR");
        roles.add("SUBMITTER");
        return roles;
    }

}
