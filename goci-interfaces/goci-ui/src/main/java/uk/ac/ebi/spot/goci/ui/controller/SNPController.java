package uk.ac.ebi.spot.goci.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.spot.goci.model.SnpResult;
import uk.ac.ebi.spot.goci.ui.SearchConfiguration;

/**
 * Created by laurent on 12/01/15.
 */

@Controller
public class SNPController {

    private SearchConfiguration searchConfiguration;

    @Autowired
    public SNPController(SearchConfiguration searchConfiguration) {
        this.searchConfiguration = searchConfiguration;
    }

    @RequestMapping(value = "/snp/{rsId}", produces = MediaType.TEXT_HTML_VALUE) String search(Model model,
                                                                                               @PathVariable String rsId,
                                                                                               @RequestParam(required = false) String filter) {

        SnpResult result = new SnpResult();
        result.setQuery(rsId);
        result.setFilter(filter);
        result.setRsId(rsId);
        model.addAttribute("result", result);
        return "/snp-page";
    }

}