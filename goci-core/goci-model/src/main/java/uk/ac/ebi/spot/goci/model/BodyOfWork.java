package uk.ac.ebi.spot.goci.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class BodyOfWork{
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "pub_id")
    private String publicationId;
    private String pubMedId;
    private String journal;
    private String title;
    private String firstAuthor;
//    private DepositionAuthor correspondingAuthor;
    @Column(name = "pub_date")
    private Date publicationDate;
//    private String status;
    private String doi;

    @ManyToMany
    @JoinTable(name = "unpublished_study_to_work", joinColumns = @JoinColumn(name = "work_id"), inverseJoinColumns =
    @JoinColumn(name = "study_id"))
    private Set<UnpublishedStudy> studies;
}
