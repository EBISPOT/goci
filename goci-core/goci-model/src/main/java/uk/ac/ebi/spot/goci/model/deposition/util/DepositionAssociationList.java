package uk.ac.ebi.spot.goci.model.deposition.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.goci.model.deposition.DepositionAssociationDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionPublication;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionAssociationList {
    
    List<DepositionAssociationDto> associations = new ArrayList<>();
}
