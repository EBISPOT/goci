package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;
import org.springframework.hateoas.ResourceSupport;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionPublication{
    private String publicationId;
    private String pmid;
    private String journal;
    private String title;
    private String firstAuthor;
    private DepositionAuthor correspondingAuthor;
    private LocalDate publicationDate;
    private String status;
}
