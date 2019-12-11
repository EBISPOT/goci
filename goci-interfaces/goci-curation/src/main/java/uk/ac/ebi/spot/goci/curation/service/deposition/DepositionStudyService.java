package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.service.HousekeepingOperationsService;
import uk.ac.ebi.spot.goci.curation.service.StudyNoteOperationsService;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionNoteDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.StudyService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    @Autowired
    NoteSubjectRepository noteSubjectRepository;
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    EfoTraitRepository efoTraitRepository;
    @Autowired
    StudyExtensionRepository studyExtensionRepository;


    public void publishSummaryStats(Study study, SecureUser currentUser) {
        UnpublishReason tempReason = unpublishReasonRepository.findById(1L);
        CurationStatus currentStatus = study.getHousekeeping().getCurationStatus();
        //studyOperationsService.unpublishStudy(study.getId(), tempReason, currentUser);
        study.setFullPvalueSet(true);
        studyService.save(study);
//        studyOperationsService
//                .assignStudyStatus(study, new StatusAssignment(currentStatus.getId(), currentStatus.getStatus()),
//                        currentUser);
    }

    public void publishSummaryStats(Collection<DepositionStudyDto> studyDtos, Collection<Study> dbStudies,
                                    SecureUser currentUser) {
        for(DepositionStudyDto studyDto: studyDtos) {
            String tag = studyDto.getStudyTag();
            boolean match = false;
            for (Study study : dbStudies) {
                if(study.getAccessionId().equals(studyDto.getAccession())){
                    publishSummaryStats(study, currentUser);
                    match = true;
                }
            }
            if(!match){
                List<Long> studyIds = new ArrayList<>();
                for (Study study : dbStudies) {
                    studyIds.add(study.getId());
                }
                for (Long studyId: studyIds) {
                    publishSummaryStats(studyService.findOne(studyId), currentUser);
                }
            }
        }
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
            Integer variantCount = studyDto.getVariantCount();
            if(variantCount != -1) {
                study.setSnpCount(variantCount);
            }
        List<EfoTrait> efoTraitList = new ArrayList<>();
        String efoTrait = studyDto.getEfoTrait();
        if(efoTrait != null){
            String[] efoTraits = efoTrait.split("\\|");
            for(String trait: efoTraits){
                EfoTrait dbTrait = efoTraitRepository.findByShortForm(studyDto.getEfoTrait());
                efoTraitList.add(dbTrait);
            }
        }
        if(studyDto.getSummaryStatisticsFile() != null){
            study.setFullPvalueSet(true);
        }
        study.setEfoTraits(efoTraitList);
        studyService.save(study);
        StudyExtension studyExtension = new StudyExtension();
        studyExtension.setBackgroundTrait(studyDto.getBackgroundTrait());
        studyExtension.setCohort(studyDto.getCohort());
        studyExtension.setCohortSpecificReference(studyDto.getCohortId());
        studyExtension.setStatisticalModel(studyDto.getStatisticalModel());
        studyExtension.setSummaryStatisticsFile(studyDto.getSummaryStatisticsFile());
        studyExtension.setSummaryStatisticsAssembly(studyDto.getSummaryStatisticsAssembly());
        studyExtension.setStudy(study);
        studyExtensionRepository.save(studyExtension);
        study.setStudyExtension(studyExtension);
        studyService.save(study);
        return study;
    }

    public void deleteStudies(Collection<Study> dbStudies, Curator curator, SecureUser currentUser) {
        if(dbStudies != null) {
            for (int i = 0; i < dbStudies.size(); i++) {
                addStudyNote(dbStudies.toArray(new Study[0])[i], null,
                        "Review for deletion, replaced by deposition import", null, curator, null, currentUser);
                //          studyService.deleteByStudyId(study.getId());
            }
        }
    }

    public void addStudyNote(Study study, String studyTag, String noteText, String noteStatus, Curator noteCurator,
                             String noteSubject,
                             SecureUser currentUser) {
        Collection<StudyNote> studyNotes = study.getNotes();
        if (studyNotes != null && studyNotes.size() != 0) {
            int length = studyNotes.size();
            StudyNote[] notes = studyNotes.toArray(new StudyNote[0]);
            for (int i = 0; i < length; i++) {
                if(studyTag != null) {
                    notes[i].setTextNote(studyTag + "\n" + noteText);
                }else{
                    notes[i].setTextNote(noteText);
                }
            }
        } else {
            StudyNote note = noteOperationsService.createEmptyStudyNote(study, currentUser);
            if(studyTag != null) {
                note.setTextNote(studyTag + "\n" + noteText);
            }else{
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
                if(subject == null){
                    subject = noteSubjectRepository.findBySubjectIgnoreCase("System note");
                }
                note.setNoteSubject(subject);
            }
            note.setStudy(study);
            noteRepository.save(note);
            study.addNote(note);
        }
        //study.setStudyDesignComment("DELETED " + study.getStudyDesignComment());
        studyService.save(study);
    }

    public void addStudyNote(Study study, DepositionStudyDto studyDto, DepositionNoteDto noteDto,
                             SecureUser currentUser,
                             Curator curator) {
        addStudyNote(study, studyDto.getStudyTag(), noteDto.getNote(), noteDto.getStatus(), curator,
                noteDto.getNoteSubject(),
                currentUser);

    }
}
