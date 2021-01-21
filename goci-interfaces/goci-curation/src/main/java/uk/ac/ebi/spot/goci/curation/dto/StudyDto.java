package uk.ac.ebi.spot.goci.curation.dto;

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
@Relation(value = "study", collectionRelation = "studies")
public class StudyDto {

    private Long id;

    @JsonProperty("initial_sample_size")
    private String initialSampleSize;

    @JsonProperty("snp_count")
    private Integer snpCount;

    @JsonProperty("accession_id")
    private String accessionId;

    @JsonProperty("full_pvalue_set")
    private Boolean fullPvalueSet;

    @JsonProperty("user_requested")
    private Boolean userRequested;

    @JsonProperty("open_targets")
    private Boolean openTargets;

    @JsonProperty("housekeeping")
    private HousekeepingDto housekeepingDto;

}
