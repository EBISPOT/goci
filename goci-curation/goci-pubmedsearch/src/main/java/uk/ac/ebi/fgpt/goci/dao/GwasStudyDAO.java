package uk.ac.ebi.fgpt.goci.dao;

import uk.ac.ebi.fgpt.goci.model.GwasStudy;

import java.util.Collection;

/**
 * A DAO interface for accessing Studies from some underlying datasource
 *
 * @author Tony Burdett
 * Date 27/10/11
 */
public interface GwasStudyDAO {

    /**
     * Returns a collection of all studies entered into the tracking system.
     *
     * @return the collection of all known studies
     */
    Collection<GwasStudy> getAllStudies();
    

    /**
     * Returns true or false, depending on whether a study with the assigned PubMed ID exists
     *
     * @param pubMedID the PubMed ID of study to retrieve
     * @return true or false
     */
    boolean getStudyByPubMedID(String pubMedID);

    /**
     * Saves the supplied study in the underlying datasource.  This does an insert or update as appropriate, making the
     * assumption that a study without a set ID has never been saved in the database.  You should take care not to set
     * the ID yourself, as this will cause this save operation to fail.
     *
     * @param study the study to save or update
     */
    void saveStudy(GwasStudy study);
}
