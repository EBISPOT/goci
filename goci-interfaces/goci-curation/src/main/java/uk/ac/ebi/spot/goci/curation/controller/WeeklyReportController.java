package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.spot.goci.curation.service.ReportService;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.WeeklyTotalsSummaryViewRepository;

/**
 * Created by dwelter on 15/04/16.
 */
@Controller
@RequestMapping("/reports/weekly")
public class WeeklyReportController {

    private WeeklyTotalsSummaryViewRepository weeklyTotalsSummaryViewRepository;
    private CuratorRepository curatorRepository;
    private CurationStatusRepository curationStatusRepository;

    // Service class
    private ReportService reportService;

    @Autowired
    public WeeklyReportController(WeeklyTotalsSummaryViewRepository weeklyTotalsSummaryViewRepository,
                                   CuratorRepository curatorRepository,
                                   CurationStatusRepository curationStatusRepository,
                                   ReportService reportService) {
        this.weeklyTotalsSummaryViewRepository = weeklyTotalsSummaryViewRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.reportService = reportService;
    }

}
