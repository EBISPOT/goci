package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.dto.SubmissionViewDto;
import uk.ac.ebi.spot.goci.curation.service.deposition.DepositionSubmissionService;
import uk.ac.ebi.spot.goci.curation.service.deposition.SubmissionImportProgressService;
import uk.ac.ebi.spot.goci.model.deposition.Submission;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionPageInfo;

import java.util.List;
import java.util.Map;

@Controller
public class SplitSubmissionController {

    private final DepositionSubmissionService submissionService;

    private final SubmissionImportProgressService submissionImportProgressService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Value("${deposition.ingest.uri}")
    private String depositionIngestURL;

    public SplitSubmissionController(@Autowired DepositionSubmissionService submissionService,
                                     SubmissionImportProgressService submissionImportProgressService) {
        this.submissionService = submissionService;
        this.submissionImportProgressService = submissionImportProgressService;
    }

    @RequestMapping(value = "/imported_submissions", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String importedSubmissionsPage(Model model,
                                          @SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        SubmissionViewDto submissionViewDto = submissionService.getSubmissionsByStatus("CURATION_COMPLETE", pageable);
        Map<String, Submission> submissionList = submissionViewDto.getSubmissionList();
        DepositionPageInfo pageInfo = submissionViewDto.getPage();

        model.addAttribute("submissions", submissionList.values());
        model.addAttribute("pageInfo", pageInfo);

        log.info("Page Number: {}", pageable.getPageNumber());
        log.info("Page SIze: {}", pageable.getPageSize());
        log.info("Offset: {}", pageable.getOffset());
        log.info("Sort: {}\n\n", pageable.getSort().toString());

        log.info("Begin Index: {}", submissionViewDto.getBeginIndex());
        log.info("Current Index: {}", submissionViewDto.getCurrentIndex());
        log.info("End Index: {}", submissionViewDto.getEndIndex());

        return "view_submissions";
    }

    @RequestMapping(value = "/in_progress_submissions", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String inProgressSubmissionsPage(Model model) {
        List<String> submissionIds = submissionImportProgressService.getSubmissions();
        Map<String, Submission> submissionList = submissionService.getSubmissionsById(submissionIds);
        model.addAttribute("submissions", submissionList.values());
        return "view_submissions";
    }

    @RequestMapping(value = "/failed_submissions", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String failedSubmissionsPage(Model model,
                                        @SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        SubmissionViewDto submissionViewDto  = submissionService.getSubmissionsByStatus("IMPORT_FAILED", pageable);
        Map<String, Submission> submissionList = submissionViewDto.getSubmissionList();
        DepositionPageInfo pageInfo = submissionViewDto.getPage();
        model.addAttribute("submissions", submissionList.values());
        model.addAttribute("pageInfo", pageInfo);
        return "view_submissions";
    }

    @RequestMapping(value = "/other_submissions", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String otherSubmissionsPage(Model model,
                                       @SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        SubmissionViewDto submissionViewDto = submissionService.getSubmissionsByStatus("OTHER", pageable);
        Map<String, Submission> submissionList = submissionViewDto.getSubmissionList();
        DepositionPageInfo pageInfo = submissionViewDto.getPage();
        model.addAttribute("submissions", submissionList.values());
        model.addAttribute("pageInfo", pageInfo);
        return "view_submissions";
    }
}
