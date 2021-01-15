package uk.ac.ebi.spot.goci.curation.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.goci.curation.controller.assembler.StudyDtoAssembler;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationService;
import uk.ac.ebi.spot.goci.model.Study;

@RestController
@RequestMapping("/api/v1/published-studies")
public class StudyRestController {

    @Autowired
    private StudyOperationService studyOperationService;

    @Autowired
    private StudyDtoAssembler studyDtoAssembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public Object getStudies(PagedResourcesAssembler<Study> assembler,
                                        @PageableDefault(size = 20) Pageable pageable) {

        Page<Study> studies = studyOperationService.getStudiesByHousekeepingStatus(pageable,true);
        final ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder
                        .methodOn(StudyRestController.class)
                        .getStudies(assembler, pageable)
        );
        return assembler.toResource(studies, studyDtoAssembler, linkBuilder.withSelfRel());
    }

    public Object getOneStudy(Long id) {
        return "";
    }
}
