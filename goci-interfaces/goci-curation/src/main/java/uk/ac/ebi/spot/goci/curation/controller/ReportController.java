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
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    private CurationStatusRepository curationStatusRepository;

    // Table row
    private CuratorTotalsTableRow curatorTotalsTableRow;

    @Autowired
    public ReportController(StudyRepository studyRepository, CuratorRepository curatorRepository, CurationStatusRepository curationStatusRepository, CuratorTotalsTableRow curatorTotalsTableRow) {
        this.studyRepository = studyRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.curatorTotalsTableRow = curatorTotalsTableRow;
    }


    // Return counts
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String getCountsTable(Model model) {

        Collection<CuratorTotalsTableRow> curatorTotalsTableRows = new ArrayList<>();
        Collection<Curator> allCurators = curatorRepository.findAll();

        // This map will hold our date range and the associated curator and total
        Map<DateRange, Map<Curator, AtomicInteger>> dateMap = createDateMap(allCurators);

        for (DateRange dateRange : dateMap.keySet()) {
            for (Curator curator : dateMap.get(dateRange).keySet()) {
                CuratorTotalsTableRow row = new CuratorTotalsTableRow();
                row.setCurator(curator.getLastName());
                row.setCuratorTotalEntries(dateMap.get(dateRange).get(curator).get());

                Calendar cal = Calendar.getInstance();
                cal.setTime(dateRange.getDateTo());
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));

                String month = new SimpleDateFormat("MMM").format(cal.getTime());
                row.setMonth(month);

                String year = new SimpleDateFormat("yyyy").format(cal.getTime());
                row.setYear(year);

                row.setPeriod(year + " " + month);
                curatorTotalsTableRows.add(row);
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

        Collection<CuratorTotalsTableRow> curatorTotalsTableRows = new ArrayList<>();
        Collection<Curator> allCurators = curatorRepository.findAll();

        // This map will hold our date range and the associated curator and total
        Map<DateRange, Map<Curator, AtomicInteger>> dateMap = createDateMap(allCurators);

        // Get filter text
        String filterYear = studySearchFilter.getYearFilter();
        String filterMonth = studySearchFilter.getMonthFilter();

        for (DateRange dateRange : dateMap.keySet()) {
            for (Curator curator : dateMap.get(dateRange).keySet()) {
                CuratorTotalsTableRow row = new CuratorTotalsTableRow();
                row.setCurator(curator.getLastName());
                row.setCuratorTotalEntries(dateMap.get(dateRange).get(curator).get());

                // Handle filters
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateRange.getDateTo());
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));

                String month = new SimpleDateFormat("MMM").format(cal.getTime());
                row.setMonth(month);

                String year = new SimpleDateFormat("yyyy").format(cal.getTime());
                row.setYear(year);

                row.setPeriod(year + " " + month);
                // If user entered a year
                if (filterYear != null && !filterYear.isEmpty()) {
                    // If we have year and month find by both
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
                // If user entered curator
                else if (filterMonth != null && !filterMonth.isEmpty()) {
                    if (filterMonth.equalsIgnoreCase(month)) {
                        curatorTotalsTableRows.add(row);
                    }
                }

                // If all else fails return all studies
                else {
                    curatorTotalsTableRows.add(row);
                }
            }
        }

        model.addAttribute("curatorTotalsTableRows", curatorTotalsTableRows);
        return "reports";
    }

/*General purpose methods*/

    private Map<DateRange, Map<Curator, AtomicInteger>> createDateMap(Collection<Curator> allCurators) {

        Map<DateRange, Map<Curator, AtomicInteger>> dateMap = new HashMap<>();

        // This loop will set up our index of dates and associated curators and their totals
        for (Curator curator : allCurators) {

            // Get all studies assigned to that curator
            Collection<Study> allStudies = studyRepository.findByCuratorOrderByStudyDateDesc(curator.getId());

            for (Study study : allStudies) {

                // Get study date
                Date studyDate = study.getStudyDate();

                boolean found = false;

                // For each date range kep
                for (DateRange dateRange : dateMap.keySet()) {

                    if (dateRange.contains(studyDate)) {
                        Map<Curator, AtomicInteger> curatorTotals = dateMap.get(dateRange);
                        if (curatorTotals.containsKey(curator)) {
                            curatorTotals.get(curator).getAndIncrement();

                        } else {
                            curatorTotals.put(curator, new AtomicInteger(1));
                        }
                        found = true;
                        break;
                    }
                }

                // If the date is not in our index
                if (!found) {

                    //Create a new date range
                    Calendar calDateFrom = Calendar.getInstance();
                    calDateFrom.setTime(studyDate);

                    // If its January we need to set range from end of Dec of last year
                    if (calDateFrom.get(Calendar.MONTH) == 1) {
                        calDateFrom.set(Calendar.YEAR, calDateFrom.get(Calendar.YEAR) - 1);
                        calDateFrom.set(Calendar.MONTH, Calendar.DECEMBER);
                        calDateFrom.set(Calendar.DAY_OF_MONTH, calDateFrom.getActualMaximum(Calendar.DAY_OF_MONTH));
                        calDateFrom.set(Calendar.HOUR_OF_DAY, 23);
                        calDateFrom.set(Calendar.MINUTE, 59);
                        calDateFrom.set(Calendar.SECOND, 59);
                        //   calDateFrom.set(Calendar.MILLISECOND, 000);


                    } else {
                        calDateFrom.set(Calendar.MONTH, calDateFrom.get(Calendar.MONTH) - 1);
                        calDateFrom.set(Calendar.DAY_OF_MONTH, calDateFrom.getActualMaximum(Calendar.DAY_OF_MONTH));
                        calDateFrom.set(Calendar.HOUR_OF_DAY, 23);
                        calDateFrom.set(Calendar.MINUTE, 59);
                        calDateFrom.set(Calendar.SECOND, 59);
                        //   calDateFrom.set(Calendar.MILLISECOND, 000);
                    }


                    Date dateFrom = calDateFrom.getTime();

                    Calendar calDateTo = Calendar.getInstance();
                    calDateTo.setTime(studyDate);
                    calDateTo.set(Calendar.MONTH, calDateTo.get(Calendar.MONTH));
                    calDateTo.set(Calendar.DAY_OF_MONTH, calDateTo.getActualMaximum(Calendar.DAY_OF_MONTH));
                    calDateTo.set(Calendar.HOUR_OF_DAY, 23);
                    calDateTo.set(Calendar.MINUTE, 59);
                    calDateTo.set(Calendar.SECOND, 59);
                    //       calDateTo.set(Calendar.MILLISECOND, 000);
                    Date dateTo = calDateTo.getTime();

                    DateRange dateRange = new DateRange(dateFrom, dateTo);

                    // Won't have any totals for this date already so set up our index
                    Map<Curator, AtomicInteger> curatorTotals = new HashMap<>();
                    curatorTotals.put(curator, new AtomicInteger(1));
                    dateMap.put(dateRange, curatorTotals);
                }
            }
        } // End of curator for loop
        return dateMap;
    }


    // Curators
    @ModelAttribute("months")
    public String[] populateMonths(Model model) {
        String[] shortMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return shortMonths;
    }

    // Curation statuses
    @ModelAttribute("years")
    public List<String> populateYears(Model model) {
        List<String> years = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int endYear = 2005;
        while (year >= endYear) {
            String stringYear = String.valueOf(year);
            years.add(stringYear);
            year--;
        }
        return years;
    }

}