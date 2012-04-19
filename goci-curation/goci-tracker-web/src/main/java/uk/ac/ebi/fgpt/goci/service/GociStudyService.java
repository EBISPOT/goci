package uk.ac.ebi.fgpt.goci.service;

import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.model.GociUser;

import java.util.Collection;

/**
 * A service that provides various methods for accessing the set of studies that are currently tracked.
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
public interface GociStudyService {
    /**
     * Returns the study with the given study ID, or null if there was no study with this ID
     *
     * @param studyID the ID of the study to retrieve
     * @return the study with the given ID
     */
    GociStudy retrieveStudy(String studyID);

    /**
     * Returns a collection of all studies entered into the tracking system.
     *
     * @return the collection of all known studies
     */
    Collection<GociStudy> retrieveAllStudies();
    
    /**
     * Returns a collection of studies entered into the tracking system that require further processing, i.e. that are GWAS eligible but have not yet been published to the catalog. 
     *
     * @return the collection of all studies that require further processing.
     */
    Collection<GociStudy> retrieveProcessableStudies();

    /**
     * Returns a collection of all studies with the given state.
     *
     * @param studyState the current state of studies to retrieve
     * @return the collection of studies that currently have the supplied state
     */
    Collection<GociStudy> retrieveStudiesByState(GociStudy.State studyState);

    /**
     * Returns a collection of studies that are currently assigned to the supplied user
     *
     * @param user the user for whom we want to retrieve studies
     * @return the collection of studies currently assigned to the supplied user
     */
    Collection<GociStudy> retrieveStudiesByUser(GociUser user);

    /**
     * Returns the study with the given pubmed ID, if it has been entered into the tracking system.
     *
     * @param pubmedID the pubmed ID of the study to retrieve
     * @return the study, if it exists in the tracking system, or null
     */
    GociStudy retrieveStudyByPubMedID(String pubmedID);

    /**
     * Creates a new {@link GociStudy} by searching pubmed for the study with the given pubmed ID and, if found,
     * creating it.
     *
     * @param pubmedID the pubmed ID of the study to create
     * @return the newly created study, if found in pubmed, or null if not
     */
    GociStudy createStudyFromPubMed(String pubmedID);
}
