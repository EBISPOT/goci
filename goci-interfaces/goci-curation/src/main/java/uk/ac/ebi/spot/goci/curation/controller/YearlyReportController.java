package uk.ac.ebi.spot.goci.curation.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.curation.service.ReportService;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.YearlyTotalsSummaryView;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.YearlyTotalsSummaryViewRepository;

import java.util.*;


/**
 * Created by emma on 29/01/15.
 *
 * @author emma
 *         <p>
 *         Report controller, used to return curator monthly totals to view
 */
@Controller
@RequestMapping("/reports/yearly")
public class YearlyReportController {

    // Repositories allowing access to database objects associated with
    private YearlyTotalsSummaryViewRepository yearlyTotalsSummaryViewRepository;
    private CuratorRepository curatorRepository;
    private CurationStatusRepository curationStatusRepository;

    // Service class
    private ReportService reportService;

    @Autowired
    public YearlyReportController(YearlyTotalsSummaryViewRepository yearlyTotalsSummaryViewRepository, CuratorRepository curatorRepository, CurationStatusRepository curationStatusRepository, ReportService reportService) {
        this.yearlyTotalsSummaryViewRepository = yearlyTotalsSummaryViewRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.reportService = reportService;
    }

    // Return yearly overview
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getYearlyOverview(Model model) {

        // Add a studySearchFilter to model in case user want to filter table
        model.addAttribute("studySearchFilter", new StudySearchFilter());

        List<YearlyTotalsSummaryView> yearlyTotalsSummaryViews = yearlyTotalsSummaryViewRepository.findAll();
        model.addAttribute("yearlyTotalsSummaryViews", yearlyTotalsSummaryViews);

        return "reports_yearly";
    }


/* General purpose methods used to populate drop downs */

    // Years used in dropdown
    @ModelAttribute("years")
    public List<Integer> populateYears(Model model) {
        return yearlyTotalsSummaryViewRepository.getAllYears();
    }

    // Curators
    @ModelAttribute("curators")
    public List<Curator> populateCurators(Model model) {
        return curatorRepository.findAll();
    }

    // Curation statuses
    @ModelAttribute("curationstatuses")
    public List<CurationStatus> populateCurationStatuses(Model model) {
        return curationStatusRepository.findAll();
    }

}