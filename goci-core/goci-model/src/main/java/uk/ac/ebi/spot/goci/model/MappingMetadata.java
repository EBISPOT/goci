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

    private String genomeBuildVersion;

    private Integer dbsnpVersion;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date usageStartDate;


    // JPA no-args constructor
    public MappingMetadata() {
    }

    public MappingMetadata(Integer ensemblReleaseNumber,
                           String genomeBuildVersion,
                           Integer dbsnpVersion,
                           Date usageStartDate) {
        this.ensemblReleaseNumber = ensemblReleaseNumber;
        this.genomeBuildVersion = genomeBuildVersion;
        this.dbsnpVersion = dbsnpVersion;
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

    public String getGenomeBuildVersion() {
        return genomeBuildVersion;
    }

    public void setGenomeBuildVersion(String genomeBuildVersion) {
        this.genomeBuildVersion = genomeBuildVersion;
    }

    public Integer getDbsnpVersion() {
        return dbsnpVersion;
    }

    public void setDbsnpVersion(Integer dbsnpVersion) {
        this.dbsnpVersion = dbsnpVersion;
    }
}
