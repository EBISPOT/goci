package uk.ac.ebi.spot.goci.curation.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.curation.service.ReportService;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.MonthlyTotalsSummaryView;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.MonthlyTotalsSummaryViewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by emma on 29/01/15.
 *
 * @author emma
 *         <p>
 *         Controller used to return curator monthly totals to view.
 */
@Controller
@RequestMapping("/reports/monthly")
public class MonthlyReportController {

    // Repositories allowing access to database objects associated with
    private MonthlyTotalsSummaryViewRepository monthlyTotalsSummaryViewRepository;
    private CuratorRepository curatorRepository;
    private CurationStatusRepository curationStatusRepository;

    // Service class
    private ReportService reportService;

    @Autowired
    public MonthlyReportController(MonthlyTotalsSummaryViewRepository monthlyTotalsSummaryViewRepository, CuratorRepository curatorRepository, CurationStatusRepository curationStatusRepository, ReportService reportService) {
        this.monthlyTotalsSummaryViewRepository = monthlyTotalsSummaryViewRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.reportService = reportService;
    }

    // Returns overview
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getOverview(Model model,
                              @RequestParam(required = false) Long status,
                              @RequestParam(required = false) Long curator,
                              @RequestParam(required = false) Integer year,
                              @RequestParam(required = false) Integer month) {


        List<MonthlyTotalsSummaryView> monthlyTotalsSummaryViews = new ArrayList<>();

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
        if (status != null && curator != null && year != null && month != null) { // all filter options
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCuratorAndCurationStatusAndYearAndMonthOrderByYearDesc(curatorName, statusName, year, month);
        } else if (status != null && curator != null && year != null) { // status, curator and year
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCuratorAndCurationStatusAndYearOrderByYearDesc(curatorName, statusName, year);
        } else if (status != null && curator != null && month != null) { // status, curator and month
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCuratorAndCurationStatusAndMonthOrderByYearDesc(curatorName, statusName, month);
        } else if (status != null && year != null && month != null) { // status, year, month
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCurationStatusAndYearAndMonthOrderByYearDesc(statusName, year, month);
        } else if (status != null && curator != null) { // status and curator
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCuratorAndCurationStatus(curatorName, statusName);
        } else if (status != null && year != null) { // status and year
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCurationStatusAndYearOrderByYearDesc(statusName, year);
        } else if (status != null && month != null) { // status and year
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCurationStatusAndMonthOrderByYearDesc(statusName, month);
        } else if (status != null) {
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCurationStatus(statusName);
        } else if (curator != null && year != null && month != null) { // curator, year and month
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCuratorAndYearAndMonthOrderByYearDesc(curatorName, year, month);
        } else if (curator != null && year != null) { // curator and year
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCuratorAndYearOrderByYearDesc(curatorName, year);
        } else if (curator != null && month != null) { // curator and month
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCuratorAndMonthOrderByYearDesc(curatorName, month);
        } else if (curator != null) {
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCurator(curatorName);
        } else if (year != null && month != null) { // year and month
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByYearAndMonthOrderByYearDesc(year, month);
        } else if (year != null) {
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByYearOrderByYearDesc(year);
        } else if (month != null) {
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByMonthOrderByYearDesc(month);
        } else { // no filters
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findAll();
        }

        studySearchFilter.setCuratorSearchFilterId(curator);
        studySearchFilter.setStatusSearchFilterId(status);
        studySearchFilter.setYearFilter(year);
        studySearchFilter.setMonthFilter(month);

        // Add studySearchFilter to model so user can filter table
        model.addAttribute("studySearchFilter", studySearchFilter);
        model.addAttribute("monthlyTotalsSummaryViews", monthlyTotalsSummaryViews);
        return "reports";
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
        Map<String, Object> filterMap = reportService.buildRedirectMap(status, curator, year, month);

        String redirectPrefix = "redirect:/reports/monthly";
        return reportService.buildRedirect(redirectPrefix, filterMap);
    }


    // Redirect to studies page
    @RequestMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getStudies(Model model, @PathVariable Long id) {

        // Get redirect variables
        MonthlyTotalsSummaryView monthlyTotalsSummaryView = monthlyTotalsSummaryViewRepository.findOne(id);
        String curator = monthlyTotalsSummaryView.getCurator();
        Long curatorId = curatorRepository.findByLastName(curator).getId();

        String status = monthlyTotalsSummaryView.getCurationStatus();
        Long statusId = curationStatusRepository.findByStatus(status).getId();

        Integer year = monthlyTotalsSummaryView.getYear();
        Integer month = monthlyTotalsSummaryView.getMonth();

        return "redirect:/studies?page=1&status=" + statusId + "&curator=" + curatorId + "&year=" + year + "&month=" + month;
    }

/* General purpose methods used to populate drop downs */

    // Months used in dropdown
    @ModelAttribute("months")
    private List<Integer> populateMonths(Model model) {
        return monthlyTotalsSummaryViewRepository.getAllMonths();
    }

    // Years used in dropdown
    @ModelAttribute("years")
    public List<Integer> populateYears(Model model) {
        return monthlyTotalsSummaryViewRepository.getAllYears();
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