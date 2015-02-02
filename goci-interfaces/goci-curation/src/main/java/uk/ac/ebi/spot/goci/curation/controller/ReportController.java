package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.spot.goci.curation.model.CuratorTotalsTableRow;
import uk.ac.ebi.spot.goci.curation.model.DateRange;
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
                    calDateFrom.set(Calendar.DAY_OF_MONTH, calDateFrom.getActualMinimum(Calendar.DAY_OF_MONTH));
                    calDateFrom.set(Calendar.HOUR_OF_DAY, 0);
                    calDateFrom.set(Calendar.MINUTE, 0);
                    calDateFrom.set(Calendar.SECOND, 0);
                    calDateFrom.set(Calendar.MILLISECOND, 0);
                    Date dateFrom = calDateFrom.getTime();

                    Calendar calDateTo = Calendar.getInstance();
                    calDateTo.setTime(studyDate);
                    calDateTo.set(Calendar.DAY_OF_MONTH, calDateTo.getActualMaximum(Calendar.DAY_OF_MONTH));
                    calDateTo.set(Calendar.HOUR_OF_DAY, 23);
                    calDateTo.set(Calendar.MINUTE, 59);
                    calDateTo.set(Calendar.SECOND, 59);
                    calDateTo.set(Calendar.MILLISECOND, 999);
                    Date dateTo = calDateTo.getTime();

                    DateRange dateRange = new DateRange(dateFrom, dateTo);

                    // Won't have any totals for this date already so set up our index
                    Map<Curator, AtomicInteger> curatorTotals = new HashMap<>();
                    curatorTotals.put(curator, new AtomicInteger(1));
                    dateMap.put(dateRange, curatorTotals);
                }
            }
        } // End of curator for loop

        for (DateRange dateRange : dateMap.keySet()) {

            for (Curator curator : dateMap.get(dateRange).keySet()) {
                CuratorTotalsTableRow row = new CuratorTotalsTableRow();
                row.setCurator(curator.getLastName());
                row.setCuratorTotalEntries(dateMap.get(dateRange).get(curator).get());
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(dateRange.getDateFrom());


                String month = new SimpleDateFormat("MMM").format(cal1.getTime());

                row.setMonth(month);

                String year = new SimpleDateFormat("yyyy").format(cal1.getTime());

                row.setYear(year);
                curatorTotalsTableRows.add(row);
            }
        }
        model.addAttribute("curatorTotalsTableRows", curatorTotalsTableRows);
        return "reports";
    }
}
