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
import uk.ac.ebi.spot.goci.service.EventOperationsService;
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
    @Autowired
    EventOperationsService eventOperationsService;


    public void publishSummaryStats(Study study, SecureUser currentUser, String studyTag) {
        UnpublishReason tempReason = unpublishReasonRepository.findById(1L);
        CurationStatus currentStatus = study.getHousekeeping().getCurationStatus();
        //studyOperationsService.unpublishStudy(study.getId(), tempReason, currentUser);
        study.setFullPvalueSet(true);
        if(studyTag != null){
            study.setStudyTag(studyTag);
        }
        studyService.save(study);
//        studyOperationsService
//                .assignStudyStatus(study, new StatusAssignment(currentStatus.getId(), currentStatus.getStatus()),
//                        currentUser);
    }

    public void publishSummaryStats(Collection<DepositionStudyDto> studyDtos, Collection<Study> dbStudies,
                                    SecureUser currentUser) {
        List<Long> studyIds = new ArrayList<>();
        for (Study study : dbStudies) {
            studyIds.add(study.getId());
        }
        for(DepositionStudyDto studyDto: studyDtos) {
            String tag = studyDto.getStudyTag();
            boolean match = false;
            for (Long studyId: studyIds) {
                Study study = studyService.findOne(studyId);
                if(study.getAccessionId().equals(studyDto.getAccession())){
                    publishSummaryStats(study, currentUser, tag);
                    match = true;
                }
            }
            if(!match){
                for (Long studyId: studyIds) {
                    publishSummaryStats(studyService.findOne(studyId), currentUser, null);
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
                Platform platform = platformRepository.findByManufacturer(manufacturer.trim());
                platformList.add(platform);
            }
            study.setPlatforms(platformList);
        }
        List<GenotypingTechnology> gtList = new ArrayList<>();
        String genotypingTech = studyDto.getGenotypingTechnology();
        if(genotypingTech != null) {
            String[] technologies = genotypingTech.split("\\|");
            for (String technology : technologies) {
                GenotypingTechnology gtt = genotypingTechnologyRepository.findByGenotypingTechnology(technology.trim());
                gtList.add(gtt);
            }
        }
        study.setGenotypingTechnologies(gtList);

        study.setPublicationId(publication);
        Housekeeping housekeeping = housekeepingRepository.createHousekeeping();
        study.setHousekeeping(housekeeping);
        housekeeping.setCurator(levelTwoCurator);
        housekeeping.setCurationStatus(levelOneCurationComplete);
        if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("")) {
            study.setFullPvalueSet(true);
        }
        Integer variantCount = studyDto.getVariantCount();
        if(variantCount != -1) {
            study.setSnpCount(variantCount);
        }
        List<EfoTrait> efoTraitList = new ArrayList<>();
        String efoTrait = studyDto.getEfoTrait();
        if(efoTrait != null){
            String[] efoTraits = efoTrait.split("\\|");
            for(String trait: efoTraits){
                EfoTrait dbTrait = efoTraitRepository.findByShortForm(trait.trim());
                efoTraitList.add(dbTrait);
            }
        }
        List<EfoTrait> mappedTraitList = new ArrayList<>();
        study.setEfoTraits(efoTraitList);
        String mappedBackgroundTrait = studyDto.getBackgroundEfoTrait();
        if(mappedBackgroundTrait != null) {
            String[] efoTraits = mappedBackgroundTrait.split("\\|");
            for (String trait : efoTraits) {
                EfoTrait dbTrait = efoTraitRepository.findByShortForm(trait);
                mappedTraitList.add(dbTrait);
            }
        }
        study.setMappedBackgroundTraits(mappedTraitList);
        DiseaseTrait backgroundTrait = diseaseTraitRepository.findByTraitIgnoreCase(studyDto.getBackgroundTrait());
        study.setBackgroundTrait(backgroundTrait);

        if(studyDto.getSummaryStatisticsFile() != null){
            study.setFullPvalueSet(true);
        }
        study.setStudyDesignComment(studyDto.getArrayInformation());
        studyService.save(study);
        StudyExtension studyExtension = new StudyExtension();
        studyExtension.setStudyDescription(studyDto.getStudyDescription());
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
        CurationStatus requiresReview = statusRepository.findByStatus("Requires Review");
        dbStudies.forEach(study -> {
            study.getHousekeeping().setCurationStatus(requiresReview);
            Event event = eventOperationsService.createEvent("REQUIRES_REVIEW", currentUser,
                    requiresReview.getStatus());
            study.getEvents().add(event);
        });
    }

    public void addStudyNote(Study study, String studyTag, String noteText, String noteStatus, Curator noteCurator,
                             String noteSubject,
                             SecureUser currentUser) {
        Collection<StudyNote> studyNotes = study.getNotes();
//        if (studyNotes != null && studyNotes.size() != 0) {
//            int length = studyNotes.size();
//            StudyNote[] notes = studyNotes.toArray(new StudyNote[0]);
//            for (int i = 0; i < length; i++) {
//                if(studyTag != null) {
//                    notes[i].setTextNote(studyTag + "\n" + noteText);
//                }else{
//                    notes[i].setTextNote(noteText);
//                }
//            }
//        } else {
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
//        }
        //study.setStudyDesignComment("DELETED " + study.getStudyDesignComment());
        studyService.save(study);
    }

//    public void addStudyNote(Study study, DepositionStudyDto studyDto, DepositionNoteDto noteDto,
//                             SecureUser currentUser,
//                             Curator curator) {
//        addStudyNote(study, studyDto.getStudyTag(), noteDto.getNote(), noteDto.getStatus(), curator,
//                noteDto.getNoteSubject(),
//                currentUser);
//
//    }
}
