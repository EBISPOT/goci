package uk.ac.ebi.spot.goci.curation.assembler;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.model.UnpublishedStudyDto;
import uk.ac.ebi.spot.goci.curation.rest.UnpublishedStudyController;
import uk.ac.ebi.spot.goci.model.UnpublishedStudy;

@Component
public class UnpublishedStudyDtoAssembler implements ResourceAssembler<UnpublishedStudy, Resource<UnpublishedStudyDto>> {

    public Resource<UnpublishedStudyDto> toResource(UnpublishedStudy unpublishedStudy) {
        UnpublishedStudyDto unpublishedStudyDto = UnpublishedStudyDto.builder()
                .id(unpublishedStudy.getId())
                .accession(unpublishedStudy.getAccession())
                .arrayManufacturer(unpublishedStudy.getArrayManufacturer())
                .genotypingTechnology(unpublishedStudy.getGenotypingTechnology())
                .build();

        final ControllerLinkBuilder controllerLinkBuilder = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UnpublishedStudyController.class).getOneUnpublishedStudy(unpublishedStudy.getId()));

        Resource<UnpublishedStudyDto> resource = new Resource<>(unpublishedStudyDto);
        resource.add(controllerLinkBuilder.withSelfRel());
        return resource;
    }
}