package uk.ac.ebi.spot.goci.curation.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.model.StudyErrorView;
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
 *         Daily scheduled task to check for errors returned from NCBI pipeline
 */
@Component
public class DailyAuditTask {

    private StudyRepository studyRepository;
    private CurationStatusRepository curationStatusRepository;
    private StudyReportRepository studyReportRepository;
    private AssociationRepository associationRepository;
    private AssociationReportRepository associationReportRepository;
    private MailService mailService;

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


    // Scheduled for 7am everyday
    @Scheduled(cron = "0 0 7 * * *")
    public void dailyErrorAudit() {

        // Get list of all studies with status "NCBI pipeline error"
        CurationStatus status = curationStatusRepository.findByStatus("NCBI pipeline error");
        Long statusId = status.getId();
        Collection<Study> studiesWithErrors = studyRepository.findByCurationStatusIgnoreCaseAndCheckedNcbiError(statusId, false);

        // Send email for all studies with errors
        if (!studiesWithErrors.isEmpty()) {
            Collection<StudyErrorView> studyErrorViews = new ArrayList<StudyErrorView>();

            // For each study retrieve its study report and association report details
            for (Study studyWithError : studiesWithErrors) {

                // Collect all information required for email
                StudyReport studyReport = studyReportRepository.findByStudyId(studyWithError.getId());
                String title = studyWithError.getTitle();
                Long studyId = studyWithError.getId();
                String pubmedId = studyWithError.getPubmedId();
                Long pubmedIdError = studyReport.getPubmedIdError();
                Date sendToNCBIDate = studyWithError.getHousekeeping().getSendToNCBIDate();
                List<String> snpErrors = new ArrayList<String>();
                List<String> geneNotOnGenomeErrors = new ArrayList<String>();
                List<String> snpGeneOnDiffChrErrors = new ArrayList<String>();
                List<String> noGeneForSymbolErrors = new ArrayList<String>();

                // Get all the associations linked to this study
                Collection<Association> studyAssociations =
                        associationRepository.findByStudyId(studyWithError.getId().longValue());

                // Get all association reports and collate errors
                for (Association association : studyAssociations) {
                    AssociationReport associationReport =
                            associationReportRepository.findByAssociationId(association.getId());

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

                // Create a view of all errors for each study
                StudyErrorView studyErrorView = new StudyErrorView(title,
                                                                   pubmedId,
                                                                   studyId,
                                                                   pubmedIdError,
                                                                   sendToNCBIDate,
                                                                   snpErrors,
                                                                   geneNotOnGenomeErrors,
                                                                   snpGeneOnDiffChrErrors,
                                                                   noGeneForSymbolErrors);

                studyErrorViews.add(studyErrorView);
            }

            // Pass details to email method
            mailService.sendDailyAuditEmail(studyErrorViews);

        }
    }
}
