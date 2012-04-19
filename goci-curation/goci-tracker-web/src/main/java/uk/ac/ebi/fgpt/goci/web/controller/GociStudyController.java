package uk.ac.ebi.fgpt.goci.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.model.GociUser;
import uk.ac.ebi.fgpt.goci.service.GociCurationService;
import uk.ac.ebi.fgpt.goci.service.GociStudyService;
import uk.ac.ebi.fgpt.goci.service.GociUserService;
import uk.ac.ebi.fgpt.goci.web.view.CurationRequestBean;
import uk.ac.ebi.fgpt.goci.web.view.CurationResponseBean;

import java.util.Arrays;
import java.util.Collection;

/**
 * A POJO controller class designed to function in the spring web-mvc framework.  This class contains some logic to
 * unwrap particular simple requests, retrieving {@link uk.ac.ebi.fgpt.goci.model.GociStudy} objects as appropriate.
 * Otherwise, it functions basically as a means to delegate calls to underlying service implementations.
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
@Controller
@RequestMapping("/studies")
public class GociStudyController {
    private GociStudyService studyService;
    private GociCurationService curationService;
    private GociUserService userService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public GociStudyService getStudyService() {
        return studyService;
    }

    @Autowired
    public void setStudyService(GociStudyService studyService) {
        this.studyService = studyService;
    }

    public GociCurationService getCurationService() {
        return curationService;
    }

    @Autowired
    public void setCurationService(GociCurationService curationService) {
        this.curationService = curationService;
    }

    public GociUserService getUserService() {
        return userService;
    }

    @Autowired
    public void setUserService(GociUserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/{studyID}", method = RequestMethod.GET)
    public @ResponseBody GociStudy retrieveStudy(@PathVariable String studyID) {
        getLog().debug("Retrieving study " + studyID);
        return getStudyService().retrieveStudy(studyID);
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody CurationResponseBean createStudy(@RequestParam String pubmedID) {
        try {
            GociStudy study = getStudyService().createStudyFromPubMed(pubmedID);
            if (study != null) {
                return new CurationResponseBean(true,
                                                "A new study was created from PubMed record '" + pubmedID + "'",
                                                study.getID());
            }
            else {
                return new CurationResponseBean(false, "No record found with PubMed ID '" + pubmedID + "'", "");
            }
        }
        catch (Exception e) {
            getLog().error("Failed to create a new Study (PubMed ID '" + pubmedID + "'): " + e.getMessage(), e);
            return new CurationResponseBean(false,
                                            "Failed to create study with PubMed ID '" + pubmedID + "' (" +
                                                    e.getMessage() + ")",
                                            "");
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Collection<GociStudy> searchStudies(
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "owner", required = false) String username,
            @RequestParam(value = "processable", required = false) boolean processable) {
        if (state == null && username == null) {
        	if(!processable){
        		return retrieveAllStudies();
        	}
        	else{
        		return retrieveProcessableStudies();
        	}
        }
        else {
            if (state != null && username == null) {
                return retrieveStudiesByState(state);
            }
            else if (state == null) {
                return retrieveStudiesByUser(username);
            }
            else {
                throw new UnsupportedOperationException("Compound state + owner queries are not yet supported");
            }
        }
    }

    @RequestMapping(value = "/{studyID}", method = RequestMethod.POST)
    public @ResponseBody CurationResponseBean curate(@PathVariable String studyID,
                                                     @RequestBody CurationRequestBean curationRequest) {
        try {
            getLog().debug("Curation request received for study " + studyID + ":\n" + curationRequest.toString());

            // get the user, identified by their rest api key
            GociUser user = getUserService().getUserByRestApiKey(curationRequest.getRestApiKey());

            // check if state is not null
            String statusMessage = "Curation operation complete! The following fields were updated: ";
            if (!curationRequest.getUpdatedState().equals("")) {
                // if not, update state
                getLog().debug("Updating state to " + curationRequest.getUpdatedState());
                GociStudy.State state = GociStudy.State.valueOf(curationRequest.getUpdatedState());
                getCurationService().updateState(studyID, state, user);
                statusMessage += "state = " + state.toString() + "; ";
            }

            // check if owner is not null
            if (!curationRequest.getUpdatedOwner().equals("")) {
                // if not, update owner
                getLog().debug("Updating owner " + curationRequest.getUpdatedOwner());
                GociUser owner = getUserService().getUserByUserName(curationRequest.getUpdatedOwner());
                getCurationService().assignOwner(studyID, owner, user);
                statusMessage += "owner = " + curationRequest.getUpdatedOwner() + "; ";
            }

            // check if eligibility flag is not null
            if (!curationRequest.getUpdatedEligibility().equals("")) {
                // if not, update state
                getLog().debug("Updated eligibility to " + curationRequest.getUpdatedEligibility());
                GociStudy.Eligibility eligibility =
                        GociStudy.Eligibility.valueOf(curationRequest.getUpdatedEligibility());
                getCurationService().updateEligibility(studyID, eligibility, user);
                statusMessage += "eligibility = " + curationRequest.getUpdatedEligibility() + "; ";
            }

            // if we got to here, update was successful so generate response
            return new CurationResponseBean(true, statusMessage, studyID);
        }
        catch (Exception e) {
            getLog().error("Unable to curate this study", e);
            return new CurationResponseBean(false,
                                            "Study '" + studyID + "' could not be updated: " + e.getMessage(),
                                            studyID);
        }
    }

    @RequestMapping(value = "/states", method = RequestMethod.GET)
    public @ResponseBody Collection<GociStudy.State> getPossibleStudyStates() {
        return Arrays.asList(GociStudy.State.values());
    }

    @RequestMapping(value = "/eligibilities", method = RequestMethod.GET)
    public @ResponseBody Collection<GociStudy.Eligibility> getPossibleStudyEligibilities() {
        return Arrays.asList(GociStudy.Eligibility.values());
    }

    public Collection<GociStudy> retrieveAllStudies() {
        getLog().debug("Retrieving all studies");
        return getStudyService().retrieveAllStudies();
    }
    
    public Collection<GociStudy> retrieveProcessableStudies() {
        getLog().debug("Retrieving studies the require further processing");
        return getStudyService().retrieveProcessableStudies();
    }

    public Collection<GociStudy> retrieveStudiesByState(String state) {
        GociStudy.State studyState = GociStudy.State.valueOf(state);
        getLog().debug("Retrieving studies with state = " + studyState);
        return getStudyService().retrieveStudiesByState(studyState);
    }

    public Collection<GociStudy> retrieveStudiesByUser(String username) {
        GociUser user = getUserService().getUserByUserName(username);
        getLog().debug("Retrieving studies with owner " + username);
        return getStudyService().retrieveStudiesByUser(user);
    }
}
