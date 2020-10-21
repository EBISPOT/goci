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
                    if (study.getAccessionId().equals(studyDto.getAccession())) {
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

    /*
    Study initStudy(DepositionStudyDto studyDto, Publication publication, SecureUser currentUser) {
        Curator levelTwoCurator = curatorRepository.findByLastName("Level 2 Curator");
        CurationStatus levelOneCurationComplete = statusRepository.findByStatus("Level 1 curation done");

        getLog().info("[IMPORT] Initializing study: {} | {}", publication.getPubmedId(), studyDto.getAccession());
        Study study = studyDto.buildStudy();
        DiseaseTrait diseaseTrait = diseaseTraitRepository.findByTraitIgnoreCase(studyDto.getTrait());
        study.setDiseaseTrait(diseaseTrait);

        String manufacturerString = studyDto.getArrayManufacturer();
        if (manufacturerString != null) {
            List<Platform> platformList = new ArrayList<>();
            String[] manufacturers = manufacturerString.split("\\||,");
            getLog().info("[IMPORT] Manufacturers provided: {}", manufacturers.length);
            for (String manufacturer : manufacturers) {
                Platform platform = platformRepository.findByManufacturer(manufacturer.trim());
                platformList.add(platform);
            }
            getLog().info("[IMPORT] Manufacturers mapped: {}", platformList.size());
            study.setPlatforms(platformList);
        }
        List<GenotypingTechnology> gtList = new ArrayList<>();
        String genotypingTech = studyDto.getGenotypingTechnology();
        if (genotypingTech != null) {
            String[] technologies = genotypingTech.split("\\||,");
            getLog().info("[IMPORT] Genotyping technology provided: {}", technologies.length);
            for (String technology : technologies) {
                GenotypingTechnology gtt = genotypingTechnologyRepository.findByGenotypingTechnology(
                        DepositionTransform.transformGenotypingTechnology(technology.trim()));
                gtList.add(gtt);
            }
        }
        getLog().info("[IMPORT] Genotyping technology mapped: {}", gtList.size());
        study.setGenotypingTechnologies(gtList);

        study.setPublicationId(publication);
        getLog().info("[IMPORT] Creating house keeping ...");
        Housekeeping housekeeping = housekeepingRepository.createHousekeeping();
        getLog().info("[IMPORT] House keeping created: {}", housekeeping.getId());
        study.setHousekeeping(housekeeping);
        housekeeping.setCurator(levelTwoCurator);
        housekeeping.setCurationStatus(levelOneCurationComplete);
        if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("") && !studyDto.getSummaryStatisticsFile().equals("NR")) {
            study.setFullPvalueSet(true);
            getLog().info("[IMPORT] Full p-value set to TRUE.");
        }
        Integer variantCount = studyDto.getVariantCount();
        if (variantCount != -1) {
            study.setSnpCount(variantCount);
        }
        List<EfoTrait> efoTraitList = new ArrayList<>();
        String efoTrait = studyDto.getEfoTrait();
        if (efoTrait != null) {
            String[] efoTraits = efoTrait.split("\\||,");
            getLog().info("[IMPORT] EFO traits provided: {}", efoTraits.length);
            for (String trait : efoTraits) {
                EfoTrait dbTrait = efoTraitRepository.findByShortForm(trait.trim());
                efoTraitList.add(dbTrait);
            }
        }
        List<EfoTrait> mappedTraitList = new ArrayList<>();
        getLog().info("[IMPORT] EFO traits mapped: {}", efoTraitList.size());
        study.setEfoTraits(efoTraitList);
        String mappedBackgroundTrait = studyDto.getBackgroundEfoTrait();
        if (mappedBackgroundTrait != null) {
            String[] efoTraits = mappedBackgroundTrait.split("\\||,");
            getLog().info("[IMPORT] Background EFO traits provided: {}", efoTraits.length);
            for (String trait : efoTraits) {
                EfoTrait dbTrait = efoTraitRepository.findByShortForm(trait);
                mappedTraitList.add(dbTrait);
            }
        }
        getLog().info("[IMPORT] Background EFO traits mapped: {}", mappedTraitList.size());
        study.setMappedBackgroundTraits(mappedTraitList);
        DiseaseTrait backgroundTrait = diseaseTraitRepository.findByTraitIgnoreCase(studyDto.getBackgroundTrait());
        study.setBackgroundTrait(backgroundTrait);

        if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("") && !studyDto.getSummaryStatisticsFile().equals("NR")) {
            study.setFullPvalueSet(true);
        }
        study.setStudyDesignComment(studyDto.getArrayInformation());
        getLog().info("[IMPORT] Saving study ...");
        try {
            studyService.save(study);
            getLog().info("[IMPORT] Study saved: {}", study.getId());
        } catch (Exception e) {
            getLog().error("[IMPORT] Could not save study: {}", e.getMessage(), e);
        }
        StudyExtension studyExtension = new StudyExtension();
        studyExtension.setStudyDescription(studyDto.getStudyDescription());
        studyExtension.setCohort(studyDto.getCohort());
        studyExtension.setCohortSpecificReference(studyDto.getCohortId());
        studyExtension.setStatisticalModel(studyDto.getStatisticalModel());
        studyExtension.setSummaryStatisticsFile(studyDto.getSummaryStatisticsFile());
        studyExtension.setSummaryStatisticsAssembly(studyDto.getSummaryStatisticsAssembly());
        studyExtension.setStudy(study);
        getLog().info("[IMPORT] Saving study extension...");
        try {
            studyExtensionRepository.save(studyExtension);
            getLog().info("[IMPORT] Study extension saved: {}", studyExtension.getId());
            study.setStudyExtension(studyExtension);
            studyService.save(study);
        } catch (Exception e) {
            getLog().error("[IMPORT] Could not save study extension: {}", e.getMessage(), e);
        }
        return study;
    }
    */

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
