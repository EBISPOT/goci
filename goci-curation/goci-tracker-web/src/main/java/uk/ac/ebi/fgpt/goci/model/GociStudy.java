package uk.ac.ebi.fgpt.goci.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * A possible genome-wide association study that has been entered into this tracking system.  Each study is assigned, at
 * any given time, an owner.  Studies can be assigned one of several states, depending on the extent to which it has
 * been curated, and can have a flag set to indicate whether it is eligible or not eligible for the GWAS catalog.
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
public interface GociStudy {
    /**
     * Returns the unique ID for this study in the GOCI tracking system.  This is not the same as the PubMed ID
     *
     * @return the unique ID for this study in GOCI.
     */
    String getID();

    /**
     * Returns the ID assigned to this study in PubMed
     *
     * @return a string representing the PubMed ID.
     */
    String getPubMedID();

    /**
     * Returns the title of the paper for this study
     *
     * @return the study's title
     */
    String getTitle();

    /**
     * Returns the abstract of the paper for this study
     *
     * @return the paper abstract
     */
    String getPaperAbstract();

    /**
     * Returns the current owner of this study, or null if it is unassigned
     *
     * @return the owner of the study
     */
    GociUser getOwner();

    /**
     * Returns the current state of this study, as determined by it's current curation progress
     *
     * @return the curation state of this study currently
     */
    State getState();

    /**
     * Returns an indication as to whether this study is currently thought to be GWAS eligible or not.  If not yet
     * known, {@link Eligibility#Unknown} is returned.
     *
     * @return whether this study is GWAS eligible or not
     */
    Eligibility getGwasEligibility();

    public enum State {
        New_publication,
        GWAS_eligibility_rejected,
        GWAS_eligibility_confirmed,
        Added_to_GWAS,
        Ethnicity_extracted,
        Ethnicity_checked,
        SNPs_extracted,
        SNPs_checked,
        NCBI_checked,
        Published_to_catalog
    }

    public enum Eligibility {
        Unknown,
        Not_GWAS,
        GWAS
    }
}
