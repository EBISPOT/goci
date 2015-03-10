package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;

/**
 * Created by emma on 06/03/15.
 *
 * @author emma
 *         <p>
 *         Controls landing page with various user options
 */
@Controller
@RequestMapping("/user_options")
public class LandingPageController {

    public LandingPageController() {
    }

    // Return home page
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getLandingPage(Model model) {

        // Add a studySearchFilter to model in case user want to filter table
        model.addAttribute("studySearchFilter", new StudySearchFilter());

        return "user_options";
    }

}
