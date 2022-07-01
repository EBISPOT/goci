package uk.ac.ebi.spot.goci.curation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyToViewDto {

    private Long diseaseTrait;
    private List<Long> mainEfoTraits;
    private List<Long> mappedBackgroundTraits;
    private Long backgroundTrait;

    private List<DiseaseTrait> diseaseTraits;
    private String diseaseTraitsHtml;
    private List<EfoTrait> efoTraits;
    private String efoTraitsHtml;

}
