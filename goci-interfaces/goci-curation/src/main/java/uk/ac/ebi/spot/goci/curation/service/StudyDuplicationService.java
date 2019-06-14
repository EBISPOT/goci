package uk.ac.ebi.spot.goci.curation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.service.StudyNoteService;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.io.IOException;
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

    private AncestryRepository ancestryRepository;
    private HousekeepingOperationsService housekeepingOperationsService;
    private TrackingOperationService trackingOperationService;
    private StudyRepository studyRepository;
    private StudyNoteOperationsService studyNoteOperationsService;
    private StudyNoteService studyNoteService;

    @Autowired
    public StudyDuplicationService(AncestryRepository ancestryRepository,
                                   HousekeepingOperationsService housekeepingOperationsService,
                                   @Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                                   StudyRepository studyRepository,
                                   StudyNoteOperationsService studyNoteOperationsService,
                                   StudyNoteService studyNoteService) {
        this.ancestryRepository = ancestryRepository;
        this.housekeepingOperationsService = housekeepingOperationsService;
        this.trackingOperationService = trackingOperationService;
        this.studyRepository = studyRepository;
        this.studyNoteOperationsService = studyNoteOperationsService;
        this.studyNoteService = studyNoteService;
    }

    /**
     * Create a study entry in the database
     *
     * @param user             User preforming request
     * @param studyToDuplicate Study to duplicate
     * @return ID of newly created duplicate study
     */
    public Study duplicateStudy(Study studyToDuplicate, String tagDuplication, SecureUser user) {

        // Record duplication event
        trackingOperationService.update(studyToDuplicate, user, "STUDY_DUPLICATION");
        studyRepository.save(studyToDuplicate);

        // New study will be created by copying existing study details
        Study duplicateStudy = copyStudy(studyToDuplicate);

        // Add study creation event
        trackingOperationService.create(duplicateStudy, user);

        // Create housekeeping object and add duplicate message
        Housekeeping duplicateStudyHousekeeping = housekeepingOperationsService.createHousekeeping();
        duplicateStudyHousekeeping.setCurator(studyToDuplicate.getHousekeeping().getCurator());
        duplicateStudy.setHousekeeping(duplicateStudyHousekeeping);

        studyRepository.save(duplicateStudy);

        StudyNote note = studyNoteOperationsService.createAutomaticNote("Duplicate of study: "
                + studyToDuplicate.getPublicationId().getFirstAuthor().getFullnameShort(30) + ", PMID: " + studyToDuplicate.getPublicationId().getPubmedId(),duplicateStudy,user);

        // type of note to help the curator to tag a duplicated study
        StudyNote tagNote = studyNoteOperationsService.createTagDuplicateNote(tagDuplication,duplicateStudy,user);

        // The note is properly created. We don't need to check any business logic. Just link to the study.
        studyNoteService.saveStudyNote(note);
        studyNoteService.saveStudyNote(tagNote);

        duplicateStudy.addNote(note);
        duplicateStudy.addNote(tagNote);

        // Copy existing ancestry
        Collection<Ancestry> studyToDuplicateAncestries = ancestryRepository.findByStudyId(studyToDuplicate.getId());
        Collection<Ancestry> newAncestries = new ArrayList<>();

        studyToDuplicateAncestries.forEach(studyToDuplicateAncestry -> {
            Ancestry duplicateAncestry = copyAncestry(studyToDuplicateAncestry);
            duplicateAncestry.setStudy(duplicateStudy);
            newAncestries.add(duplicateAncestry);
            ancestryRepository.save(duplicateAncestry);
        });

        duplicateStudy.setAncestries(newAncestries);
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
        // THOR
        // duplicateStudy.setAuthor(studyToDuplicate.getAuthor() + " DUP");
        duplicateStudy.setPublicationId(studyToDuplicate.getPublicationId());
        duplicateStudy.setInitialSampleSize(studyToDuplicate.getInitialSampleSize());
        duplicateStudy.setReplicateSampleSize(studyToDuplicate.getReplicateSampleSize());

        duplicateStudy.setCnv(studyToDuplicate.getCnv());
        duplicateStudy.setGxe(studyToDuplicate.getGxe());
        duplicateStudy.setGxg(studyToDuplicate.getGxg());
//        duplicateStudy.setGenotypingTechnologies(studyToDuplicate.getGenotypingTechnologies());
//        duplicateStudy.setGenomewideArray(studyToDuplicate.getGenomewideArray());
//        duplicateStudy.setTargetedArray(studyToDuplicate.getTargetedArray());
        duplicateStudy.setDiseaseTrait(studyToDuplicate.getDiseaseTrait());
        duplicateStudy.setSnpCount(studyToDuplicate.getSnpCount());
        duplicateStudy.setQualifier(studyToDuplicate.getQualifier());
        duplicateStudy.setImputed(studyToDuplicate.getImputed());
        duplicateStudy.setPooled(studyToDuplicate.getPooled());
        duplicateStudy.setFullPvalueSet(studyToDuplicate.getFullPvalueSet()); 
        duplicateStudy.setUserRequested(studyToDuplicate.getUserRequested());
        duplicateStudy.setOpenTargets(studyToDuplicate.getOpenTargets());
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

        Collection<GenotypingTechnology> genotypingTechnologies = studyToDuplicate.getGenotypingTechnologies();
        Collection<GenotypingTechnology> genotypingTechnologiesDuplicateStudy = new ArrayList<>();

        if(genotypingTechnologies != null && !genotypingTechnologies.isEmpty()){
            genotypingTechnologiesDuplicateStudy.addAll(genotypingTechnologies);
            duplicateStudy.setGenotypingTechnologies(genotypingTechnologiesDuplicateStudy);
        }

        return duplicateStudy;

    }

    /**
     * Create a study entry in the database
     *
     * @param studyToDuplicateAncestry Ancestry to duplicate
     * @return ancestry
     */
    private Ancestry copyAncestry(Ancestry studyToDuplicateAncestry) {
        Ancestry duplicateAncestry = new Ancestry();
        duplicateAncestry.setType(studyToDuplicateAncestry.getType());
        duplicateAncestry.setNumberOfIndividuals(studyToDuplicateAncestry.getNumberOfIndividuals());
        duplicateAncestry.setDescription(studyToDuplicateAncestry.getDescription());
        duplicateAncestry.setPreviouslyReported(studyToDuplicateAncestry.getPreviouslyReported());
        duplicateAncestry.setSampleSizesMatch(studyToDuplicateAncestry.getSampleSizesMatch());
        duplicateAncestry.setNotes(studyToDuplicateAncestry.getNotes());

        Collection<AncestralGroup> ancestralGroups = studyToDuplicateAncestry.getAncestralGroups();
        Collection<AncestralGroup> ancestralGroupsDuplicateStudy = new ArrayList<>();

        if(ancestralGroups != null && !ancestralGroups.isEmpty()){
            ancestralGroupsDuplicateStudy.addAll(ancestralGroups);
            duplicateAncestry.setAncestralGroups(ancestralGroupsDuplicateStudy);
        }

        Collection<Country> coos = studyToDuplicateAncestry.getCountryOfOrigin();
        Collection<Country> coosDuplicateStudy = new ArrayList<>();

        if(coos != null && !coos.isEmpty()){
            coosDuplicateStudy.addAll(coos);
            duplicateAncestry.setCountryOfOrigin(coosDuplicateStudy);
        }

        Collection<Country> cors = studyToDuplicateAncestry.getCountryOfRecruitment();
        Collection<Country> corsDuplicateStudy = new ArrayList<>();

        if(cors != null && !cors.isEmpty()){
            corsDuplicateStudy.addAll(cors);
            duplicateAncestry.setCountryOfRecruitment(corsDuplicateStudy);
        }

        return duplicateAncestry;
    }

    /**
     * Create duplication study with specific tag
     *
     * @param studyToDuplicate
     *        tagsNoteList
     * @return empty string if no error is raised
     */
    public String create(Study studyToDuplicate, String tagsNoteList, SecureUser secureUser ) {

        String result = "";

        ArrayList<String> tagsNoteToAdd = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(tagsNoteList);
            Integer numStudyToDuplicate = 0;
            for (JsonNode node : jsonNode) {
                numStudyToDuplicate += 1;
                if (node.asText().length() > 1) {
                    tagsNoteToAdd.add(node.asText());
                } else {
                    tagsNoteToAdd.add("Dup " + numStudyToDuplicate.toString());
                }
            }
        } catch (IOException ioe) {
            result = "{\"failed\":\"Something went wrong with the tags to add.\"}";
        }

        for (String tagDuplication : tagsNoteToAdd) {
            try {
                Study duplicateStudy = duplicateStudy(studyToDuplicate, tagDuplication, secureUser);
            } catch (Exception e) {
                result = "{\"failed\":\"Something went wrong during the creation of the duplication!\"}";
            }
        }
        return result;
    }

}