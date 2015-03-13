package uk.ac.ebi.spot.goci.curation.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emma on 13/03/15.
 *
 * @author emma
 *         <p>
 *         Model class to deal with errors returned from NCBI pipeline for idividual studies and collate into a single
 *         daily eamil
 */
public class StudyErrorView {

    private String title;

    private String pubmedId;

    private Long studyError;

    private List<String> snpErrors = new ArrayList<String>();

    private List<String> geneNotOnGenomeErrors = new ArrayList<String>();

    private List<String> snpGeneOnDiffChrErrors = new ArrayList<String>();

    private List<String> noGeneForSymbolErrors = new ArrayList<String>();

    public StudyErrorView(String title,
                          String pubmedId,
                          Long studyError,
                          List<String> snpErrors,
                          List<String> geneNotOnGenomeErrors,
                          List<String> snpGeneOnDiffChrErrors,
                          List<String> noGeneForSymbolErrors) {
        this.title = title;
        this.pubmedId = pubmedId;
        this.studyError = studyError;
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

    public Long getStudyError() {
        return studyError;
    }

    public void setStudyError(Long studyError) {
        this.studyError = studyError;
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
