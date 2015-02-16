package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        // iterate over all studies and grab trait info
        getLog().info("Obtained " + allStudies.size() + " studies, starting deep load...");
        allStudies.forEach(this::loadAssociatedData);
        return allStudies;
    }

    @Transactional(readOnly = true)
    public List<Study> findAll(Sort sort) {
        List<Study> studies = studyRepository.findAll(sort);
        // iterate over all studies and grab region info
        getLog().info("Obtained " + studies.size() + " studies, starting deep load...");
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> findAll(Pageable pageable) {
        Page<Study> studies = studyRepository.findAll(pageable);
        // iterate over all studies and grab region info
        getLog().info("Obtained " + studies.getSize() + " studies, starting deep load...");
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
        List<Study> studies = studyRepository.findByHousekeepingPublishDateIsNotNull();
        // iterate over all studies and grab trait info
        getLog().info("Obtained " + studies.size() + " studies, starting deep load...");
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public List<Study> findPublishedStudies(Sort sort) {
        List<Study> studies = studyRepository.findByHousekeepingPublishDateIsNotNull(sort);
        // iterate over all studies and grab region info
        getLog().info("Obtained " + studies.size() + " studies, starting deep load...");
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> findPublishedStudies(Pageable pageable) {
        Page<Study> studies = studyRepository.findByHousekeepingPublishDateIsNotNull(pageable);
        // iterate over all studies and grab region info
        getLog().info("Obtained " + studies.getSize() + " studies, starting deep load...");
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
                studyRepository.findByAssociationsLociStrongestRiskAllelesSnpIdAndHousekeepingPublishDateIsNotNull(snpId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Collection<Study> findByAssociationId(Long associationId) {
        Collection<Study> studies =
                studyRepository.findByAssociationsIdAndHousekeepingPublishDateIsNotNull(associationId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Collection<Study> findByDiseaseTraitId(Long diseaseTraitId) {
        Collection<Study> studies =
                studyRepository.findByDiseaseTraitIdAndHousekeepingPublishDateIsNotNull(diseaseTraitId);
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    public void loadAssociatedData(Study study) {
        int efoTraitCount = study.getEfoTraits().size();
        int associationCount = study.getAssociations().size();
        Date publishDate = study.getHousekeeping().getPublishDate();
        if (publishDate != null) {
            getLog().info(
                    "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                            "has " + associationCount + " associations and was published on " + publishDate.toString());
        }
        else {
            getLog().info(
                    "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits, " +
                            "has " + associationCount + " associations and is not yet published");
        }
    }
}
