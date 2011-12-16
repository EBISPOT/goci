package uk.ac.ebi.fgpt.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import uk.ac.ebi.fgpt.goci.exception.InsufficientPrivilegesException;
import uk.ac.ebi.fgpt.goci.model.DatabaseRecoveredGociStudy;
import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.model.GociUser;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A basic implementation of a curation service.  This implementation retrieves studies from a GociStudyService and
 * simply updates fields as they are modified, without taking any further action (such as writing changes to a
 * database)
 *
 * @author Tony Burdett
 * @date 26/10/11
 */
public class DefaultGociCurationService implements GociCurationService {
    private GociStudyService studyService;
    private MailSender mailSender;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public GociStudyService getStudyService() {
        return studyService;
    }

    public void setStudyService(GociStudyService studyService) {
        this.studyService = studyService;
    }

    public MailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void updateState(String studyID, GociStudy.State updatedState, GociUser user)
            throws InsufficientPrivilegesException {
        if (user.getPermissions().compareTo(GociUser.Permissions.CURATOR) > -1) {
            GociStudy study = getStudyService().retrieveStudy(studyID);
            if (study instanceof DatabaseRecoveredGociStudy) {
                ((DatabaseRecoveredGociStudy) study).setState(updatedState);
            }
            else {
                getLog().warn("Cannot update study, unknown type '" + study.getClass().getSimpleName() + "'");
            }
        }
        else {
            throw new InsufficientPrivilegesException(user, "Not enough privileges to update state");
        }
    }

    public void updateEligibility(String studyID, GociStudy.Eligibility updatedEligibility, GociUser user)
            throws InsufficientPrivilegesException {
        if (user.getPermissions().compareTo(GociUser.Permissions.CURATOR) > -1) {
            GociStudy study = getStudyService().retrieveStudy(studyID);
            if (study instanceof DatabaseRecoveredGociStudy) {
                ((DatabaseRecoveredGociStudy) study).setGwasEligibility(updatedEligibility);
            }
            else {
                getLog().warn("Cannot update study, unknown type '" + study.getClass().getSimpleName() + "'");
            }
        }
        else {
            throw new InsufficientPrivilegesException(user, "Not enough privileges to update eligibility");
        }
    }

    public void assignOwner(String studyID, GociUser updatedOwner, GociUser user)
            throws InsufficientPrivilegesException {
        if (user.getPermissions().compareTo(GociUser.Permissions.CURATOR) > -1) {
            GociStudy study = getStudyService().retrieveStudy(studyID);
            if (study instanceof DatabaseRecoveredGociStudy) {
                // set the new owner
                GociUser oldOwner = study.getOwner();
                ((DatabaseRecoveredGociStudy) study).setOwner(updatedOwner);
                GociUser newOwner = study.getOwner();

                // only notify by email if the user did not assign this paper to themself
                if (newOwner != null && !newOwner.equals(user)) {
                    // notify new owner by email, cc old owner
                    try {
                        StringBuilder msgText = new StringBuilder();
                        String hostAddress = InetAddress.getLocalHost().getCanonicalHostName();
                        String userName = user.getFirstName() + " " + user.getSurname();
                        String priorOwnerString = oldOwner == null
                                                  ? "unowned"
                                                  : "owned by " + oldOwner.getFirstName() + " " + oldOwner.getSurname();
                        msgText.append("Hi ").append(newOwner.getFirstName()).append(",").append("\n").append("\n");
                        msgText.append("Study \"").append(study.getTitle()).append("\" (PubMed ID ")
                                .append(study.getPubMedID()).append(") ").append("has been assigned to you by ")
                                .append(userName).append(".  This publication was previously ")
                                .append(priorOwnerString).append(".\n\n");
                        msgText.append("You can view the status of this study in the tracking system ")
                                .append("by visiting http://").append(hostAddress)
                                .append(":8080/goci, or head to http://www.ncbi.nlm.nih.gov/pubmed/")
                                .append(study.getPubMedID()).append(" to view the PubMed entry for this paper.");

                        SimpleMailMessage msg = new SimpleMailMessage();
                        msg.setFrom("goci-tracker@ebi.ac.uk");
                        msg.setSubject(
                                "[goci] You have been assigned paper '" + study.getPubMedID() +
                                        "' in the GOCI Tracking System");
                        msg.setTo(newOwner.getEmail());
                        if (oldOwner != null) {
                            msg.setCc(oldOwner.getEmail());
                        }
                        msg.setText(msgText.toString());

                        getMailSender().send(msg);
                    }
                    catch (UnknownHostException e) {
                        getLog().error("Failed to send email: host domain name could not be found");
                    }
                }
                else {
                    getLog().debug("User assigned paper " + study.getPubMedID() + " to themself, no need to email");
                }
            }
            else {
                getLog().warn("Cannot update study, unknown type '" + study.getClass().getSimpleName() + "'");
            }
        }
        else {
            throw new InsufficientPrivilegesException(user, "Not enough privileges to update owner");
        }
    }
}
