package uk.ac.ebi.spot.goci.model.reports;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

    @Column(name = "studies_created")
    private String studiesCreated;

    @Column(name = "studies_level1")
    private String studiesLevel1Completed;

    @Column(name = "studies_level2")
    private String studiesLevel2Completed;

    @Column(name = "studies_published")
    private String studiesPublished;

    @Column(name = "pubs_created")
    private String publicationsCreated;

    @Column(name = "pubs_level1")
    private String publicationsLevel1Completed;

    @Column(name = "pubs_level2")
    private String publicationsLevel2Completed;

    @Column(name = "pubs_published")
    private String publicationsPublished;
}
