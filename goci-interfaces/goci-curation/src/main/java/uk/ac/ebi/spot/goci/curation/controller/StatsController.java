package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.ebi.spot.goci.curation.component.StatsReportExcelView;
import uk.ac.ebi.spot.goci.curation.service.reports.WeeklyProgressReportService;
import uk.ac.ebi.spot.goci.service.CuratorTrackingService;
import uk.ac.ebi.spot.goci.service.StudyTrackingViewService;
import uk.ac.ebi.spot.goci.service.WeeklyTrackingService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Cinzia on 19/12/16.
 *
 * @author Cinzia
 *         <p>
 *         Stats controller.
 *
 */
@Controller
@RequestMapping("/reports/stats")
public class StatsController {
    private StudyTrackingViewService studyTrackingViewService;
    private CuratorTrackingService curatorTrackingService;
    private WeeklyTrackingService weeklyTrackingService;
    private WeeklyProgressReportService weeklyProgressReportService;

    @Autowired
    public StatsController(StudyTrackingViewService studyTrackingViewService,
                           CuratorTrackingService curatorTrackingService,
                           WeeklyTrackingService weeklyTrackingService,
                           WeeklyProgressReportService weeklyProgressReportService) {

        this.studyTrackingViewService = studyTrackingViewService;
        this.weeklyTrackingService = weeklyTrackingService;
        this.curatorTrackingService = curatorTrackingService;
        this.weeklyProgressReportService = weeklyProgressReportService;
    }


    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String statsHomePage(Model model) {
        return "reports_stats";
    }

    @RequestMapping(value = "/generateStats", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String generateWeeklyReport() {
        weeklyTrackingService.deleteAll();
        curatorTrackingService.deleteAll();
        studyTrackingViewService.generateReport();
        return "{\"success\":1}";

    }


    @RequestMapping(value = "/downloadStatsExcel", method = RequestMethod.GET)
    public ModelAndView getMyData(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        Map<String, Object> model = new HashMap<String, Object>();
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, 1);

        int lastWeek = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
        int year = calendar.get(Calendar.YEAR);

        if (lastWeek == 0) {
            lastWeek = 52;
            year = year-1;
        }

        ArrayList<Integer[]> progressiveQueues = weeklyProgressReportService.calculateProgressiveQueues();
        model.put("progressiveQueues", progressiveQueues);


        List<Object> reportWeekly = weeklyTrackingService.findAllWeekStatsReport();
        model.put("reportWeekly", reportWeekly);


        List<Object> curatorsStatsByWeek = curatorTrackingService.statsByWeek(year, lastWeek);

        model.put("curatorsStatsByWeek", curatorsStatsByWeek);
        String period = Integer.toString(lastWeek) + "/" + Integer.toString(year);
        model.put("periodStatsByWeek", period);

        List<String> curators = curatorTrackingService.findAllCurators();
        for (String curatorName : curators) {
            List<Object> curatorStats = curatorTrackingService.statsByCuration(curatorName);
            model.put("curator_" + curatorName, curatorStats);
        }

        return new ModelAndView(new StatsReportExcelView(), model);
    }

    @RequestMapping(value = "/getProgressiveQueuesJson", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ArrayList<Integer[]> getProgressiveQueuesJson() {
        ArrayList<Integer[]> progressiveQueues = weeklyProgressReportService.calculateProgressiveQueues();
        return progressiveQueues;
    }

}