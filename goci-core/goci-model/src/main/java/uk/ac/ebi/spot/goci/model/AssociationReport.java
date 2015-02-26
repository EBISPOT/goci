package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 12/02/15
 */
@Entity
public class AssociationReport {
    @Id
    @GeneratedValue
    private Long id;

    private boolean snpPending;

    private Date lastUpdateDate;

    private Long geneError;

    private String snpError;

    private String snpGeneOnDiffChr;

    private String noGeneForSymbol;

    private String geneNotOnGenome;

    @OneToOne
    private Association association;

    // JPA no-args constructor
    public AssociationReport() {
    }

    public AssociationReport(boolean snpPending,
                             Date lastUpdateDate,
                             Long geneError,
                             String snpError,
                             String snpGeneOnDiffChr,
                             String noGeneForSymbol,
                             String geneNotOnGenome,
                             Association association) {
        this.snpPending = snpPending;
        this.lastUpdateDate = lastUpdateDate;
        this.geneError = geneError;
        this.snpError = snpError;
        this.snpGeneOnDiffChr = snpGeneOnDiffChr;
        this.noGeneForSymbol = noGeneForSymbol;
        this.geneNotOnGenome = geneNotOnGenome;
        this.association = association;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSnpPending() {
        return snpPending;
    }

    public void setSnpPending(boolean snpPending) {
        this.snpPending = snpPending;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getGeneError() {
        return geneError;
    }

    public void setGeneError(Long geneError) {
        this.geneError = geneError;
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

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    @Override public String toString() {
        return "AssociationReport{" +
                "id=" + id +
                ", snpPending=" + snpPending +
                ", lastUpdateDate=" + lastUpdateDate +
                ", geneError=" + geneError +
                ", snpError='" + snpError + '\'' +
                ", snpGeneOnDiffChr='" + snpGeneOnDiffChr + '\'' +
                ", noGeneForSymbol='" + noGeneForSymbol + '\'' +
                ", geneNotOnGenome='" + geneNotOnGenome + '\'' +
                '}';
    }
}
