package uk.ac.ebi.spot.goci.curation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyViewDto {

    private String filters;
    private String sortString;
    private List<Study> studies;
    private Page<Study> studyPage;
    private StudySearchFilter studySearchFilter;

}
