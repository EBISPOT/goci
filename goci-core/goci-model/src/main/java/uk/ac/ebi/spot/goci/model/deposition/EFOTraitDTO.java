package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.core.Relation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Relation(value = "efoTrait", collectionRelation = "efoTraits")
public class EFOTraitDTO {


    private String trait;


    private String uri;


    private String shortForm;


    private String mongoSeqId;
}
