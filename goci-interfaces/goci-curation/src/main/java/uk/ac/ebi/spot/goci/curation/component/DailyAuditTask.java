package uk.ac.ebi.spot.goci.curation.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.model.StudyAuditView;
import uk.ac.ebi.spot.goci.curation.service.MailService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyReport;
import uk.ac.ebi.spot.goci.repository.AssociationReportRepository;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.StudyReportRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by emma on 13/03/15.
 *
 * @author emma
 *         <p>
 *         Daily scheduled task to record studies sent to NCBI and check for errors returned from NCBI pipeline
 */
@Component
public class DailyAuditTask {

    private StudyRepository studyRepository;
    private CurationStatusRepository curationStatusRepository;
    private StudyReportRepository studyReportRepository;
    private AssociationRepository associationRepository;
    private AssociationReportRepository associationReportRepository;
    private MailService mailService;

    // Variables set by first scheduled task, store these here so we can access them when sending email
    private Integer totalNumberOfStudiesSentToNcbi;
    private Collection<StudyAuditView> studiesSentToNcbiForEmail = new ArrayList<StudyAuditView>();

    @Autowired
    public DailyAuditTask(StudyRepository studyRepository,
                          CurationStatusRepository curationStatusRepository,
                          StudyReportRepository studyReportRepository,
                          AssociationRepository associationRepository,
                          AssociationReportRepository associationReportRepository,
                          MailService mailService) {
        this.studyRepository = studyRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.studyReportRepository = studyReportRepository;
        this.associationRepository = associationRepository;
        this.associationReportRepository = associationReportRepository;
        this.mailService = mailService;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    // Calculate studies with status 'Send to NCBI' before pipeline runs at 00:15
    @Scheduled(cron = "0 0 0 * * *")
    public void calculateNumberOfStudiesSentToNcbi() {
        CurationStatus status = curationStatusRepository.findByStatus("Send to NCBI");
        Long statusId = status.getId();
        Collection<Study> studiesSentToNcbi = studyRepository.findByCurationStatusIgnoreCase(statusId);

        // Set total
        this.totalNumberOfStudiesSentToNcbi = studiesSentToNcbi.size();

        // Create list of all studies sent to NCBI that will be used to create email text
        Collection<StudyAuditView> studiesSentToNcbiForEmail = new ArrayList<StudyAuditView>();

        if (!studiesSentToNcbi.isEmpty()) {
            for (Study studySentToNcbi : studiesSentToNcbi) {
                StudyAuditView studyAuditView = createView(studySentToNcbi);
                studiesSentToNcbiForEmail.add(studyAuditView);
            }

            // Pass details to email method
            getLog().info("List of studies sent to NCBI calculated, as part of daily audit task");
        }

        this.studiesSentToNcbiForEmail = studiesSentToNcbiForEmail;
    }

    // Scheduled for 7am everyday
    @Scheduled(cron = "0 0 7 * * *")
    public void dailyErrorAudit() {

        // Get list of all studies with status "NCBI pipeline error"
        CurationStatus ncbiErrorStatus = curationStatusRepository.findByStatus("NCBI pipeline error");
        CurationStatus importErrorStatus = curationStatusRepository.findByStatus("Data import error");

        Long ncbiErrorStatusId = ncbiErrorStatus.getId();
        Long importErrorStatusId = importErrorStatus.getId();

        Collection<Study> studiesWithNcbiErrors = studyRepository.findByCurationStatusIgnoreCase(ncbiErrorStatusId);
        Collection<Study> studiesWithImportErrors = studyRepository.findByCurationStatusIgnoreCase(importErrorStatusId);

        // Calculate some totals that we can add as a summary view to email
        Integer totalStudiesWithNcbiErrors = studiesWithNcbiErrors.size();
        Integer totalStudiesWithImportErrors = studiesWithImportErrors.size();

        Collection<StudyAuditView> studiesWithNcbiErrorsForEmail = new ArrayList<StudyAuditView>();
        // Send email for all studies with NCBI pipeline errors
        if (!studiesWithNcbiErrors.isEmpty()) {

            // For each study retrieve its study report and association report details
            for (Study studyWithNcbiError : studiesWithNcbiErrors) {
                StudyAuditView studyAuditView = createView(studyWithNcbiError);
                studiesWithNcbiErrorsForEmail.add(studyAuditView);
            }

            // Pass details to email method
            getLog().info("List of studies with errors calculated, as part of daily audit task");
        }

        // Send mail
        mailService.sendDailyAuditEmail(studiesWithNcbiErrorsForEmail,
                                        totalStudiesWithNcbiErrors,
                                        totalStudiesWithImportErrors,
                                        this.totalNumberOfStudiesSentToNcbi,
                                        this.studiesSentToNcbiForEmail);
    }

    // Create view objects that will be parsed to create email
    private StudyAuditView createView(Study study) {

        // Initialise and collect all information required for email
        String title = study.getTitle();
        Long studyId = study.getId();
        String pubmedId = study.getPubmedId();
        String author = study.getAuthor();
        Date studyDate = study.getPublicationDate();

        // possibility that these may not be set in DB
        Date sendToNCBIDate = null;
        Long pubmedIdError = null;
        List<String> snpErrors = new ArrayList<String>();
        List<String> geneNotOnGenomeErrors = new ArrayList<String>();
        List<String> snpGeneOnDiffChrErrors = new ArrayList<String>();
        List<String> noGeneForSymbolErrors = new ArrayList<String>();

        StudyReport studyReport = studyReportRepository.findByStudyId(study.getId());

        if (studyReport != null) {
            pubmedIdError = studyReport.getPubmedIdError();
        }

        if (study.getHousekeeping().getSendToNCBIDate() != null) {
            sendToNCBIDate = study.getHousekeeping().getSendToNCBIDate();
        }

        // Get all the associations linked to this study
        Collection<Association> studyAssociations =
                associationRepository.findByStudyId(study.getId().longValue());

        // Get all association reports and collate errors
        if (studyAssociations != null) {
            for (Association association : studyAssociations) {
                AssociationReport associationReport =
                        associationReportRepository.findByAssociationId(association.getId());

                // If we have a report try and store the error messages for subsequent email
                if (associationReport != null) {
                    if (associationReport.getSnpError() != null && !associationReport.getSnpError().isEmpty()) {
                        snpErrors.add(associationReport.getSnpError());
                    }

                    if (associationReport.getGeneNotOnGenome() != null &&
                            !associationReport.getGeneNotOnGenome().isEmpty()) {
                        geneNotOnGenomeErrors.add(associationReport.getGeneNotOnGenome());
                    }

                    if (associationReport.getSnpGeneOnDiffChr() != null &&
                            !associationReport.getSnpGeneOnDiffChr().isEmpty()) {
                        snpGeneOnDiffChrErrors.add(associationReport.getSnpGeneOnDiffChr());
                    }

                    if (associationReport.getNoGeneForSymbol() != null &&
                            !associationReport.getNoGeneForSymbol().isEmpty()) {
                        noGeneForSymbolErrors.add(associationReport.getNoGeneForSymbol());
                    }
                }
            }
        }


        // Create a view of all errors for each study
        StudyAuditView studyAuditView = new StudyAuditView(title,
                                                           pubmedId,
                                                           studyId,
                                                           author,
                                                           sendToNCBIDate,
                                                           studyDate,
                                                           pubmedIdError,
                                                           snpErrors,
                                                           geneNotOnGenomeErrors,
                                                           snpGeneOnDiffChrErrors,
                                                           noGeneForSymbolErrors);

        return studyAuditView;
    }
}
