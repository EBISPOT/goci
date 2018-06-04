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
//@RequestMapping("/reports/weekly_progress")
public class WeeklyProgressController {

    private WeeklyProgressService weeklyProgressService;

    @Autowired
    public WeeklyProgressController(WeeklyProgressService weeklyProgressService) {
        this.weeklyProgressService = weeklyProgressService;
    }

    @RequestMapping(value = "/reports/weekly_progress", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getWeeklyProgressTable(Model model) {

        String content = "overall";

        List<ReportsWeeklyProgressView> weeklyProgressViews = weeklyProgressService.processWeeklyView(content);
        model.addAttribute("weeklyProgressViews", weeklyProgressViews);
        return "reports_weekly_progress";
    }

    @RequestMapping(value = "/reports/reports_weekly_progress_targeted_arrays", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getWeeklyProgressTargetedArraysTable(Model model) {

        String content = "targeted";

        List<ReportsWeeklyProgressView> weeklyProgressViews = weeklyProgressService.processWeeklyView(content);
        model.addAttribute("weeklyProgressViews", weeklyProgressViews);
        return "reports_weekly_progress_targeted_arrays";
    }
}