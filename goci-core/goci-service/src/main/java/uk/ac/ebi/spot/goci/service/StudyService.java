package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
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

            int ancestryCount = study.getAncestries().size();

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
}
