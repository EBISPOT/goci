package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.core.Relation;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Relation(value = "diseaseTrait", collectionRelation = "diseaseTraits")
public class DiseaseTraitDto {

    private Long id;

    private String trait;

    private Integer studies;

    private String mongoSeqId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiseaseTraitDto that = (DiseaseTraitDto) o;
        return trait.equalsIgnoreCase(that.trait);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trait);
    }
}
