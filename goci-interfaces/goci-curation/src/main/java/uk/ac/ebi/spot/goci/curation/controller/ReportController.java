package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.spot.goci.curation.service.CuratorTotalsTableRow;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;

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

    // Return all studies
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String getCountsTable(Model model) {

        Collection<CuratorTotalsTableRow> curatorTotalsTableRows = new ArrayList<>();
        Collection<Curator> allCurators = curatorRepository.findAll();

        //   Long count =studyRepository.countByStudyDate();

        for (Curator curator : allCurators) {
            DateFormatSymbols dfs = new DateFormatSymbols();

            String[] arrayOfMonthsNames = dfs.getMonths();

            // Loop over each month name
            for (String monthName : arrayOfMonthsNames) {

                CuratorTotalsTableRow row = new CuratorTotalsTableRow();
                row.setMonth(monthName);
                row.setCurator(curator);

                Collection<Study> allStudies = studyRepository.findByCuratorOrderByStudyDate(curator.getId());


            }


        }

        model.addAttribute("curatorTotalsTableRows", curatorTotalsTableRows);
        return "reports";
    }


}
