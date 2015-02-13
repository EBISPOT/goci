package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.CuratorTotalsTableRow;
import uk.ac.ebi.spot.goci.curation.model.DateRange;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by emma on 29/01/15.
 *
 * @author emma
 *         Report controller, used to reurn curator monthly totals to view
 */
@Controller
@RequestMapping("/reports")
public class ReportController {

    // Repositories allowing access to database objects associated with a study
    private StudyRepository studyRepository;
    private CuratorRepository curatorRepository;

    // Table row
    private CuratorTotalsTableRow curatorTotalsTableRow;

    @Autowired
    public ReportController(StudyRepository studyRepository, CuratorRepository curatorRepository, CuratorTotalsTableRow curatorTotalsTableRow) {
        this.studyRepository = studyRepository;
        this.curatorRepository = curatorRepository;
        this.curatorTotalsTableRow = curatorTotalsTableRow;
    }

    // Return counts
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String getCountsTable(Model model) {

        Collection<CuratorTotalsTableRow> curatorTotalsTableRows = new ArrayList<>();
        Collection<Curator> allCurators = curatorRepository.findAll();

        // This list will hold our date ranges
        List<DateRange> dateRanges = createDateList();

        for (Curator curator : allCurators) {
            for (DateRange dateRange : dateRanges) {

                // Query database for studies between certain dates curated by current curator
                List<Study> studies = studyRepository.findByStudyDateAndCurator(curator.getId(), dateRange.getDateFrom(), dateRange.getDateTo());

                if (studies.size() != 0) {

                    CuratorTotalsTableRow row = new CuratorTotalsTableRow();
                    row.setCurator(curator.getLastName());
                    row.setCuratorTotalEntries(studies.size());

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateRange.getDateTo());
                    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));

                    // Create year and month to add as period
                    String month = new SimpleDateFormat("MMM").format(cal.getTime());
                    row.setMonth(month);

                    String year = new SimpleDateFormat("yyyy").format(cal.getTime());
                    row.setYear(year);

                    row.setPeriod(year + " " + month);
                    curatorTotalsTableRows.add(row);
                }
            }
        }

        model.addAttribute("curatorTotalsTableRows", curatorTotalsTableRows);

        // Add a studySearchFilter to model in case user want to filter table
        model.addAttribute("studySearchFilter", new StudySearchFilter());
        return "reports";

    }


    // Studies by curator and/or status
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, params = "filters=true", method = RequestMethod.POST)
    public String filteredSearch(@ModelAttribute StudySearchFilter studySearchFilter, Model model) {

        // Get filter text
        String filterYear = studySearchFilter.getYearFilter();
        String filterMonth = studySearchFilter.getMonthFilter();

        Collection<CuratorTotalsTableRow> curatorTotalsTableRows = new ArrayList<>();
        Collection<Curator> allCurators = curatorRepository.findAll();

        // This map will hold our date range and the associated curator and total
        List<DateRange> dateRanges = createDateList();

        for (Curator curator : allCurators) {
            for (DateRange dateRange : dateRanges) {

                List<Study> studies = studyRepository.findByStudyDateAndCurator(curator.getId(), dateRange.getDateFrom(), dateRange.getDateTo());

                if (studies.size() != 0) {

                    CuratorTotalsTableRow row = new CuratorTotalsTableRow();
                    row.setCurator(curator.getLastName());
                    row.setCuratorTotalEntries(studies.size());

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateRange.getDateTo());
                    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));

                    // Create year and month to add as period
                    String month = new SimpleDateFormat("MMM").format(cal.getTime());
                    row.setMonth(month);

                    String year = new SimpleDateFormat("yyyy").format(cal.getTime());
                    row.setYear(year);

                    row.setPeriod(year + " " + month);

                    // Organise the results according to filter option
                    if (filterYear != null && !filterYear.isEmpty()) {
                        // If year and month find by both
                        if (filterMonth != null && !filterMonth.isEmpty()) {
                            if (filterMonth.equalsIgnoreCase(month) && filterYear.equalsIgnoreCase(year)) {
                                curatorTotalsTableRows.add(row);
                            }
                        } else {
                            // return just year
                            if (filterYear.equals(year)) {
                                curatorTotalsTableRows.add(row);
                            }
                        }
                    }
                    // If user entered a month
                    else {
                        if (filterMonth != null && !filterMonth.isEmpty()) {
                            if (filterMonth.equalsIgnoreCase(month)) {
                                curatorTotalsTableRows.add(row);
                            }
                        }
                    }
                }
            }
        }

        model.addAttribute("curatorTotalsTableRows", curatorTotalsTableRows);
        return "reports";
    }
/*General purpose methods*/

    private List<DateRange> createDateList() {

        List<DateRange> dateRanges = new ArrayList<>();
        Collection<Study> allStudies = studyRepository.findAll();

        for (Study study : allStudies) {
            // Get study date
            Date studyDate = study.getStudyDate();

            boolean found = false;

            for (DateRange dateRange : dateRanges) {

                if (dateRange.contains(studyDate)) {
                    found = true;
                    break;
                }
            }
            // If the date is not in our index
            if (!found) {
                DateRange dateRange = createDateRange(studyDate);
                dateRanges.add(dateRange);

            }
        }
        return dateRanges;
    }

    // Creates a range from a supplied date, range should be from the very end of the previous month
    // to end of current month of study date
    private DateRange createDateRange(Date studyDate) {
        //Create a new date range
        Calendar calDateFrom = Calendar.getInstance();
        calDateFrom.setTime(studyDate);

        // If its January we need to set range from end of Dec of last year
        if (calDateFrom.get(Calendar.MONTH) == 0) {
            calDateFrom.set(Calendar.YEAR, calDateFrom.get(Calendar.YEAR) - 1);
            calDateFrom.set(Calendar.MONTH, Calendar.DECEMBER);
            calDateFrom.set(Calendar.DAY_OF_MONTH, calDateFrom.getActualMaximum(Calendar.DAY_OF_MONTH));
            calDateFrom.set(Calendar.HOUR_OF_DAY, 23);
            calDateFrom.set(Calendar.MINUTE, 59);
            calDateFrom.set(Calendar.SECOND, 59);


        } else {
            calDateFrom.set(Calendar.MONTH, calDateFrom.get(Calendar.MONTH) - 1);
            calDateFrom.set(Calendar.DAY_OF_MONTH, calDateFrom.getActualMaximum(Calendar.DAY_OF_MONTH));
            calDateFrom.set(Calendar.HOUR_OF_DAY, 23);
            calDateFrom.set(Calendar.MINUTE, 59);
            calDateFrom.set(Calendar.SECOND, 59);

        }

        Date dateFrom = calDateFrom.getTime();

        Calendar calDateTo = Calendar.getInstance();
        calDateTo.setTime(studyDate);
        calDateTo.set(Calendar.MONTH, calDateTo.get(Calendar.MONTH));
        calDateTo.set(Calendar.DAY_OF_MONTH, calDateTo.getActualMaximum(Calendar.DAY_OF_MONTH));
        calDateTo.set(Calendar.HOUR_OF_DAY, 23);
        calDateTo.set(Calendar.MINUTE, 59);
        calDateTo.set(Calendar.SECOND, 59);
        Date dateTo = calDateTo.getTime();

        DateRange dateRange = new DateRange(dateFrom, dateTo);

        return dateRange;

    }

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