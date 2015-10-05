package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.DateRange;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.model.MonthlyTotalsSummaryView;
import uk.ac.ebi.spot.goci.model.YearlyTotalsSummaryView;
import uk.ac.ebi.spot.goci.repository.MonthlyTotalsSummaryViewRepository;
import uk.ac.ebi.spot.goci.repository.YearlyTotalsSummaryViewRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    @Autowired
    public ReportController(MonthlyTotalsSummaryViewRepository monthlyTotalsSummaryViewRepository, YearlyTotalsSummaryViewRepository yearlyTotalsSummaryViewRepository) {
        this.monthlyTotalsSummaryViewRepository = monthlyTotalsSummaryViewRepository;
        this.yearlyTotalsSummaryViewRepository = yearlyTotalsSummaryViewRepository;
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




    // Studies by curator and/or status
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, params = "filters=true", method = RequestMethod.POST)
    public String filteredSearch(@ModelAttribute StudySearchFilter studySearchFilter, Model model) {

        // Get filter text
        String filterYear = studySearchFilter.getYearFilter();
        String filterMonth = studySearchFilter.getMonthFilter();


        return "reports";
    }

/* General purpose methods */

    // Months used in dropdown
    @ModelAttribute("months")
    public String[] populateMonths(Model model) {
        String[] shortMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return shortMonths;
    }

    // Years used in dropdown
    @ModelAttribute("years")
    public List<String> populateYears(Model model) {
        List<String> years = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        // Only have studies with dates up as far as 2005
        int endYear = 2005;
        while (year >= endYear) {
            String stringYear = String.valueOf(year);
            years.add(stringYear);
            year--;
        }
        return years;
    }

}