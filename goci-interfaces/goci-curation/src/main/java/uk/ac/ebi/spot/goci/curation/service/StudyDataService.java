package uk.ac.ebi.spot.goci.curation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.dto.StudyDto;
import uk.ac.ebi.spot.goci.curation.exception.ResourceNotFoundException;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.projection.StudySearchProjection;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyNoteRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudyDataService {

    private final StudyRepository studyRepository;
    private final DiseaseTraitRepository diseaseTraitRepository;
    private final EfoTraitRepository efoTraitRepository;
    private final StudyNoteRepository studyNoteRepository;

    public StudyDataService(StudyRepository studyRepository,
                            DiseaseTraitRepository diseaseTraitRepository,
                            EfoTraitRepository efoTraitRepository,
                            StudyNoteRepository studyNoteRepository) {
        this.studyRepository = studyRepository;
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.studyNoteRepository = studyNoteRepository;
    }

    public Page<Study> getStudiesByHousekeepingStatus(Pageable pageable, boolean isPublished) {
        return studyRepository.findByHousekeepingIsPublished(pageable, isPublished);
    }

    public Optional<Study> getStudyByAccessionId(String accessionId) {
        return studyRepository.findByAccessionId(accessionId);
    }

    public Study updateStudyDiseaseTraitByAccessionId(String trait, String accessionId) {
        Study study = this.getStudyByAccessionId(accessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", accessionId));

        DiseaseTrait diseaseTrait = Optional.ofNullable(diseaseTraitRepository.findByTraitIgnoreCase(trait))
                .orElseThrow(() -> new ResourceNotFoundException("Disease Trait", trait));

        study.setDiseaseTrait(diseaseTrait);
        studyRepository.save(study);
        log.info("Study with accession Id: {} found and updated", accessionId);
        return study;
    }

    public List<StudyDto> getAllStudies(String pubmedId, String author, String studyType,
                                        Long efoTraitId, Long diseaseTraitId,
                                        String notesQuery, Long status,
                                        Long curator, String accessionId, Long studyId, Pageable pageable) {
        Boolean gxe = null;
        Boolean gxg = null;
        Boolean cnv = null;
        String genotypeTech = "*";

        if (studyType.equals("GXE")) {
            gxe = true;
        }

        switch (studyType) {
            case "GXE":
                gxe = Boolean.TRUE;
                break;
            case "GXG":
                gxg = Boolean.TRUE;
                break;
            case "CNV":
                cnv = Boolean.TRUE;
                break;
            case "Genome-wide genotyping array studies":
                genotypeTech = "Genome-wide genotyping array";
                break;
            case "Targeted genotyping array studies":
                genotypeTech = "Targeted genotyping array";
                break;
            case "Exome genotyping array studies":
                genotypeTech = "Exome genotyping array";
                break;
            case "Exome-wide sequencing studies":
                genotypeTech = "Exome-wide sequencing";
                break;
            case "Genome-wide sequencing studies":
                genotypeTech = "Genome-wide sequencing";
                break;
            default:
                break;
        }

        List<StudySearchProjection> studyDataList =
                studyRepository.findByMultipleFilters(pubmedId, author, efoTraitId, diseaseTraitId,
                                                      notesQuery, status, curator, accessionId,
                                                      studyId, gxe, gxg, cnv, genotypeTech, pageable);
        List<Long> ids = new ArrayList<>();
        studyDataList.forEach(studyProjection -> ids.add(studyProjection.getStudyId()));

        List<StudySearchProjection> efoTraitsDataList = efoTraitRepository.findUsingStudyIds(ids);
        List<StudySearchProjection> notesDataList = studyNoteRepository.findUsingStudyIds(ids);

        List<StudyDto> studyDtos = new ArrayList<>();
        studyDataList.forEach(study -> {
            studyDtos.add(StudyDto.builder()
                                  .id(study.getStudyId())
                                  .accession(study.getAccessionId())
                                  .author(study.getAuthor())
                                  .title(study.getTitle())
                                  .publicationDate(study.getDate())
                                  .pubmedId(study.getPubmedId())
                                  .publication(study.getPublication())
                                  .curator(study.getCuratorLastName())
                                  .curationStatus(study.getCurationStatus())
                                  .diseaseTrait((Optional.ofNullable(study.getDiseaseTrait()).isPresent()) ? study.getDiseaseTrait() : "")

                                  .efoTrait(efoTraitsDataList.stream()
                                                    .filter(efoData -> efoData.getStudyId().equals(study.getStudyId()))
                                                    .collect(Collectors.toList()).stream()
                                                    .map(StudySearchProjection::getTrait)
                                                    .collect((Collectors.joining(", "))))

                                  .notes(notesDataList.stream().filter(noteData -> noteData.getStudyId().equals(study.getStudyId()))
                                                 .collect(Collectors.toList()).stream()
                                                 .map(StudySearchProjection::getTextNote)
                                                 .collect(Collectors.joining(" | ")))
                                  .build());
        });

        return studyDtos;
    }
}
