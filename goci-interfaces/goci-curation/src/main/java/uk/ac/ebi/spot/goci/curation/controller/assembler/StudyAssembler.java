package uk.ac.ebi.spot.goci.curation.controller.assembler;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.controller.rest.StudyRestController;
import uk.ac.ebi.spot.goci.curation.dto.StudyDto;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class StudyAssembler implements ResourceAssembler<Study, Resource<StudyDto>> {

    public Resource<StudyDto> toResource(Study study) {

        StudyDto studyDto = assemble(study);
        final ControllerLinkBuilder controllerLinkBuilder = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(StudyRestController.class).getOneStudy(study.getId()));

        Resource<StudyDto> resource = new Resource<>(studyDto);
        resource.add(controllerLinkBuilder.slash(study.getId()).withSelfRel());
        return resource;
    }

    public StudyDto assemble(Study study){

        return StudyDto.builder()
                .id(study.getId())
                .author(study.getPublicationId().getFirstAuthor().getFullname())
                .title(study.getPublicationId().getTitle())
                .publicationDate(study.getPublicationId().getPublicationDate())
                .pubmedId(study.getPublicationId().getPubmedId())
                .publication(study.getPublicationId().getPublication())
                .diseaseTrait((Optional.ofNullable(study.getDiseaseTrait()).isPresent()) ? study.getDiseaseTrait().getTrait() : "")
                .efoTrait(study.getEfoTraits().stream().map(EfoTrait::getTrait).collect(Collectors.joining(", ")))
                .curator(study.getHousekeeping().getCurator().getLastName())
                .curationStatus(study.getHousekeeping().getCurationStatus().getStatus())
                .notes(study.getNotes().stream().map(note -> String.format("%s %s %s",
                                                                           note.getTextNote(),
                                                                           note.getCurator().getLastName(),
                                                                           note.getUpdatedAt())).collect(Collectors.joining(" | ")))
                .build();
    }

    public List<StudyDto> assemble(List<Study> studies){

        List<StudyDto> studyDtos = new ArrayList<>();
        studies.forEach(study -> studyDtos.add(assemble(study)));
        return studyDtos;
    }


}
