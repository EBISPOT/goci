package uk.ac.ebi.spot.goci.curation.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.MonthlyTotalsSummaryView;
import uk.ac.ebi.spot.goci.model.YearlyTotalsSummaryView;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.MonthlyTotalsSummaryViewRepository;
import uk.ac.ebi.spot.goci.repository.YearlyTotalsSummaryViewRepository;

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

    // Return overview
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getOverview(Model model) {

        // Add a studySearchFilter to model in case user want to filter table
        model.addAttribute("studySearchFilter", new StudySearchFilter());

        List<MonthlyTotalsSummaryView> monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findAll();
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

/*
    // Studies by curator and/or status
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, params = "filters=true", method = RequestMethod.POST)
    public String monthlyTotalsFilteredSearch(@ModelAttribute StudySearchFilter studySearchFilter, Model model) {

        // Get filter text
        String filterYear = studySearchFilter.getYearFilter();
        String filterMonth = studySearchFilter.getMonthFilter();
        Long status = studySearchFilter.getStatusSearchFilterId();
        Long curator = studySearchFilter.getCuratorSearchFilterId();

        List<MonthlyTotalsSummaryView> monthlyTotalsSummaryViews = new ArrayList<>();
        Integer year = Integer.valueOf(filterYear);
        Integer month = 0;
        if (filterMonth! = null  && !filterMonth.isEmpty()){
            month = convertMonthToNumberForm(filterMonth);
        }

        // Handle filtering by year or month
        if (filterYear != null && !filterYear.isEmpty()) {
            // If year and month find by both
            if (filterMonth != null && !filterMonth.isEmpty()) {
               monthlyTotalsSummaryViews = monthlyTotalsSummaryViewRepository.findByYearAndMonthOrderByYearDesc(year, month);

            }
            else {
                // return just year

            }
        }
        // If user entered a month
        else {
            if (filterMonth != null && !filterMonth.isEmpty()) {


            }
        }

        model.addAttribute("monthlyTotalsSummaryViews", monthlyTotalsSummaryViews);
        return "reports";
    }

    private Integer convertMonthToNumberForm(String filterMonth) {
        Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(filterMonth);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        System.out.println(month == Calendar.FEBRUARY);


    }*/




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