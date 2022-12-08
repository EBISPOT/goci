package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionPageInfo;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionSubmissionList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionSubmissionDto {

    @JsonProperty(value = "_embedded")
    private DepositionSubmissionList wrapper;

    private DepositionPageInfo page;
}

