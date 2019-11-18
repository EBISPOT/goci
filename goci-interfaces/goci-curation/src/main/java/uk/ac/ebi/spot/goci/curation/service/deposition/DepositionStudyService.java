package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.service.HousekeepingOperationsService;
import uk.ac.ebi.spot.goci.curation.service.StudyNoteOperationsService;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionNoteDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.StudyService;

import java.sql.Date;
import java.util.*;

@Component
public class DepositionStudyService {
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
    @Autowired NoteSubjectRepository noteSubjectRepository;
    @Autowired NoteRepository noteRepository;



    public void publishSummaryStats(Study study, SecureUser currentUser) {
        UnpublishReason tempReason = unpublishReasonRepository.findById(1L);
        CurationStatus currentStatus = study.getHousekeeping().getCurationStatus();
        study.setFullPvalueSet(true);
        studyService.save(study);
        studyOperationsService.unpublishStudy(study.getId(), tempReason, currentUser);
        studyOperationsService
                .assignStudyStatus(study, new StatusAssignment(currentStatus.getId(), currentStatus.getStatus()),
                        currentUser);
    }

        public void publishSummaryStats(Collection<Study> dbStudies, SecureUser currentUser) {
        for (Study study : dbStudies) {
            publishSummaryStats(study, currentUser);
        }
    }

    public void unPublishStudy(Study study) {

    }

    public void createStudy(DepositionStudyDto studyDto) {

    }

    Study initStudy(DepositionStudyDto studyDto, Publication publication, SecureUser currentUser) {
        Curator levelTwoCurator = curatorRepository.findByLastName("Level 2 Curator");
        CurationStatus levelOneCurationComplete = statusRepository.findByStatus("Level 1 curation done");

        Study study = studyDto.buildStudy();
        DiseaseTrait diseaseTrait = diseaseTraitRepository.findByTraitIgnoreCase(studyDto.getTrait());
        study.setDiseaseTrait(diseaseTrait);

        String manufacturerString = studyDto.getArrayManufacturer();
        if (manufacturerString != null) {
            List<Platform> platformList = new ArrayList<>();
            String[] manufacturers = manufacturerString.split("\\|");
            for (String manufacturer : manufacturers) {
                Platform platform = platformRepository.findByManufacturer(manufacturer);
                platformList.add(platform);
            }
            study.setPlatforms(platformList);
        }
        List<GenotypingTechnology> gtList = new ArrayList<>();
        GenotypingTechnology gtt =
                genotypingTechnologyRepository.findByGenotypingTechnology(studyDto.getGenotypingTechnology());
        gtList.add(gtt);
        study.setGenotypingTechnologies(gtList);

        study.setPublicationId(publication);
        Housekeeping housekeeping = housekeepingRepository.createHousekeeping();
        study.setHousekeeping(housekeeping);
        housekeeping.setCurator(levelTwoCurator);
        housekeeping.setCurationStatus(levelOneCurationComplete);
        if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("")) {
            study.setFullPvalueSet(true);
        }
        study.setImputed(studyDto.getImputation());
        study.setSnpCount(studyDto.getVariantCount());
        return study;
    }

    public void deleteStudies(Collection<Study> dbStudies, Curator curator, SecureUser currentUser) {
        for (Study study : dbStudies) {
            addStudyNote(study, "DELETED, replaced by deposition import", null, curator, null, currentUser);
//          studyService.deleteByStudyId(study.getId());
        }
    }

    public void addStudyNote(Study study, String noteText, String noteStatus,
                             Curator noteCurator, String noteSubject, SecureUser currentUser){
        Collection<StudyNote> studyNotes = study.getNotes();
        if (studyNotes != null && studyNotes.size() != 0) {
            for (StudyNote note : studyNotes) {
                note.setTextNote(noteText);
            }
        } else {
            StudyNote note = noteOperationsService.createEmptyStudyNote(study, currentUser);
            note.setTextNote(noteText);
            if(noteStatus != null) {
                note.setStatus(Boolean.parseBoolean(noteStatus));
            }
            if(noteCurator != null) {
                note.setCurator(noteCurator);
            }
            if(noteSubject != null) {
                note.setNoteSubject(noteSubjectRepository.findBySubjectIgnoreCase(noteSubject));
            }
            note.setStudy(study);
            noteRepository.save(note);
            study.addNote(note);
        }
        //study.setStudyDesignComment("DELETED " + study.getStudyDesignComment());
        studyService.save(study);
    }

    public void addStudyNote(Study study, DepositionNoteDto noteDto, SecureUser currentUser, Curator curator){
        addStudyNote(study, noteDto.getNote(), noteDto.getStatus(), curator, noteDto.getNoteSubject(), currentUser);

    }
}
