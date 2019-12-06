package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
public class PublicationCorrespondingAuthor {
    @Id
    @GeneratedValue
    private Long id;

    private String correspondingAuthorName;

    private String correspondingAuthorEmail;

    private String correspondingAuthorOrcId;

    @OneToOne(cascade = {CascadeType.ALL})
    @JsonManagedReference("publicationInfo")
    @JoinColumn(name = "publication_id")
    private Publication publicationId;


}
