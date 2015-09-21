package uk.ac.ebi.spot.goci.curation.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by emma on 13/03/15.
 *
 * @author emma
 *         <p>
 *         Model class to handle information sent to curators as part of a daily audit email
 */
public class StudyAuditView {

    private String title;

    private String pubmedId;

    private Long studyId;

    private String author;

    private Date sendToNCBIDate;

    private Date publicationDate;

    private Long pubmedIdError;

    private List<String> snpErrors = new ArrayList<String>();

    private List<String> geneNotOnGenomeErrors = new ArrayList<String>();

    private List<String> snpGeneOnDiffChrErrors = new ArrayList<String>();

    private List<String> noGeneForSymbolErrors = new ArrayList<String>();

    public StudyAuditView(String title,
                          String pubmedId,
                          Long studyId,
                          String author,
                          Date sendToNCBIDate,
                          Date publicationDate,
                          Long pubmedIdError,
                          List<String> snpErrors,
                          List<String> geneNotOnGenomeErrors,
                          List<String> snpGeneOnDiffChrErrors,
                          List<String> noGeneForSymbolErrors) {
        this.title = title;
        this.pubmedId = pubmedId;
        this.studyId = studyId;
        this.author = author;
        this.sendToNCBIDate = sendToNCBIDate;
        this.publicationDate = publicationDate;
        this.pubmedIdError = pubmedIdError;
        this.snpErrors = snpErrors;
        this.geneNotOnGenomeErrors = geneNotOnGenomeErrors;
        this.snpGeneOnDiffChrErrors = snpGeneOnDiffChrErrors;
        this.noGeneForSymbolErrors = noGeneForSymbolErrors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getSendToNCBIDate() {
        return sendToNCBIDate;
    }

    public void setSendToNCBIDate(Date sendToNCBIDate) {
        this.sendToNCBIDate = sendToNCBIDate;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Long getPubmedIdError() {
        return pubmedIdError;
    }

    public void setPubmedIdError(Long pubmedIdError) {
        this.pubmedIdError = pubmedIdError;
    }

    public List<String> getSnpErrors() {
        return snpErrors;
    }

    public void setSnpErrors(List<String> snpErrors) {
        this.snpErrors = snpErrors;
    }

    public List<String> getGeneNotOnGenomeErrors() {
        return geneNotOnGenomeErrors;
    }

    public void setGeneNotOnGenomeErrors(List<String> geneNotOnGenomeErrors) {
        this.geneNotOnGenomeErrors = geneNotOnGenomeErrors;
    }

    public List<String> getSnpGeneOnDiffChrErrors() {
        return snpGeneOnDiffChrErrors;
    }

    public void setSnpGeneOnDiffChrErrors(List<String> snpGeneOnDiffChrErrors) {
        this.snpGeneOnDiffChrErrors = snpGeneOnDiffChrErrors;
    }

    public List<String> getNoGeneForSymbolErrors() {
        return noGeneForSymbolErrors;
    }

    public void setNoGeneForSymbolErrors(List<String> noGeneForSymbolErrors) {
        this.noGeneForSymbolErrors = noGeneForSymbolErrors;
    }
}
