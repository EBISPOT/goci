package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.goci.model.deposition.BodyOfWorkDto;

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
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(name = "pub_id")
    @JsonProperty("publication_id")
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
    @EqualsAndHashCode.Exclude
    private Set<UnpublishedStudy> studies;

    public static BodyOfWork create(BodyOfWorkDto dto){
        BodyOfWork bodyOfWork = BeanMapper.MAPPER.convert(dto);
        bodyOfWork.setPublicationId(dto.getBodyOfWorkId());
        bodyOfWork.setPubMedId(dto.getPmids() != null ? dto.getPmids().get(0) : null);
        //bodyOfWork.setPublicationDate(dto.get);
        if(dto.getDoi() == null) {
            bodyOfWork.setDoi(dto.getPreprintServerDOI());
        }
        return bodyOfWork;
    }

    public void update(BodyOfWork newBom){
        publicationId = newBom.getPublicationId();
        pubMedId = newBom.getPubMedId();
        journal = newBom.getJournal();
        title = newBom.getTitle();
        firstAuthor = newBom.getFirstAuthor();
        publicationDate = newBom.getPublicationDate();
        doi = newBom.getDoi();
    }
}
