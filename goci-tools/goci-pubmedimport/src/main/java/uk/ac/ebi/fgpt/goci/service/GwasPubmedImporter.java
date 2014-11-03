package uk.ac.ebi.fgpt.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.GwasStudyDAO;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.lang.ImporterProperties;
import uk.ac.ebi.fgpt.goci.model.GwasStudy;

import java.util.*;

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

    public String dispatchSearch(String input) throws DispatcherException{
        String log = "";
        getLog().debug("Checking input against database...");
        Collection<String> pubmedIDs = processInput(input);

        // filter list of pubmed ids, only include those that aren't already entered
        List<String> newPubMedIDs = new ArrayList<String>();
        for (String pubmedID : pubmedIDs) {
            if (!getStudyDAO().studyExists(pubmedID)) {
                newPubMedIDs.add(pubmedID);
            }
            else{
                String message = "Pubmed ID " + pubmedID + " already exists in the database";
                log = log.concat(message).concat("\n");
                getLog().trace(message);
            }
        }

        // fetch titles and abstracts for the new pubmed ids
        String msg = pubmedIDs.size() + " Pubmed IDs were provided.  " +
                "Of these, " + newPubMedIDs.size() + " are new.";
        getLog().info(msg);
        log = log.concat(msg).concat("\n");


        if(newPubMedIDs.size() > 0){
            Map<String, GwasStudy> studiesMap = getDispatcherService().dispatchSummaryQuery(newPubMedIDs);
            for (String pubmedID : newPubMedIDs) {
                getLog().debug("Study ID '" + pubmedID + "' is new, will be entered into tracking system");
                GwasStudy study = studiesMap.get(pubmedID);

                if(study != null) {
                    getStudyDAO().saveStudy(study);
                    String message = "Added study '" + study.getPubMedID() + "' (\"" + study.getTitle() + "\") " +
                            "into the table " + ImporterProperties.getOutputTable();
                    log = log.concat(message).concat("\n");
                    getLog().info(message);
                }
                else{
                     String message = "PubmedID " + pubmedID + " is not a valid PubmedID and no study can be added";
                     log = log.concat(message).concat("\n");
                    getLog().info(message);
                }
            }

        }
        else {
            String message = "No new studies to be added.";
            log = log.concat(message).concat("\n");
            getLog().info(message);
        }
        return log;

    }

    public Collection<String> processInput(String input){
        Collection<String> pmids = new ArrayList<String>();

        if(input.contains(",")){
            StringTokenizer tk = new StringTokenizer(input,",");
            while (tk.hasMoreTokens()){
                pmids.add(tk.nextToken());
            }

        }
        else {
            pmids.add(input);
        }

        return pmids;
    }
}
