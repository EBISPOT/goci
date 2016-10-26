package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Platform;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 25/05/2016.
 *
 * @author emma
 *         <p>
 *         Service class to duplicate a study
 */
@Service
public class StudyDuplicationService {

    private EthnicityRepository ethnicityRepository;
    private HousekeepingOperationsService housekeepingOperationsService;
    private TrackingOperationService trackingOperationService;
    private StudyRepository studyRepository;

    @Autowired
    public StudyDuplicationService(EthnicityRepository ethnicityRepository,
                                   HousekeepingOperationsService housekeepingOperationsService,
                                   @Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                                   StudyRepository studyRepository) {
        this.ethnicityRepository = ethnicityRepository;
        this.housekeepingOperationsService = housekeepingOperationsService;
        this.trackingOperationService = trackingOperationService;
        this.studyRepository = studyRepository;
    }

    /**
     * Create a study entry in the database
     *
     * @param user             User preforming request
     * @param studyToDuplicate Study to duplicate
     * @return ID of newly created duplicate study
     */
    public Study duplicateStudy(Study studyToDuplicate, SecureUser user) {

        // Record duplication event
        trackingOperationService.update(studyToDuplicate, user, EventType.STUDY_DUPLICATION);
        studyRepository.save(studyToDuplicate);

        // New study will be created by copying existing study details
        Study duplicateStudy = copyStudy(studyToDuplicate);

        // Add study creation event
        trackingOperationService.create(duplicateStudy, user);

        // Create housekeeping object and add duplicate message
        Housekeeping duplicateStudyHousekeeping = housekeepingOperationsService.createHousekeeping();
        duplicateStudyHousekeeping.setNotes(
                "Duplicate of study: " + studyToDuplicate.getAuthor() + ", PMID: " + studyToDuplicate.getPubmedId());
        duplicateStudy.setHousekeeping(duplicateStudyHousekeeping);

        studyRepository.save(duplicateStudy);

        // Copy existing ethnicity
        Collection<Ethnicity> studyToDuplicateEthnicities = ethnicityRepository.findByStudyId(studyToDuplicate.getId());
        Collection<Ethnicity> newEthnicities = new ArrayList<>();

        studyToDuplicateEthnicities.forEach(studyToDuplicateEthnicity -> {
            Ethnicity duplicateEthnicity = copyEthnicity(studyToDuplicateEthnicity);
            duplicateEthnicity.setStudy(duplicateStudy);
            newEthnicities.add(duplicateEthnicity);
            ethnicityRepository.save(duplicateEthnicity);
        });

        duplicateStudy.setEthnicities(newEthnicities);
        studyRepository.save(duplicateStudy);

        // Save newly duplicated study and housekeeping
        housekeepingOperationsService.saveHousekeeping(duplicateStudy, duplicateStudyHousekeeping);

        return duplicateStudy;
    }

    /**
     * Create a study entry in the database
     *
     * @param studyToDuplicate Study to duplicate
     * @return study
     */
    private Study copyStudy(Study studyToDuplicate) {

        Study duplicateStudy = new Study();
        duplicateStudy.setAuthor(studyToDuplicate.getAuthor() + " DUP");
        duplicateStudy.setPublicationDate(studyToDuplicate.getPublicationDate());
        duplicateStudy.setPublication(studyToDuplicate.getPublication());
        duplicateStudy.setTitle(studyToDuplicate.getTitle());
        duplicateStudy.setInitialSampleSize(studyToDuplicate.getInitialSampleSize());
        duplicateStudy.setReplicateSampleSize(studyToDuplicate.getReplicateSampleSize());
        duplicateStudy.setPubmedId(studyToDuplicate.getPubmedId());
        duplicateStudy.setCnv(studyToDuplicate.getCnv());
        duplicateStudy.setGxe(studyToDuplicate.getGxe());
        duplicateStudy.setGxg(studyToDuplicate.getGxg());
        duplicateStudy.setGenomewideArray(studyToDuplicate.getGenomewideArray());
        duplicateStudy.setTargetedArray(studyToDuplicate.getTargetedArray());
        duplicateStudy.setDiseaseTrait(studyToDuplicate.getDiseaseTrait());
        duplicateStudy.setSnpCount(studyToDuplicate.getSnpCount());
        duplicateStudy.setQualifier(studyToDuplicate.getQualifier());
        duplicateStudy.setImputed(studyToDuplicate.getImputed());
        duplicateStudy.setPooled(studyToDuplicate.getPooled());
        duplicateStudy.setFullPvalueSet(studyToDuplicate.getFullPvalueSet()); 
        duplicateStudy.setStudyDesignComment(studyToDuplicate.getStudyDesignComment());

        // Deal with EFO traits
        Collection<EfoTrait> efoTraits = studyToDuplicate.getEfoTraits();
        Collection<EfoTrait> efoTraitsDuplicateStudy = new ArrayList<EfoTrait>();

        if (efoTraits != null && !efoTraits.isEmpty()) {
            efoTraitsDuplicateStudy.addAll(efoTraits);
            duplicateStudy.setEfoTraits(efoTraitsDuplicateStudy);
        }

        //Deal with platforms
        Collection<Platform> platforms = studyToDuplicate.getPlatforms();
        Collection<Platform> platformsDuplicateStudy = new ArrayList<>();

        if (platforms != null && !platforms.isEmpty()) {
            platformsDuplicateStudy.addAll(platforms);
            duplicateStudy.setPlatforms(platformsDuplicateStudy);
        }
        return duplicateStudy;
    }

    /**
     * Create a study entry in the database
     *
     * @param studyToDuplicateEthnicity Ethnicity to duplicate
     * @return ethnicity
     */
    private Ethnicity copyEthnicity(Ethnicity studyToDuplicateEthnicity) {
        Ethnicity duplicateEthnicity = new Ethnicity();
        duplicateEthnicity.setType(studyToDuplicateEthnicity.getType());
        duplicateEthnicity.setNumberOfIndividuals(studyToDuplicateEthnicity.getNumberOfIndividuals());
        duplicateEthnicity.setEthnicGroup(studyToDuplicateEthnicity.getEthnicGroup());
        duplicateEthnicity.setCountryOfOrigin(studyToDuplicateEthnicity.getCountryOfOrigin());
        duplicateEthnicity.setCountryOfRecruitment(studyToDuplicateEthnicity.getCountryOfRecruitment());
        duplicateEthnicity.setDescription(studyToDuplicateEthnicity.getDescription());
        duplicateEthnicity.setPreviouslyReported(studyToDuplicateEthnicity.getPreviouslyReported());
        duplicateEthnicity.setSampleSizesMatch(studyToDuplicateEthnicity.getSampleSizesMatch());
        duplicateEthnicity.setNotes(studyToDuplicateEthnicity.getNotes());
        return duplicateEthnicity;
    }
}