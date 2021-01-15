package uk.ac.ebi.spot.goci.curation.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.goci.curation.controller.assembler.UnpublishedStudyDtoAssembler;
import uk.ac.ebi.spot.goci.curation.exception.ResourceNotFoundException;
import uk.ac.ebi.spot.goci.curation.dto.UnpublishedStudyDto;
import uk.ac.ebi.spot.goci.curation.service.UnpublishedStudyService;
import uk.ac.ebi.spot.goci.model.UnpublishedStudy;

@RestController
@RequestMapping("/api/v1/unpublished-studies")
public class UnpublishedStudyRestController {

    @Autowired
    private UnpublishedStudyService unpublishedStudyService;

    @Autowired
    private UnpublishedStudyDtoAssembler unpublishedStudyDtoAssembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public Object getUnpublishedStudies(PagedResourcesAssembler<UnpublishedStudy> assembler,
                                        @PageableDefault(size = 50) Pageable pageable) {

        Page<UnpublishedStudy> unpublishedStudies = unpublishedStudyService.getUnpublishedStudiesBySumStatsFile(pageable);
        final ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder
                        .methodOn(UnpublishedStudyRestController.class)
                        .getUnpublishedStudies(assembler, pageable)
        );
        return assembler.toResource(unpublishedStudies, unpublishedStudyDtoAssembler, linkBuilder.withSelfRel());
    }

    @GetMapping(value = "/{unpublishedStudyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UnpublishedStudyDto getOneUnpublishedStudy(@PathVariable Long unpublishedStudyId) {
        return unpublishedStudyService.getUnpublishedStudy(unpublishedStudyId)
                .map(diseaseTrait -> UnpublishedStudyDto.builder()
                        .accession(diseaseTrait.getAccession())
                        .genotypingTechnology(diseaseTrait.getGenotypingTechnology())
                        .build()
                ).orElseThrow(() -> new ResourceNotFoundException("Unpublished Study", unpublishedStudyId));
    }
}