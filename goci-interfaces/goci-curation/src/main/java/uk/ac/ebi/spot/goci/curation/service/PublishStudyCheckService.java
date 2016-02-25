package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;

/**
 * Created by emma on 24/02/2016.
 *
 * @author emma
 *         <p>
 *         Checks a study is suitable for publishing
 */
@Service
public class PublishStudyCheckService {

    private CheckEfoTermAssignment checkEfoTermAssignment;

    @Autowired
    public PublishStudyCheckService(CheckEfoTermAssignment checkEfoTermAssignment) {
        this.checkEfoTermAssignment = checkEfoTermAssignment;
    }

    /**
     * Run study checks
     *
     * @param study        study to run checks one
     * @param associations all associations found for a study
     */
    public String runChecks(Study study, Collection<Association> associations) {
        String message;

        // Check EFO term assigned to study
        Boolean efoTermsAssigned = checkEfoTermAssignment.checkStudyEfoAssignment(study);

        // Check all associations approved
        int snpNotApproved = studyAssociationCheck(associations);

        if (snpNotApproved == 1 && !efoTermsAssigned) {
            message = "No EFO trait assigned and some SNP associations have not been approved for study: "
                    + study.getAuthor() + ", "
                    + " pubmed = " + study.getPubmedId()
                    + ", please review before changing the status.";
        }
        else if (snpNotApproved == 1) {
            message = "Some SNP associations have not been approved for study: "
                    + study.getAuthor() + ", "
                    + " pubmed = " + study.getPubmedId()
                    + ", please review before changing the status.";
        }
        else if (!efoTermsAssigned) {
            message = "No EFO trait assigned to study: "
                    + study.getAuthor() + ", "
                    + " pubmed = " + study.getPubmedId()
                    + ", please review before changing the status.";
        }
        else {
            message = null;
        }

        return message;
    }

    /**
     * Check SNPs have been approved
     *
     * @param associations All associations found for a study
     */
    private int studyAssociationCheck(Collection<Association> associations) {
        int snpsNotApproved = 0;
        for (Association association : associations) {
            // If we have one that is not checked set value
            if (!association.getSnpApproved()) {
                snpsNotApproved = 1;
            }
        }

        return snpsNotApproved;
    }

}
