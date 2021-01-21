package uk.ac.ebi.spot.goci.curation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.core.Relation;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(value = "housekeeping", collectionRelation = "housekeepings")
public class HousekeepingDto {

    private Long id;

    @JsonProperty("ancestry_back_filled")
    private Boolean ancestryBackFilled;

    @JsonProperty("is_published")
    private Boolean isPublished;

    @JsonProperty("catalog_publish_date")
    private Date catalogPublishDate;

    @JsonProperty("catalog_unpublish_date")
    private Date catalogUnpublishDate;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("notes")
    private String notes;

}