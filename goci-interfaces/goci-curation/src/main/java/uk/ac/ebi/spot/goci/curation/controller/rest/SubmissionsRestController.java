package uk.ac.ebi.spot.goci.curation.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.goci.curation.constants.Endpoint;
import uk.ac.ebi.spot.goci.curation.service.deposition.DepositionSubmissionService;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionAssociationListWrapper;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionSampleListWrapper;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionStudyListWrapper;

@Slf4j
@RestController
@RequestMapping(Endpoint.API_V1 + Endpoint.SUBMISSIONS)
public class SubmissionsRestController {

    @Autowired
    private DepositionSubmissionService service;

    @GetMapping(value = "/{submissionId}/" + Endpoint.SAMPLES, produces = MediaTypes.HAL_JSON_VALUE)
    public DepositionSampleListWrapper getSubmissionSamples(@PathVariable String submissionId,
                                                            @SortDefault(sort = "study_tag", direction = Sort.Direction.DESC)
                                                            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return service.getSubmissionSamples(pageable, submissionId);
    }

    @GetMapping(value = "/{submissionId}/" + Endpoint.STUDIES, produces = MediaTypes.HAL_JSON_VALUE)
    public DepositionStudyListWrapper getSubmissionStudies(@PathVariable String submissionId,
                                                           @SortDefault(sort = "study_tag", direction = Sort.Direction.DESC)
                                                           @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return service.getSubmissionStudies(pageable, submissionId);
    }

    @GetMapping(value = "/{submissionId}/" + Endpoint.ASSOCIATIONS, produces = MediaTypes.HAL_JSON_VALUE)
    public DepositionAssociationListWrapper getSubmissionAssociations(@PathVariable String submissionId,
                                                                      @SortDefault(sort = "study_tag", direction = Sort.Direction.DESC)
                                                                      @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return service.getSubmissionAssociations(pageable, submissionId);
    }

}

