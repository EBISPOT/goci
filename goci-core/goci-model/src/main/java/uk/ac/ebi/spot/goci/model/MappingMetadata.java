package uk.ac.ebi.spot.goci.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by emma on 28/09/2015.
 * <p>
 * Model object representing information stored about an Ensembl release
 */
@Entity
public class MappingMetadata {

    @Id
    @GeneratedValue
    private Long id;

    private Integer ensemblReleaseNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date usageStartDate;


    // JPA no-args constructor
    public MappingMetadata() {
    }

    public MappingMetadata(Integer ensemblReleaseNumber, Date usageStartDate) {
        this.ensemblReleaseNumber = ensemblReleaseNumber;
        this.usageStartDate = usageStartDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEnsemblReleaseNumber() {
        return ensemblReleaseNumber;
    }

    public void setEnsemblReleaseNumber(Integer ensemblReleaseNumber) {
        this.ensemblReleaseNumber = ensemblReleaseNumber;
    }

    public Date getUsageStartDate() {
        return usageStartDate;
    }

    public void setUsageStartDate(Date usageStartDate) {
        this.usageStartDate = usageStartDate;
    }
}
