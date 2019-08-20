package uk.ac.ebi.spot.goci.model.deposition.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionAssociationListWrapper extends ResourceSupport {
    @JsonProperty(value = "_embedded")
    private DepositionAssociationList associations;
    private DepositionPageInfo page;
}
