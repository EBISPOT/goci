package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/01/15
 */
@Service
public class StudyService {
    private StudyRepository studyRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public StudyService(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
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

    @Transactional(readOnly = true)
    public List<Study> findReallyAll(){
        List<Study> allStudies = studyRepository.findAll();
        allStudies.forEach(this::loadAssociatedDataAndSnp);

        return allStudies;
    }


    @Transactional(readOnly = true)
    public List<Study> findAll(Sort sort) {
        List<Study> studies = studyRepository.findAll(sort);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> findAll(Pageable pageable) {
        Page<Study> studies = studyRepository.findAll(pageable);
        studies.forEach(this::loadAssociatedData);
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
        List<Study> studies = studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull();
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public List<Study> findPublishedStudies(Sort sort) {
        List<Study> studies = studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
                sort);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> findPublishedStudies(Pageable pageable) {
        Page<Study> studies = studyRepository.findByHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
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
                studyRepository.findByAssociationsLociStrongestRiskAllelesSnpIdAndHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
                        snpId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Collection<Study> findByAssociationId(Long associationId) {
        Collection<Study> studies =
                studyRepository.findByAssociationsIdAndHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
                        associationId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Collection<Study> findByDiseaseTraitId(Long diseaseTraitId) {
        Collection<Study> studies =
                studyRepository.findByDiseaseTraitIdAndHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(diseaseTraitId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    public void loadAssociatedData(Study study) {
        int efoTraitCount = study.getEfoTraits().size();
        int associationCount = study.getAssociations().size();
        Date publishDate = study.getHousekeeping().getCatalogPublishDate();
        if (publishDate != null) {
            getLog().trace(
                    "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                            "has " + associationCount + " associations and was published on " + publishDate.toString());
        }
        else {
            getLog().trace(
                    "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                            "has " + associationCount + " associations and is not yet published");
        }
    }

    public void loadAssociatedDataAndSnp(Study study) {
        int efoTraitCount = study.getEfoTraits().size();
        int associationCount = study.getAssociations().size();
        int snpCount = study.getSingleNucleotidePolymorphisms().size();
//        System.out.println("BONJOUR");
//        getLog().error("BONJOUR");
        for(SingleNucleotidePolymorphism snp : study.getSingleNucleotidePolymorphisms()){
            int regionCount = snp.getRegions().size();
            getLog().trace("Snp '" + snp.getId() + "' is linked to " + regionCount + " regions.");
//            for(Region region : snp.getRegions()){
//                region.getId();
//            }
        }
        Date publishDate = study.getHousekeeping().getCatalogPublishDate();
        if (publishDate != null) {
            getLog().trace(
                    "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                            "has " + associationCount + " associations, " + snpCount + " snps and was published on " + publishDate.toString());
        }
        else {
            getLog().trace(
                    "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                            "has " + associationCount + " associations, " + snpCount + " and is not yet published");
        }
    }

}
