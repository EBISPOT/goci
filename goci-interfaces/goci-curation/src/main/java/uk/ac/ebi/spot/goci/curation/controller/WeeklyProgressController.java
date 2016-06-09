package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.reports.ReportsWeeklyProgressView;
import uk.ac.ebi.spot.goci.curation.service.reports.WeeklyProgressService;

import java.util.List;

/**
 * Created by emma on 08/06/2016.
 *
 * @author emma
 *         <p>
 *         Controller used to handle requests to display weekly curator progress
 */

@Controller
@RequestMapping("/reports/weekly_progress")
public class WeeklyProgressController {

    private WeeklyProgressService weeklyProgressService;

    @Autowired
    public WeeklyProgressController(WeeklyProgressService weeklyProgressService) {
        this.weeklyProgressService = weeklyProgressService;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getWeeklyProgressTable(Model model) {

        List<ReportsWeeklyProgressView> weeklyProgressViews = weeklyProgressService.processWeeklyView();
        model.addAttribute("weeklyProgressViews", weeklyProgressViews);
        return "reports_weekly_progress";
    }
}