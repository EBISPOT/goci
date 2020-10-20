package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.deposition.SubmissionImportProgress;
import uk.ac.ebi.spot.goci.repository.SubmissionImportProgressRepository;

import java.util.Calendar;
import java.util.Optional;

@Service
public class SubmissionImportProgressService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private SubmissionImportProgressRepository submissionImportProgressRepository;

    public boolean importInProgress(String submissionId) {
        Optional<SubmissionImportProgress> submissionImportProgressOptional = submissionImportProgressRepository.findBySubmissionId(submissionId);
        return submissionImportProgressOptional.isPresent();
    }

    public SubmissionImportProgress createNewImport(String userEmail, String submissionId) {
        log.info("Creating new import for [{}]: {}", submissionId, userEmail);
        SubmissionImportProgress submissionImportProgress = new SubmissionImportProgress(null, Calendar.getInstance().getTime(), submissionId, userEmail);
        submissionImportProgress = submissionImportProgressRepository.save(submissionImportProgress);
        log.info("Import progress created: {}", submissionImportProgress.getId());
        return submissionImportProgress;
    }

    public void deleteImport(Long id) {
        log.info("Removing id: {}", id);
        submissionImportProgressRepository.delete(id);
    }
}
