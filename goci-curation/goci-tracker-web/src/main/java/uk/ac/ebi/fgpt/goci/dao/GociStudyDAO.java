package uk.ac.ebi.fgpt.goci.dao;

import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.model.GociUser;

import java.util.Collection;

/**
 * A DAO interface for accessing Studies from some underlying datasource
 *
 * @author Tony Burdett
 * @date 27/10/11
 */
public interface GociStudyDAO {
    /**
     * Returns the study with the given study ID, or null if there was no study with this ID
     *
     * @param studyID the ID of the study to retrieve
     * @return the study with the given ID
     */
    GociStudy getStudy(String studyID);

    /**
     * Returns a collection of all studies entered into the tracking system.
     *
     * @return the collection of all known studies
     */
    Collection<GociStudy> getAllStudies();
    
    /**
     * Returns a collection of studies entered into the tracking system that require further processing, i.e. that are GWAS eligible but have not yet been published to the catalog. 
     *
     * @return the collection of all studies that require further processing
     */
    Collection<GociStudy> getProcessableStudies();

    /**
     * Returns a collection of all studies with the given state.
     *
     * @param studyState the current state of studies to retrieve
     * @return the collection of studies that currently have the supplied state
     */
    Collection<GociStudy> getStudiesByState(GociStudy.State studyState);

    /**
     * Returns a collection of studies that are currently assigned to the supplied user
     *
     * @param user the user for whom we want to retrieve studies
     * @return the collection of studies currently assigned to the supplied user
     */
    Collection<GociStudy> getStudiesByUser(GociUser user);

    /**
     * Returns the study that relates to the assigned PubMed ID
     *
     * @param pubMedID the PubMed ID of study to retrieve
     * @return the study with this PubMed ID
     */
    GociStudy getStudyByPubMedID(String pubMedID);

    /**
     * Saves the supplied study in the underlying datasource.  This does an insert or update as appropriate, making the
     * assumption that a study without a set ID has never been saved in the database.  You should take care not to set
     * the ID yourself, as this will cause this save operation to fail.
     *
     * @param study the study to save or update
     */
    void saveStudy(GociStudy study);
}
