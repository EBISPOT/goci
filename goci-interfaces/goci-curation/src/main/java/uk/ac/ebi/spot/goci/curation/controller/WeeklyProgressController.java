package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by emma on 08/06/2016.
 *
 * @author emma
 */

@Controller
@RequestMapping("/reports/weekly_progress")
public class WeeklyProgressController {

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getWeeklyProgressTable(Model model) {

        //TODO DETERMINE WHAT THIS WILL RETURN
        processWeeklyView();


        // TODO CREATE VIEW
        return "view";

    }

}
