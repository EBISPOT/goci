package uk.ac.ebi.spot.goci.model;

/**
 * Created by emma on 26/01/2016.
 *
 * @author emma
 *         <p>
 *         Object to hold old mapping errors and new mapping errors. This class is used when database is mapped to a new
 *         release and curators need to compare the old/new mapping errors.
 */
public class MappingErrorComparisonReport {

    private Long studyId;

    private String pubmedId;

    private Long associationId;

    private String oldSnpError;

    private String newSnpError;

    private String oldSnpGeneOnDiffChr;

    private String newSnpGeneOnDiffChr;

    private String oldNoGeneForSymbol;

    private String newNoGeneForSymbol;

    private String oldRestServiceError;

    private String newRestServiceError;

    private String oldSuspectVariationError;

    private String newSuspectVariationError;

    private String oldGeneError;

    private String newGeneError;


    public MappingErrorComparisonReport() {
    }

    public MappingErrorComparisonReport(Long studyId,
                                        String pubmedId,
                                        Long associationId,
                                        String oldSnpError,
                                        String oldSnpGeneOnDiffChr,
                                        String oldNoGeneForSymbol,
                                        String oldRestServiceError,
                                        String oldSuspectVariationError,
                                        String oldGeneError,
                                        String newSnpError,
                                        String newSnpGeneOnDiffChr,
                                        String newNoGeneForSymbol,
                                        String newRestServiceError,
                                        String newSuspectVariationError, String newGeneError) {
        this.studyId = studyId;
        this.pubmedId = pubmedId;
        this.associationId = associationId;
        this.oldSnpError = oldSnpError;
        this.oldSnpGeneOnDiffChr = oldSnpGeneOnDiffChr;
        this.oldNoGeneForSymbol = oldNoGeneForSymbol;
        this.oldRestServiceError = oldRestServiceError;
        this.oldSuspectVariationError = oldSuspectVariationError;
        this.oldGeneError = oldGeneError;
        this.newSnpError = newSnpError;
        this.newSnpGeneOnDiffChr = newSnpGeneOnDiffChr;
        this.newNoGeneForSymbol = newNoGeneForSymbol;
        this.newRestServiceError = newRestServiceError;
        this.newSuspectVariationError = newSuspectVariationError;
        this.newGeneError = newGeneError;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public String getOldSnpError() {
        return oldSnpError;
    }

    public void setOldSnpError(String oldSnpError) {
        this.oldSnpError = oldSnpError;
    }

    public String getOldSnpGeneOnDiffChr() {
        return oldSnpGeneOnDiffChr;
    }

    public void setOldSnpGeneOnDiffChr(String oldSnpGeneOnDiffChr) {
        this.oldSnpGeneOnDiffChr = oldSnpGeneOnDiffChr;
    }

    public String getOldNoGeneForSymbol() {
        return oldNoGeneForSymbol;
    }

    public void setOldNoGeneForSymbol(String oldNoGeneForSymbol) {
        this.oldNoGeneForSymbol = oldNoGeneForSymbol;
    }

    public String getOldRestServiceError() {
        return oldRestServiceError;
    }

    public void setOldRestServiceError(String oldRestServiceError) {
        this.oldRestServiceError = oldRestServiceError;
    }

    public String getOldSuspectVariationError() {
        return oldSuspectVariationError;
    }

    public void setOldSuspectVariationError(String oldSuspectVariationError) {
        this.oldSuspectVariationError = oldSuspectVariationError;
    }

    public String getOldGeneError() {
        return oldGeneError;
    }

    public void setOldGeneError(String oldGeneError) {
        this.oldGeneError = oldGeneError;
    }

    public String getNewSnpError() {
        return newSnpError;
    }

    public void setNewSnpError(String newSnpError) {
        this.newSnpError = newSnpError;
    }

    public String getNewSnpGeneOnDiffChr() {
        return newSnpGeneOnDiffChr;
    }

    public void setNewSnpGeneOnDiffChr(String newSnpGeneOnDiffChr) {
        this.newSnpGeneOnDiffChr = newSnpGeneOnDiffChr;
    }

    public String getNewNoGeneForSymbol() {
        return newNoGeneForSymbol;
    }

    public void setNewNoGeneForSymbol(String newNoGeneForSymbol) {
        this.newNoGeneForSymbol = newNoGeneForSymbol;
    }

    public String getNewRestServiceError() {
        return newRestServiceError;
    }

    public void setNewRestServiceError(String newRestServiceError) {
        this.newRestServiceError = newRestServiceError;
    }

    public String getNewSuspectVariationError() {
        return newSuspectVariationError;
    }

    public void setNewSuspectVariationError(String newSuspectVariationError) {
        this.newSuspectVariationError = newSuspectVariationError;
    }

    public String getNewGeneError() {
        return newGeneError;
    }

    public void setNewGeneError(String newGeneError) {
        this.newGeneError = newGeneError;
    }
}
