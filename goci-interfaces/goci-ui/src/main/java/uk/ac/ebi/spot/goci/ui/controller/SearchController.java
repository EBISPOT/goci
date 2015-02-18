package uk.ac.ebi.spot.goci.ui.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.spot.goci.model.SearchResult;

/**
 * Created by dwelter on 12/01/15.
 */

@Controller
public class SearchController {
    @RequestMapping(value = "/search", produces = MediaType.TEXT_HTML_VALUE)
    String search(Model model, @RequestParam(required = false) String query) {
        SearchResult result = new SearchResult();
        result.setQuery(query);
        model.addAttribute("result", result);
        System.out.println("Hello!");
        return "search";
    }

//    @RequestMapping(value = "stats", produces = MediaType.APPLICATION_JSON_VALUE)
//    String getStats(Model model){
//        System.out.println("Hello again!");
//
//        String stats = "2015-02-17";
//
//        model.addAttribute("data", stats);
//        return "stats";
//    }
}
