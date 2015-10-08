package uk.ac.ebi.spot.goci.curation.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.curation.service.ReportService;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.YearlyTotalsSummaryView;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.YearlyTotalsSummaryViewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by emma on 29/01/15.
 *
 * @author emma
 *         <p>
 *         Report controller, used to return curator yearly totals to view.
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
    public String getYearlyOverview(Model model, @RequestParam(required = false) Long status,
                                    @RequestParam(required = false) Long curator,
                                    @RequestParam(required = false) Integer year) {

        List<YearlyTotalsSummaryView> yearlyTotalsSummaryViews = new ArrayList<>();

        // This will be returned to view and store what curator has searched for
        StudySearchFilter studySearchFilter = new StudySearchFilter();

        // Need to convert status and curator to a string
        String curatorName = null;
        String statusName = null;
        if (curator != null) {
            curatorName = curatorRepository.findOne(curator).getLastName();
        }
        if (status != null) {
            statusName = curationStatusRepository.findOne(status).getStatus();
        }

        //Search database for various filter options
        if (status != null && curator != null && year != null) { // all filter options supplied
            yearlyTotalsSummaryViews = yearlyTotalsSummaryViewRepository.findByCuratorAndCurationStatusAndYearOrderByYearDesc(curatorName, statusName, year);
        } else if (status != null && curator != null) { // status and curator
            yearlyTotalsSummaryViews = yearlyTotalsSummaryViewRepository.findByCuratorAndCurationStatus(curatorName, statusName);
        } else if (status != null && year != null) { // status and year
            yearlyTotalsSummaryViews = yearlyTotalsSummaryViewRepository.findByCurationStatusAndYearOrderByYearDesc(statusName, year);
        } else if (status != null) {
            yearlyTotalsSummaryViews = yearlyTotalsSummaryViewRepository.findByCurationStatus(statusName);
        } else if (curator != null && year != null) { // curator and year
            yearlyTotalsSummaryViews = yearlyTotalsSummaryViewRepository.findByCuratorAndYearOrderByYearDesc(curatorName, year);
        } else if (curator != null) {
            yearlyTotalsSummaryViews = yearlyTotalsSummaryViewRepository.findByCurator(curatorName);
        } else if (year != null) {
            yearlyTotalsSummaryViews = yearlyTotalsSummaryViewRepository.findByYearOrderByYearDesc(year);
        } else { // no filters
            yearlyTotalsSummaryViews = yearlyTotalsSummaryViewRepository.findAll();
        }

        studySearchFilter.setCuratorSearchFilterId(curator);
        studySearchFilter.setStatusSearchFilterId(status);
        studySearchFilter.setYearFilter(year);

        // Add studySearchFilter to model so user can filter table
        model.addAttribute("studySearchFilter", studySearchFilter);
        model.addAttribute("yearlyTotalsSummaryViews", yearlyTotalsSummaryViews);

        return "reports_yearly";
    }

    // Takes filters supplied and creates appropriate redirect
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String searchByFilter(@ModelAttribute StudySearchFilter studySearchFilter,
                                 Model model,
                                 @RequestParam(required = true) String filters) {

        // Get ids of objects searched for
        Long status = studySearchFilter.getStatusSearchFilterId();
        Long curator = studySearchFilter.getCuratorSearchFilterId();
        Integer year = studySearchFilter.getYearFilter();
        Integer month = studySearchFilter.getMonthFilter();

        // To handle various filters create a map to store type and value
        Map<String, Object> filterMap = reportService.buildRedirectMap(status, curator, year, null);

        String redirectPrefix = "redirect:/reports/yearly";
        return reportService.buildRedirect(redirectPrefix, filterMap);
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