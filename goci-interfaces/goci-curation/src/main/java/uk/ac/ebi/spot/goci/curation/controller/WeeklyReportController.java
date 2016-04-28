package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.model.WeeklyTotalsSummaryView;
import uk.ac.ebi.spot.goci.repository.WeeklyTotalsSummaryViewRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwelter on 15/04/16.
 */
@Controller
@RequestMapping("/reports/weekly")
public class WeeklyReportController {

    private WeeklyTotalsSummaryViewRepository weeklyTotalsSummaryViewRepository;
//    private CuratorRepository curatorRepository;
//    private CurationStatusRepository curationStatusRepository;

    // Service class
//    private ReportService reportService;

    @Autowired
    public WeeklyReportController(WeeklyTotalsSummaryViewRepository weeklyTotalsSummaryViewRepository//,
//                                   CuratorRepository curatorRepository,
//                                   CurationStatusRepository curationStatusRepository,
//                                   ReportService reportService
    ) {
        this.weeklyTotalsSummaryViewRepository = weeklyTotalsSummaryViewRepository;
//        this.curatorRepository = curatorRepository;
//        this.curationStatusRepository = curationStatusRepository;
//        this.reportService = reportService;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getWeeklyOverview(Model model){

        List<WeeklyTotalsSummaryView> weeklyTotalsSummaryViews = new ArrayList<>();

//        weeklyTotalsSummaryViews = weeklyTotalsSummaryViewRepository.getLastEightWeeks();

        weeklyTotalsSummaryViews = weeklyTotalsSummaryViewRepository.findAll();

        Integer studiesTotal = null;
        Integer entriesTotal = null;

        for(WeeklyTotalsSummaryView w : weeklyTotalsSummaryViews){
            studiesTotal = studiesTotal + w.getWeeklyStudies();
            entriesTotal = entriesTotal + w.getWeeklyEntries();
        }

        Integer studiesAverage = studiesTotal/weeklyTotalsSummaryViews.size();
        Integer entriesAverage = entriesTotal/weeklyTotalsSummaryViews.size();


        model.addAttribute("weeklyTotalsSummaryViews", weeklyTotalsSummaryViews);
        model.addAttribute("studiesAverage", studiesAverage);
        model.addAttribute("entriesAverage", entriesAverage);

        return "reports_weekly_studies";
    }

}
