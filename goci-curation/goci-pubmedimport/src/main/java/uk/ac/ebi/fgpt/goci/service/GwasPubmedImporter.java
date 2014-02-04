package uk.ac.ebi.fgpt.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.GwasStudyDAO;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.model.GwasStudy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 01/05/13
 * Time: 16:06
 * To change this template use File | Settings | File Templates.
 */
public class GwasPubmedImporter {

    private GwasPubMedDispatcherService dispatcherService;
    private GwasStudyDAO studyDAO;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }


    public GwasPubMedDispatcherService getDispatcherService() {
        return dispatcherService;
    }

    public void setDispatcherService(GwasPubMedDispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }

    public GwasStudyDAO getStudyDAO() {
        return studyDAO;
    }

    public void setStudyDAO(GwasStudyDAO studyDAO) {
        this.studyDAO = studyDAO;
    }

    public void dispatchSearch() throws DispatcherException{
        getLog().debug("Dispatching PubMed queries...");
        Collection<String> pubmedIDs = getDispatcherService().dispatchSearchQuery();
        getLog().debug("Query returned " + pubmedIDs.size() + " studies...");

        // filter list of pubmed ids, only include those that aren't already entered
        List<String> newPubMedIDs = new ArrayList<String>();
        for (String pubmedID : pubmedIDs) {
            if (!getStudyDAO().studyExists(pubmedID)) {
                newPubMedIDs.add(pubmedID);
            }
            else{
                getLog().trace("Pubmed ID " + pubmedID + " already exists in the database");
            }
        }

        // fetch titles and abstracts for the new pubmed ids
        getLog().info("PubMed search ran, and identified " + pubmedIDs.size() + " publications.  " +
                "Of these, " + newPubMedIDs.size() + " are new.");

        if(newPubMedIDs.size() > 0){
            Map<String, GwasStudy> studiesMap = getDispatcherService().dispatchSummaryQuery(newPubMedIDs);
            for (String pubmedID : newPubMedIDs) {
                getLog().debug("Study ID '" + pubmedID + "' is new, will be entered into tracking system");
                GwasStudy study = studiesMap.get(pubmedID);
                getStudyDAO().saveStudy(study);
                getLog().info("Added study '" + study.getPubMedID() + "' (\"" + study.getTitle() + "\") " +
                        "into the tracking system.");
            }
        }
        else {
            getLog().info("No new studies to be added.");
        }

    }
}
