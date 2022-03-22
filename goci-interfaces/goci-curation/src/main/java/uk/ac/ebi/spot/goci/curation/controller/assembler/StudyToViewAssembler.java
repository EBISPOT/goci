package uk.ac.ebi.spot.goci.curation.controller.assembler;

import uk.ac.ebi.spot.goci.curation.dto.StudyToViewDto;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudyToViewAssembler {

    public static StudyToViewDto assemble(Study studyToView){
        Long diseaseTrait = Optional.ofNullable(studyToView.getDiseaseTrait()).map(DiseaseTrait::getId).orElse(0L);
        List<Long> mainEfoTraits = studyToView.getEfoTraits().stream().map(EfoTrait::getId).collect(Collectors.toList());
        List<Long> mappedBackgroundTraits = studyToView.getMappedBackgroundTraits().stream().map(EfoTrait::getId).collect(Collectors.toList());
        Long backgroundTrait = Optional.ofNullable(studyToView.getBackgroundTrait()).map(DiseaseTrait::getId).orElse(0L);

        return StudyToViewDto.builder()
                .diseaseTrait(diseaseTrait)
                .mainEfoTraits(mainEfoTraits)
                .mappedBackgroundTraits(mappedBackgroundTraits)
                .backgroundTrait(backgroundTrait)
                .build();
    }
}
