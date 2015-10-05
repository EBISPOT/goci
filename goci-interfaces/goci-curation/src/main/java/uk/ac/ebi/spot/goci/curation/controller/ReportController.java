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
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.MonthlyTotalsSummaryView;
import uk.ac.ebi.spot.goci.model.YearlyTotalsSummaryView;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.MonthlyTotalsSummaryViewRepository;
import uk.ac.ebi.spot.goci.repository.YearlyTotalsSummaryViewRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by emma on 29/01/15.
 *
 * @author emma
 *         <p>
 *         Report controller, used to return curator monthly totals to view
 */
@Controller
@RequestMapping("/reports")
public class ReportController {

    // Repositories allowing access to database objects associated with
    private MonthlyTotalsSummaryViewRepository monthlyTotalsSummaryViewRepository;
    private YearlyTotalsSummaryViewRepository yearlyTotalsSummaryViewRepository;
    private CuratorRepository curatorRepository;
    private CurationStatusRepository curationStatusRepository;

    @Autowired
    public ReportController(MonthlyTotalsSummaryViewRepository monthlyTotalsSummaryViewRepository, YearlyTotalsSummaryViewRepository yearlyTotalsSummaryViewRepository, CuratorRepository curatorRepository, CurationStatusRepository curationStatusRepository) {
        this.monthlyTotalsSummaryViewRepository = monthlyTotalsSummaryViewRepository;
        this.yearlyTotalsSummaryViewRepository = yearlyTotalsSummaryViewRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
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

        // If user entered a status
        if (status != null) {
            // If we have curator and status find by both
            if (curator != null) {
                monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCuratorAndCurationStatus(curatorName, statusName);

                // Return these values so they appear in filter results
                studySearchFilter.setCuratorSearchFilterId(curator);
                studySearchFilter.setStatusSearchFilterId(status);

            } else {
                monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCurationStatus(statusName);

                // Return this value so it appears in filter result
                studySearchFilter.setStatusSearchFilterId(status);

            }
        }
        // If user entered curator
        else if (curator != null) {
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByCurator(curatorName);

            // Return this value so it appears in filter result
            studySearchFilter.setCuratorSearchFilterId(curator);
        } else if (year != null) {

            // Handle filtering by year or month

            // If year and month find by both
            if (month != null) {
                monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByYearAndMonthOrderByYearDesc(year, month);
            } else {
                // return just year
                monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByYearOrderByYearDesc(year);
            }
        }

        // If user entered a month
        else if (month != null) {
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByMonthOrderByYearDesc(month);
        } else {
            monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findAll();
        }


        // Add studySearchFilter to model so user can filter table
        model.addAttribute("studySearchFilter", studySearchFilter);
        model.addAttribute("monthlyTotalsSummaryViews", monthlyTotalsSummaryViews);
        return "reports";
    }

    // Return yearly overview
    @RequestMapping(value = "/yearly", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getYearlyOverview(Model model) {

        // Add a studySearchFilter to model in case user want to filter table
        model.addAttribute("studySearchFilter", new StudySearchFilter());

        List<YearlyTotalsSummaryView> yearlyTotalsSummaryViews = yearlyTotalsSummaryViewRepository.findAll();
        model.addAttribute("yearlyTotalsSummaryViews", yearlyTotalsSummaryViews);

        return "reports_yearly";
    }


    // Redirects from landing page and main page
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String searchByFilter(@ModelAttribute StudySearchFilter studySearchFilter,
                                 Model model,
                                 @RequestParam(required = true) String filters) {

        // Get ids of objects searched for
        Long status = studySearchFilter.getStatusSearchFilterId();
        Long curator = studySearchFilter.getCuratorSearchFilterId();
        Integer year = studySearchFilter.getYearFilter();
        Integer month = studySearchFilter.getMonthFilter();


        // If user entered a status
        if (status != null) {
            // If we have curator and status find by both
            if (curator != null) {
                return "redirect:/reports?status=" + status + "&curator=" + curator;
            } else {
                return "redirect:/reports?status=" + status;
            }
        }
        // If user entered curator
        else if (curator != null) {
            return "redirect:/reports?curator=" + curator;
        }
        // For year and moth searches
        else if (year != null) {
            if (month != null) {
                return "redirect:/reports?year=" + year + "&month=" + month;
            } else {
                return "redirect:/reports?year=" + year;
            }
        }
        // If user entered a month
        else if (month != null) {
            return "redirect:/reports?month=" + month;
        }
        // If all else fails
        else {
            return "redirect:/reports";
        }

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