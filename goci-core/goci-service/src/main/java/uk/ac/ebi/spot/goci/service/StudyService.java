package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.*;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/01/15
 */
@Service
public class StudyService {
    private StudyRepository studyRepository;

    private CuratorTrackingService curatorTrackingService;

    private WeeklyTrackingService weeklyTrackingService;

    private StudyNoteService studyNoteService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public StudyService(StudyRepository studyRepository,
                        CuratorTrackingService curatorTrackingService,
                        WeeklyTrackingService weeklyTrackingService,
                        StudyNoteService studyNoteService) {
        this.studyRepository = studyRepository;
        this.curatorTrackingService = curatorTrackingService;
        this.weeklyTrackingService = weeklyTrackingService;
        this.studyNoteService = studyNoteService;
    }

    protected Logger getLog() {
        return log;
    }

    /**
     * A facade service around a {@link uk.ac.ebi.spot.goci.repository.StudyRepository} that retrieves all studies, and
     * then within the same datasource transaction additionally loads other objects referenced by this study (traits,
     * associations, housekeeping).
     * <p>
     * Use this when you know you will need deep information about a study and do not have an open session that can be
     * used to lazy load extra data.
     *
     * @return a list of Studies
     */
    @Transactional(readOnly = true)
    public List<Study> findAll() {
        List<Study> allStudies = studyRepository.findAll();
        allStudies.forEach(this::loadAssociatedData);
        return allStudies;
    }

    /**
     * Get in one transaction all the studies, plus associated Associations, plus associated SNPs and their regions,
     * plus the studies publish date.
     *
     * @return a List of Studies
     */
    @Transactional(readOnly = true)
    public List<Study> deepFindAll() {
        List<Study> allStudies = studyRepository.findAll();
        allStudies.forEach(this::deepLoadAssociatedData);
        return allStudies;
    }


