package uk.ac.ebi.spot.goci.curation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@Relation(value = "study", collectionRelation = "studies")
@JsonPropertyOrder({
        "Accession ID",
        "Author",
        "Title",
        "Publication Date",
        "Pubmed ID",
        "Publication",
        "Disease/Trait",
        "EFO Trait",
        "Curator",
        "Curation Status",
        "Notes"
})
public class StudyDto {

    @JsonIgnore
    private Long id;

    @JsonProperty("Accession ID")
    private String accession;

    @JsonProperty("Author")
    private String author;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Publication Date")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="BST")
    private Date publicationDate;

    @JsonProperty("Pubmed ID")
    private String pubmedId;

    @JsonProperty("Publication")
    private String publication;

    @JsonProperty("Disease/Trait")
    private String diseaseTrait;

    @JsonProperty("EFO Trait")
    private String efoTrait;

    @JsonProperty("Curator")
    private String curator;

    @JsonProperty("Curation Status")
    private String curationStatus;

    @JsonProperty("Notes")
    private String notes;
}