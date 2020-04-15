package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BodyOfWorkDto {

    @JsonProperty("bodyOfWorkId")
    private String bodyOfWorkId;

    @NotEmpty
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("firstAuthor")
    private DepositionAuthor firstAuthor;

    @JsonProperty("lastAuthor")
    private DepositionAuthor lastAuthor;

    @JsonProperty("journal")
    private String journal;

    @JsonProperty("doi")
    private String doi;

    @JsonProperty("url")
    private String url;

    @JsonProperty("correspondingAuthors")
    private List<DepositionAuthor> correspondingAuthors;

    @JsonProperty("prePrintServer")
    private String prePrintServer;

    @JsonProperty("preprintServerDOI")
    private String preprintServerDOI;

    @JsonProperty("embargoDate")
    private LocalDate embargoDate;

    @JsonProperty("embargoUntilPublished")
    private Boolean embargoUntilPublished;

    @JsonProperty("pmids")
    private List<String> pmids;

    @JsonProperty("status")
    private String status;
}