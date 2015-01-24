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
     * A facade service around a {@link uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository} that
     * retrieves all SNPs, and then within the same datasource transaction additionally loads other objects referenced
     * by this SNP (so Genes and Regions).
     * <p>
     * Use this when you know you will need deep information about a SNP and do not have an open session that can be
     * used to lazy load extra data.
     *
     * @return a list of SingleNucleotidePolymorphisms
     */
    @Transactional(readOnly = true)
    public List<Study> deepFindAll() {
        List<Study> allStudies = studyRepository.findAll();
        // iterate over all studies and grab trait info
        getLog().info("Obtained " + allStudies.size() + " studies, starting deep load...");
        allStudies.forEach(this::loadAssociatedData);
        return allStudies;
    }

    @Transactional(readOnly = true)
    public List<Study> deepFindAll(Sort sort) {
        List<Study> studies = studyRepository.findAll(sort);
        // iterate over all studies and grab region info
        getLog().info("Obtained " + studies.size() + " studies, starting deep load...");
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Page<Study> deepFindAll(Pageable pageable) {
        Page<Study> studies = studyRepository.findAll(pageable);
        // iterate over all studies and grab region info
        getLog().info("Obtained " + studies.getSize() + " studies, starting deep load...");
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    @Transactional(readOnly = true)
    public Study deepFetchOne(Study study) {
        loadAssociatedData(study);
        return study;
    }

    @Transactional(readOnly = true)
    public Collection<Study> deepFetchAll(Collection<Study> studies) {
        studies.forEach(this::loadAssociatedData);
        return studies;
    }

    public void loadAssociatedData(Study study) {
        int efoTraitCount = study.getEfoTraits().size();
        getLog().info(
                "Study '" + study.getId() + "' is mapped to " + efoTraitCount + " traits");
    }
}
