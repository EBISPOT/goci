package uk.ac.ebi.fgpt.goci.service.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.factory.GociStudyFactory;
import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.service.GociPubMedDispatcherService;
import uk.ac.ebi.fgpt.goci.service.GociTrackerService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * Date 27/10/11
 */
public class PubMedSearchJob implements Job {
    private GociPubMedDispatcherService dispatcherService;
    private GociTrackerService trackerService;
    private GociStudyFactory studyFactory;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public GociPubMedDispatcherService getDispatcherService() {
        return dispatcherService;
    }

    public void setDispatcherService(GociPubMedDispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }

    public GociTrackerService getTrackerService() {
        return trackerService;
    }

    public void setTrackerService(GociTrackerService trackerService) {
        this.trackerService = trackerService;
    }

    public GociStudyFactory getStudyFactory() {
        return studyFactory;
    }

    public void setStudyFactory(GociStudyFactory studyFactory) {
        this.studyFactory = studyFactory;
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            getLog().debug("Dispatching PubMed queries...");
            Collection<String> pubmedIDs = getDispatcherService().dispatchSearchQuery();
            getLog().debug("Query returned " + pubmedIDs.size() + " studies...");

            // filter list of pubmed ids, only include those that aren't already entered
            List<String> newPubMedIDs = new ArrayList<String>();
            for (String pubmedID : pubmedIDs) {
                if (!getTrackerService().isStudyEntered(pubmedID)) {
                    newPubMedIDs.add(pubmedID);
                }
            }

            // fetch titles and abstracts for the new pubmed ids
            getLog().info("PubMed search ran, and identified " + pubmedIDs.size() + " publications.  " +
                                  "Of these, " + newPubMedIDs.size() + " are new.");
            Map<String, String> titlesMap = getDispatcherService().dispatchSummaryQuery(newPubMedIDs);
            Map<String, String> abstractsMap = getDispatcherService().dispatchFetchQuery(newPubMedIDs);
            for (String pubmedID : newPubMedIDs) {
                if (!getTrackerService().isStudyEntered(pubmedID)) {
                    getLog().debug("Study ID '" + pubmedID + "' is new, will be entered into tracking system");
                    GociStudy study = getStudyFactory().createStudy(pubmedID,
                                                                    titlesMap.get(pubmedID),
                                                                    abstractsMap.get(pubmedID));
                    getTrackerService().enterStudy(study);
                    getLog().info("Added study '" + study.getPubMedID() + "' (\"" + study.getTitle() + "\") " +
                                          "into the tracking system.");
                }
                else {
                    getLog().warn("Filtering pubmed IDs failed to identify study with pubmed ID '" + pubmedID + "', " +
                                          "which is already entered into the tracking system - the search string is " +
                                          "detecting duplicates that aren't filtered.");
                }
            }
        }
        catch (DispatcherException e) {
            throw new JobExecutionException(e);
        }
    }
}
