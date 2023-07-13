package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionPageInfo;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionStudyList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositionStudyWrapperDto {

    @JsonProperty(value = "_embedded")
    private DepositionStudyList studyList;

    @JsonProperty(value = "page")
    private DepositionPageInfo page;
}