    @Transactional(readOnly = true)
    public List<Study> findAll(Sort sort) {
        List<Study> studies = studyRepository.findAll(sort);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public List<Study> deepFindAll(Sort sort) {
        List<Study> studies = studyRepository.findAll(sort);
        studies.forEach(this::deepLoadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> findAll(Pageable pageable) {
        Page<Study> studies = studyRepository.findAll(pageable);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> deepFindAll(Pageable pageable) {
        Page<Study> studies = studyRepository.findAll(pageable);
        studies.forEach(this::deepLoadAssociatedData);
        return studies;
    }

    /**
     * A facade service around a {@link uk.ac.ebi.spot.goci.repository.StudyRepository} that retrieves all studies, and
     * then within the same datasource transaction additionally loads other objects referenced by this study (traits,
     * associations, housekeeping).
     * <p>
     * Use this when you know you will need deep information about a study and do not have an open session that can be
     * used to lazy load extra data.
     *
     * @return a list of Studies
     */
    @Transactional(readOnly = true)
    public List<Study> findPublishedStudies() {
        List<Study> studies =
                studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull();
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public List<Study> deepFindPublishedStudies() {
        List<Study> studies =
                studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull();
        studies.forEach(this::deepLoadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public List<Study> deepFindUnPublishedStudies() {
        List<Study> studies =
                studyRepository.findByHousekeepingCatalogPublishDateIsNullOrHousekeepingCatalogUnpublishDateIsNotNull();
        studies.forEach(this::deepLoadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public List<Study> findPublishedStudies(Sort sort) {
        List<Study> studies =
                studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
                        sort);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> findPublishedStudies(Pageable pageable) {
        Page<Study> studies =
                studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
                        pageable);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Study fetchOne(Study study) {
        loadAssociatedData(study);
        return study;
    }

    @Transactional(readOnly = true)
    public Collection<Study> fetchAll(Collection<Study> studies) {
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Collection<Study> findBySnpId(Long snpId) {
        Collection<Study> studies =
                studyRepository.findByAssociationsLociStrongestRiskAllelesSnpIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
                        snpId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Collection<Study> findByAssociationId(Long associationId) {
        Collection<Study> studies =
                studyRepository.findByAssociationsIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
                        associationId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Collection<Study> findByDiseaseTraitId(Long diseaseTraitId) {
        Collection<Study> studies =
                studyRepository.findByDiseaseTraitIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
                        diseaseTraitId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }



    public void loadAssociatedData(Study study) {
        int efoTraitCount = study.getEfoTraits().size();
        int associationCount = study.getAssociations().size();
        int ancestryCount = study.getAncestries().size();

        study.getAncestries().forEach(
                ancestry -> {
                    int groupCount = ancestry.getAncestralGroups().size();
                    int coo = ancestry.getCountryOfOrigin().size();
                    int cor = ancestry.getCountryOfRecruitment().size();
                    getLog().trace("Ancestry " + ancestry.getId() + " has "
                                           + groupCount + " ancestral groups, "
                                           + coo + " countries of origin and "
                                           + cor + " countries of recruitment");
                }
        );

        Collection<Author> authorArrayList = new ArrayList<>();
        // Extract the author in order
        study.getPublicationId().getPublicationAuthors().forEach(publicationAuthor ->{
            authorArrayList.add(publicationAuthor.getAuthor());
        });



        int platformCount = study.getPlatforms().size();
        Date publishDate = study.getHousekeeping().getCatalogPublishDate();
        if (publishDate != null) {
            getLog().trace(
                    "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                            "has " + associationCount + " associations, " + ancestryCount +
                            " ancestry entries, " + platformCount + " platform manufacturers "  +
                            " and was published on " + publishDate.toString());
        }
        else {
            getLog().trace(
                    "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                            "has " + associationCount + " associations, " + ancestryCount +
                            " ancestry entries , " + platformCount + " platform manufacturers " +
                            "and is not yet published");
        }
    }

    public void deepLoadAssociatedData(Study study) {
        int efoTraitCount = study.getEfoTraits().size();
        int associationCount = study.getAssociations().size();
//        int snpCount = study.getSingleNucleotidePolymorphisms().size();
        int platformCount = study.getPlatforms().size();

        int ancestryCount = study.getAncestries().size();

        study.getAncestries().forEach(
                ancestry -> {
                    int groupCount = ancestry.getAncestralGroups().size();
                    int coo = ancestry.getCountryOfOrigin().size();
                    int cor = ancestry.getCountryOfRecruitment().size();
                    getLog().trace("Ancestry " + ancestry.getId() + " has "
                        + groupCount + " ancestral groups, "
                        + coo + " countries of origin and "
                        + cor + " countries of recruitment");
                }
        );


        Collection<SingleNucleotidePolymorphism> snps = new ArrayList<>();
        study.getAssociations().forEach(
                association -> {
                    association.getLoci().forEach(
                            locus -> {
                                locus.getStrongestRiskAlleles().forEach(
                                        riskAllele -> {
                                            snps.add(riskAllele.getSnp());
                                        }
                                );
                            }
                    );
                }
        );
        int snpCount = snps.size();


//        for (SingleNucleotidePolymorphism snp : study.getSingleNucleotidePolymorphisms()) {
        for (SingleNucleotidePolymorphism snp : snps) {

            int locationCount = snp.getLocations().size();
            getLog().trace("Snp '" + snp.getId() + "' is linked to " + locationCount + " regions.");


            for (Association association : study.getAssociations()) {
                int lociCount = association.getLoci().size();
                int associationEfoTraitCount = association.getEfoTraits().size();
                getLog().trace("Association '" + association.getId() + "' is linked to " + lociCount + " loci and " +
                                       associationEfoTraitCount + "efo traits.");
                for (Locus locus : association.getLoci()) {
                    int riskAlleleCount = locus.getStrongestRiskAlleles().size();
                    getLog().trace("Locus '" + locus.getId() + "' is linked to " + riskAlleleCount + " risk alleles.");
                    for (RiskAllele riskAllele : locus.getStrongestRiskAlleles()) {
                        SingleNucleotidePolymorphism riskAlleleSnp = riskAllele.getSnp();
                        int riskAlleleSnpRegionCount = riskAlleleSnp.getLocations().size();
                        getLog().trace("Snp '" + riskAlleleSnp.getId() + "' is linked to " + riskAlleleSnpRegionCount +
                                               " regions.");
                    }
                }
            }


            Date publishDate = study.getHousekeeping().getCatalogPublishDate();
            if (publishDate != null) {
                getLog().trace(
                        "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                                "has " + associationCount + " associations, " + snpCount + " snps, " + ancestryCount +
                                " ancestry entries , " + platformCount + " platform manufacturers " +
                                " and was published on " +
                                publishDate.toString());
            }
            else {
                getLog().trace(
                        "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                                "has " + associationCount + " associations, " + snpCount + " snps, , " +
                                ancestryCount + " ancestry entries , " + platformCount + " platform manufacturers "  +
                                " and is not yet published");
            }
        }
    }

    // Shared with === DataDeletionService and StudyDeletionService ===
    public void deleteRelatedInfoByStudy(Study study) {
        // Delete the curatorTracking rows related
        curatorTrackingService.deleteByStudy(study);

        // Delete the weeklyTracking rows related
        weeklyTrackingService.deleteByStudy(study);

        // Delete the note rows related
        studyNoteService.deleteAllNoteByStudy(study);
    }

//convenience method for when an already loaded & modified study needs to be deleted - this method lazy-loads the study from scratch at deletion time
    public void deleteByStudyId(Long studyId){
        studyRepository.delete(studyId);
    }


    public Optional<Study> getValue(Study study) {
        return (study != null) ? Optional.of(study) : Optional.empty();
    }


    public Optional<Study> findOptionalByStudyId(Long studyId) {
        Study study = studyRepository.findOne(studyId);
        return getValue(study);
    }

    //#xintodo this could be the place to add exception if a study is null
    // CM: to avoid any exception and return NULL I use Optional
    public Study findOne(Long id) {
        Optional<Study> study = findOptionalByStudyId(id);
        return (study.isPresent()) ? study.get(): null;
    }

    public void save(Study study) {
        save(study);
    }

}