package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by emma on 09/02/15.
 *
 * @author emma
 *         <p>
 *         Home controller directs users to login page
 */
@Controller
@RequestMapping("/")
public class HomeController {

    public HomeController() {
    }

    // Return home page
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getHomePage(Model model) {

        return "redirect:/login";
    }
}
