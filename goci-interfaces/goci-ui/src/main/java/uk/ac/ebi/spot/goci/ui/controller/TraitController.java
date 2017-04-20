package uk.ac.ebi.spot.goci.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.spot.goci.ui.SearchConfiguration;

/**
 * Created by xinhe on 19/04/2017.
 */
@Controller
@RequestMapping("/beta")
public class TraitController {


    private SearchConfiguration searchConfiguration;

    @Autowired
    public TraitController(SearchConfiguration searchConfiguration) {
        this.searchConfiguration = searchConfiguration;
    }


    @RequestMapping(path = "/trait", method = RequestMethod.GET)
    public String traitPage() {
        return "trait-page2";
    }

    @RequestMapping(value = "/trait/{traitName}", produces = MediaType.TEXT_HTML_VALUE)
    public String search(Model model) {
        return "trait-page2";
    }


}
