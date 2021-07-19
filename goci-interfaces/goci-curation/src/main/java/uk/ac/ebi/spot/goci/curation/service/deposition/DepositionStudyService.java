package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.curation.service.HousekeepingOperationsService;
import uk.ac.ebi.spot.goci.curation.service.StudyNoteOperationsService;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.EventOperationsService;
import uk.ac.ebi.spot.goci.service.StudyService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class DepositionStudyService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    DiseaseTraitRepository diseaseTraitRepository;
    @Autowired
    PlatformRepository platformRepository;
    @Autowired
    GenotypingTechnologyRepository genotypingTechnologyRepository;
    @Autowired
    HousekeepingOperationsService housekeepingRepository;
    @Autowired
    CuratorRepository curatorRepository;
    @Autowired
    CurationStatusRepository statusRepository;
    @Autowired
    UnpublishReasonRepository unpublishReasonRepository;
    @Autowired
    StudyService studyService;
    @Autowired
    StudyOperationsService studyOperationsService;
    @Autowired
    StudyNoteOperationsService noteOperationsService;
    @Autowired
    NoteSubjectRepository noteSubjectRepository;
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    EfoTraitRepository efoTraitRepository;
    @Autowired
    StudyExtensionRepository studyExtensionRepository;
    @Autowired
    EventOperationsService eventOperationsService;


    public void publishSummaryStats(Study study, String studyTag) {
        study.setFullPvalueSet(true);
        if (studyTag != null) {
            study.setStudyTag(studyTag);
        }
        studyService.save(study);
    }

    public Pair<Boolean, List<String>> publishSummaryStats(Collection<DepositionStudyDto> studyDtos, Collection<Study> dbStudies) {
        getLog().info("Publishing summary stats: {} | {}", studyDtos.size(), dbStudies.size());
        List<String> errors = new ArrayList<>();
        boolean outcome = true;
        List<Long> studyIds = new ArrayList<>();
        for (Study study : dbStudies) {
            studyIds.add(study.getId());
        }
        try {
            for (DepositionStudyDto studyDto : studyDtos) {
                String tag = studyDto.getStudyTag();
                boolean match = false;
                for (Long studyId : studyIds) {
                    Study study = studyService.findOne(studyId);
                    if (study.getAccessionId() != null && study.getAccessionId().equals(studyDto.getAccession())) {
                        publishSummaryStats(study, tag);
                        match = true;
                    }
                }
                if (!match) {
                    for (Long studyId : studyIds) {
                        getLog().warn(" - Study [{}] has no study tag.", studyId);
                        errors.add("Warning: Study [" + studyId + "] has no study tag.");
                        publishSummaryStats(studyService.findOne(studyId), null);
                    }
                }
            }
            getLog().info("Publishing summary stats done.");
        } catch (Exception e) {
            getLog().error("Encountered error: {}", e.getMessage(), e);
            errors.add("Error: " + e.getMessage());
            outcome = false;
        }

        return Pair.of(outcome, errors);
    }

    @Transactional
    public String deleteStudies(Collection<Study> dbStudies, Curator curator, SecureUser currentUser) {
        try {
            if (dbStudies != null) {
                for (int i = 0; i < dbStudies.size(); i++) {
                    addStudyNote(dbStudies.toArray(new Study[0])[i], null,
                            "Review for deletion, replaced by deposition import", null, curator, null, currentUser);
                    //          studyService.deleteByStudyId(study.getId());
                }
            }
            CurationStatus requiresReview = statusRepository.findByStatus("Requires Review");
            dbStudies.forEach(study -> {
                study.getHousekeeping().setCurationStatus(requiresReview);
                Event event = eventOperationsService.createEvent("REQUIRES_REVIEW", currentUser,
                        requiresReview.getStatus());
                study.getEvents().add(event);
            });
        } catch (Exception e) {
            getLog().error("Error encountered: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }

        return null;
    }

    public void addStudyNote(Study study, String studyTag, String noteText, String noteStatus, Curator noteCurator,
                             String noteSubject,
                             SecureUser currentUser) {
        StudyNote note = noteOperationsService.createEmptyStudyNote(study, currentUser);
        if (studyTag != null) {
            note.setTextNote(studyTag + "\n" + noteText);
        } else {
            note.setTextNote(noteText);
        }
        if (noteStatus != null) {
            note.setStatus(Boolean.parseBoolean(noteStatus));
        }
        if (noteCurator != null) {
            note.setCurator(noteCurator);
        }
        if (noteSubject != null) {
            NoteSubject subject = noteSubjectRepository.findBySubjectIgnoreCase(noteSubject);
            if (subject == null) {
                subject = noteSubjectRepository.findBySubjectIgnoreCase("System note");
            }
            note.setNoteSubject(subject);
        }
        note.setStudy(study);
        noteRepository.save(note);
        study.addNote(note);
        studyService.save(study);
    }

}
