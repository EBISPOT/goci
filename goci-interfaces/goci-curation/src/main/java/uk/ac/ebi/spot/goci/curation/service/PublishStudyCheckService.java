package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.GenotypingTechnology;
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

    private CheckEfoTermAssignmentService checkEfoTermAssignmentService;

    @Autowired
    public PublishStudyCheckService(CheckEfoTermAssignmentService checkEfoTermAssignment) {
        this.checkEfoTermAssignmentService = checkEfoTermAssignment;
    }

    /**
     * Run study checks
     *
     * @param study        study to run checks one
     * @param associations all associations found for a study
     */
    public String runChecks(Study study, Collection<Association> associations) {
        String message = null;

        // Check EFO term assigned to study
        Boolean efoTermsAssigned = checkEfoTermAssignmentService.checkStudyEfoAssignment(study);

        // Check all associations approved
        int snpNotApproved = studyAssociationCheck(associations);

        boolean missingCoR = countryOfRecruitmentCheck(study);

        Collection<GenotypingTechnology> genotypingTechnologies = study.getGenotypingTechnologies();

        boolean targetedArrayStudy = false;
        boolean missingGenotypingTechnology = false;

        if(genotypingTechnologies.isEmpty()){
            missingGenotypingTechnology = true;
        }

        for(GenotypingTechnology gt : genotypingTechnologies){
            if(gt.getGenotypingTechnology().contains("Targeted") || gt.getGenotypingTechnology().contains("Exome") || gt.getGenotypingTechnology().contains("sequencing")){
                targetedArrayStudy = true;
            }
        }


        if (targetedArrayStudy) {
            message = "Study: "
                    + study.getPublicationId().getFirstAuthor().getFullnameShort(30) + ", "
                    + " pubmed = " + study.getPublicationId().getPubmedId()
                    + ", is a targeted array, other non-genome-wide or sequencing study and should not be published.";
        }

        else if(snpNotApproved == 1 || !efoTermsAssigned || missingCoR || missingGenotypingTechnology){
            message = "Study: "
                    + study.getPublicationId().getFirstAuthor().getFullnameShort(30) + ", "
                    + " pubmed = " + study.getPublicationId().getPubmedId()
                    + " requires review before publication because ";

            if(snpNotApproved == 1){
                message = message.concat("some SNP associations have not been approved; ");
            }
            if(!efoTermsAssigned){
                message = message.concat("no EFO trait assigned; ");
            }
            if(missingCoR){
                message = message.concat("at least one ancestry description with no country of recruitment; ");

            }
            if(missingGenotypingTechnology){
                message = message.concat("no genotyping technology provided; ");
            }
        }

        return message;
    }

    /**
     * Check all ancestry entries have a country of recruitment recorded
     *
     * @param study
     */
    private boolean countryOfRecruitmentCheck(Study study) {

        boolean missingcor = false;
        for(Ancestry ancestry : study.getAncestries()){
            if(ancestry.getCountryOfRecruitment() == null || ancestry.getCountryOfRecruitment().isEmpty()){
               missingcor = true;
               break;
            }
        }
        return missingcor;
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
