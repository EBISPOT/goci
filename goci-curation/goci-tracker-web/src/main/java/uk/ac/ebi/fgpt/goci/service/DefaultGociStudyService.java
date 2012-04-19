package uk.ac.ebi.fgpt.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.GociStudyDAO;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.model.GociUser;

import java.util.Collection;

/**
 * A default implementation of a study service that can be supplied with a {@link uk.ac.ebi.fgpt.goci.dao.GociStudyDAO}
 * in order to retrieve studies from an underlying datasource.
 *
 * @author Tony Burdett
 * Date 27/10/11
 */
public class DefaultGociStudyService implements GociStudyService {
    private GociStudyDAO studyDAO;
    private GociPubMedDispatcherService dispatcherService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public GociStudyDAO getStudyDAO() {
        return studyDAO;
    }

    public void setStudyDAO(GociStudyDAO studyDAO) {
        this.studyDAO = studyDAO;
    }

    public GociPubMedDispatcherService getDispatcherService() {
        return dispatcherService;
    }

    public void setDispatcherService(GociPubMedDispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }

    public GociStudy retrieveStudy(String studyID) {
        return getStudyDAO().getStudy(studyID);
    }

    public Collection<GociStudy> retrieveAllStudies() {
        return getStudyDAO().getAllStudies();
    }
    
    public Collection<GociStudy> retrieveProcessableStudies(){
    	return getStudyDAO().getProcessableStudies();
    }

    public Collection<GociStudy> retrieveStudiesByState(GociStudy.State studyState) {
        return getStudyDAO().getStudiesByState(studyState);
    }

    public Collection<GociStudy> retrieveStudiesByUser(GociUser user) {
        return getStudyDAO().getStudiesByUser(user);
    }

    public GociStudy retrieveStudyByPubMedID(String pubmedID) {
        return getStudyDAO().getStudyByPubMedID(pubmedID);
    }

    public GociStudy createStudyFromPubMed(String pubmedID) {
        try {
            if (getStudyDAO().getStudyByPubMedID(pubmedID) == null) {
                getDispatcherService().addPubMedID(pubmedID);
                // now we have added the next pubmed id, redo the search -
                // if still not found, the dispatcher service may have postponed the search
                return getStudyDAO().getStudyByPubMedID(pubmedID);
            }
            else {
                // this study is already found, so throw an exception
                throw new IllegalArgumentException("Cannot create a study from PubMed ID '" + pubmedID + "': " +
                                                           "the study with this PubMed ID already exists");
            }
        }
        catch (DispatcherException e) {
            getLog().error("PubMed query could not be dispatched for '" + pubmedID + "'");
            return null;
        }
    }
}
