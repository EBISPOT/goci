package uk.ac.ebi.spot.goci.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by emma on 11/02/15.
 *
 * @author emma
 *         <p/>
 *         Model object representing an association report
 */
@Entity
public class AssociationReport {
    @Id
    @GeneratedValue
    private Long associationId;

    private Integer geneError;

    private Integer pubmedIdError;

    private String snpError;

    private String snpGeneOnDiffChr;

    private String noGeneForSymbol;

    private String geneNotOnGenome;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;


    // JPA no-args constructor
    public AssociationReport() {
    }

    public AssociationReport(Integer geneError, Integer pubmedIdError, String snpError, String snpGeneOnDiffChr, String noGeneForSymbol, String geneNotOnGenome, Date lastUpdateDate) {
        this.geneError = geneError;
        this.pubmedIdError = pubmedIdError;
        this.snpError = snpError;
        this.snpGeneOnDiffChr = snpGeneOnDiffChr;
        this.noGeneForSymbol = noGeneForSymbol;
        this.geneNotOnGenome = geneNotOnGenome;
        this.lastUpdateDate = lastUpdateDate;


    }

    public Integer getGeneError() {
        return geneError;
    }

    public void setGeneError(Integer geneError) {
        this.geneError = geneError;
    }

    public Integer getPubmedIdError() {
        return pubmedIdError;
    }

    public void setPubmedIdError(Integer pubmedIdError) {
        this.pubmedIdError = pubmedIdError;
    }

    public String getSnpError() {
        return snpError;
    }

    public void setSnpError(String snpError) {
        this.snpError = snpError;
    }

    public String getSnpGeneOnDiffChr() {
        return snpGeneOnDiffChr;
    }

    public void setSnpGeneOnDiffChr(String snpGeneOnDiffChr) {
        this.snpGeneOnDiffChr = snpGeneOnDiffChr;
    }

    public String getNoGeneForSymbol() {
        return noGeneForSymbol;
    }

    public void setNoGeneForSymbol(String noGeneForSymbol) {
        this.noGeneForSymbol = noGeneForSymbol;
    }

    public String getGeneNotOnGenome() {
        return geneNotOnGenome;
    }

    public void setGeneNotOnGenome(String geneNotOnGenome) {
        this.geneNotOnGenome = geneNotOnGenome;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }


    @Override
    public String toString() {
        return "AssociationReport{" +
                "geneError=" + geneError +
                ", pubmedIdError=" + pubmedIdError +
                ", snpError='" + snpError + '\'' +
                ", snpGeneOnDiffChr='" + snpGeneOnDiffChr + '\'' +
                ", noGeneForSymbol='" + noGeneForSymbol + '\'' +
                ", geneNotOnGenome='" + geneNotOnGenome + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }
}
