package uk.ac.ebi.spot.goci.model.reports;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class WeeklyReport {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private Date timestamp;

    @Column(name = "week_code")
    private Long weekCode;

    @Column(name = "type")
    private String type;

    @Column(name = "week_date")
    private Date weekDate;

    @Lob
    @Column(name = "studies_created")
    private String studiesCreated;

    @Lob
    @Column(name = "studies_level1")
    private String studiesLevel1Completed;

    @Lob
    @Column(name = "studies_level2")
    private String studiesLevel2Completed;

    @Lob
    @Column(name = "studies_published")
    private String studiesPublished;

    @Lob
    @Column(name = "pubs_created")
    private String publicationsCreated;

    @Lob
    @Column(name = "pubs_level1")
    private String publicationsLevel1Completed;

    @Lob
    @Column(name = "pubs_level2")
    private String publicationsLevel2Completed;

    @Lob
    @Column(name = "pubs_published")
    private String publicationsPublished;
}
