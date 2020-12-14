package uk.ac.ebi.spot.goci.curation.service.deposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.model.deposition.SubmissionImportStudy;
import uk.ac.ebi.spot.goci.repository.SubmissionImportStudyRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

@Service
public class DepositionStudiesImportService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private SubmissionImportStudyRepository submissionImportStudyRepository;

    @Autowired
    private DepositionSubmissionService depositionSubmissionService;

    private ObjectMapper objectMapper;

    @PostConstruct
    public void initialize() {
        objectMapper = new ObjectMapper();
    }

    public List<String> retrieveStudies(String submissionId) {
        getLog().info("[{}] Retrieving studies from the Deposition App ...", submissionId);

        int page = 0;
        int count = 0;
        boolean done = false;
        List<String> errors = new ArrayList<>();

        while (!done) {
            List<DepositionStudyDto> depositionStudyDtos = new ArrayList<>();
            try {
                depositionStudyDtos = depositionSubmissionService.getStudiesForSubmission(submissionId, page);
            } catch (Exception e) {
                errors.add(e.getMessage());
                getLog().error("ERROR: {}", e.getMessage(), e);
                done = true;
            }

            for (DepositionStudyDto depositionStudyDto : depositionStudyDtos) {
                String error = materializeStudy(depositionStudyDto, submissionId);
                if (error != null) {
                    errors.add(error);
                    done = true;
                    break;
                }
                count++;
            }

            if (depositionStudyDtos.isEmpty()) {
                done = true;
            }
            page++;
        }

        getLog().info("[{}] Retrieved {} studies from the Deposition App ...", submissionId, count);
        if (count == 0) {
            getLog().error("No studies found for submission: {}", submissionId);
            errors.add("No studies found for submission: " + submissionId);
        }
        return errors;
    }

    public SubmissionImportStudy enrich(SubmissionImportStudy submissionImportStudy) {
        try {
            DepositionStudyDto depositionStudyDto = objectMapper.readValue(submissionImportStudy.getContent(), DepositionStudyDto.class);
            submissionImportStudy.setDepositionStudyDto(depositionStudyDto);
            return submissionImportStudy;

        } catch (IOException e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return submissionImportStudy;
        }
    }

    public Stream<SubmissionImportStudy> streamBySubmissionId(String submissionId) {
        return submissionImportStudyRepository.readBySubmissionId(submissionId);
    }

    @Transactional
    String materializeStudy(DepositionStudyDto depositionStudyDto, String submissionId) {
        String content;
        try {
            content = objectMapper.writeValueAsString(depositionStudyDto);
        } catch (JsonProcessingException e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return e.getMessage();
        }

        SubmissionImportStudy submissionImportStudy = new SubmissionImportStudy();
        submissionImportStudy.setAccessionId(depositionStudyDto.getAccession());
        submissionImportStudy.setContent(content);
        submissionImportStudy.setSubmissionId(submissionId);
        submissionImportStudy.setTimestamp(Calendar.getInstance().getTime());
        submissionImportStudy.setSuccess(false);
        submissionImportStudy.setFinalized(false);
        submissionImportStudyRepository.save(submissionImportStudy);
        return null;
    }

    @Transactional
    public void deleteStudies(List<Long> markedForDeletion) {
        submissionImportStudyRepository.deleteByIdIn(markedForDeletion);
    }

    @Transactional
    public void save(SubmissionImportStudy submissionImportStudy) {
        submissionImportStudyRepository.save(submissionImportStudy);
    }

    public long countUnsuccessful(String submissionId) {
        return submissionImportStudyRepository.countBySubmissionIdAndSuccessAndFinalized(submissionId, false, true);
    }

    @Transactional
    public void deleteStudiesForSubmissionId(String submissionID) {
        submissionImportStudyRepository.deleteBySubmissionId(submissionID);
    }
}
