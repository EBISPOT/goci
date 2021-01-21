package uk.ac.ebi.spot.goci.curation.controller.assembler;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.dto.HousekeepingDto;
import uk.ac.ebi.spot.goci.curation.dto.StudyDto;
import uk.ac.ebi.spot.goci.curation.controller.rest.StudyRestController;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Optional;

@Component
public class StudyDtoAssembler implements ResourceAssembler<Study, Resource<StudyDto>> {

        public Resource<StudyDto> toResource(Study study) {
            StudyDto studyDto = StudyDto.builder()
                    .id(study.getId())
                    .accessionId(study.getAccessionId())
                    .fullPvalueSet(study.getFullPvalueSet())
                    .initialSampleSize(study.getInitialSampleSize())
                    .snpCount(study.getSnpCount())
                    .housekeepingDto(this.toHousekeepingModel(study.getHousekeeping()))
                    .build();

        final ControllerLinkBuilder controllerLinkBuilder = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(StudyRestController.class).getOneStudy(study.getId()));

        Resource<StudyDto> resource = new Resource<>(studyDto);
        resource.add(controllerLinkBuilder.withSelfRel());
        return resource;
    }


    private HousekeepingDto toHousekeepingModel(Housekeeping housekeeping) {
        Optional<Housekeeping> optionalHousekeeping = Optional.ofNullable(housekeeping);
        return optionalHousekeeping
                .map(hk -> HousekeepingDto.builder()
                        .id(housekeeping.getId())
                        .isPublished(housekeeping.getIsPublished())
                        .ancestryBackFilled(housekeeping.getAncestryBackFilled())
                        .catalogPublishDate(housekeeping.getCatalogPublishDate())
                        .catalogUnpublishDate(housekeeping.getCatalogUnpublishDate())
                        .fileName(housekeeping.getFileName())
                        .build())
                .orElseGet(HousekeepingDto::new);
    }

}


