package uk.ac.ebi.spot.goci.curation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Relation(value = "study", collectionRelation = "studies")
public class StudyDto {

    private Long id;

    @JsonProperty("author")
    private String author;

    @JsonProperty("title")
    private String title;

    @JsonProperty("publication_date")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="BST")
    private Date publicationDate;

    @JsonProperty("pubmed_id")
    private String pubmedId;

    @JsonProperty("publication")
    private String publication;

    @JsonProperty("disease_trait")
    private String diseaseTrait;

    @JsonProperty("efo_trait")
    private String efoTrait;

    @JsonProperty("curator")
    private String curator;

    @JsonProperty("curation_status")
    private String curationStatus;

    @JsonProperty("notes")
    private String notes;

}