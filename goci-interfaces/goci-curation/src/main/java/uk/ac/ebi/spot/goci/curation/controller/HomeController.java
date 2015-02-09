package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;

/**
 * Created by emma on 09/02/15.
 */
@Controller
@RequestMapping("/")
public class HomeController {

    public HomeController() {
    }

    // Return all studies
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String showHomePage(Model model) {
        return "home";
    }
}
