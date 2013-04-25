package uk.ac.ebi.fgpt.goci;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.GwasStudyDAO;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.model.GwasStudy;
import uk.ac.ebi.fgpt.goci.service.GwasPubMedDispatcherService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class PubmedSearchDriver
{

    public PubmedSearchDriver(){

        dispatchSearch();
    }


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


    public void dispatchSearch(){
        try {
            getLog().debug("Dispatching PubMed queries...");
            Collection<String> pubmedIDs = getDispatcherService().dispatchSearchQuery();
            getLog().debug("Query returned " + pubmedIDs.size() + " studies...");

            // filter list of pubmed ids, only include those that aren't already entered
            List<String> newPubMedIDs = new ArrayList<String>();
            for (String pubmedID : pubmedIDs) {
                if (!getStudyDAO().getStudyByPubMedID(pubmedID)) {
                    newPubMedIDs.add(pubmedID);
                }
            }

            // fetch titles and abstracts for the new pubmed ids
            getLog().info("PubMed search ran, and identified " + pubmedIDs.size() + " publications.  " +
                    "Of these, " + newPubMedIDs.size() + " are new.");
            Map<String, GwasStudy> studiesMap = getDispatcherService().dispatchSummaryQuery(newPubMedIDs);
            for (String pubmedID : newPubMedIDs) {
                getLog().debug("Study ID '" + pubmedID + "' is new, will be entered into tracking system");
                GwasStudy study = studiesMap.get(pubmedID);
                getStudyDAO().saveStudy(study);
                getLog().info("Added study '" + study.getPubMedID() + "' (\"" + study.getTitle() + "\") " +
                        "into the tracking system.");
            }
        }
        catch (DispatcherException e) {
          //  throw new JobExecutionException(e);
        }
    }



}
