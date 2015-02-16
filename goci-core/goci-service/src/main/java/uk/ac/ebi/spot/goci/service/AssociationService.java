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
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/01/15
 */
@Service
public class AssociationService {
    private AssociationRepository associationRepository;

    private StudyService studyService;
    private SingleNucleotidePolymorphismService snpService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public AssociationService(AssociationRepository associationRepository,
                              StudyService studyService,
                              SingleNucleotidePolymorphismService snpService) {
        this.associationRepository = associationRepository;
        this.studyService = studyService;
        this.snpService = snpService;
    }

    protected Logger getLog() {
        return log;
    }

    /**
     * A facade service around a {@link uk.ac.ebi.spot.goci.repository.AssociationRepository} that retrieves all
     * associations, and then within the same datasource transaction additionally loads other objects referenced by this
     * association (so Genes and Regions).
     * <p>
     * Use this when you know you will need deep information about a association and do not have an open session that
     * can be used to lazy load extra data.
     *
     * @return a list of Associations
     */
    @Transactional(readOnly = true)
    public List<Association> findAll() {
        List<Association> allAssociations = associationRepository.findAll();
        // iterate over all Associations and grab region info
        getLog().info("Obtained " + allAssociations.size() + " associations, starting deep load...");
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public List<Association> findAll(Sort sort) {
        List<Association> allAssociations = associationRepository.findAll(sort);
        // iterate over all Associations and grab region info
        getLog().info("Obtained " + allAssociations.size() + " associations, starting deep load...");
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public Page<Association> findAll(Pageable pageable) {
        Page<Association> allAssociations = associationRepository.findAll(pageable);
        // iterate over all Associations and grab region info
        getLog().info("Obtained " + allAssociations.getSize() + " associations, starting deep load...");
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public List<Association> findPublishedAssociations() {
        List<Association> allAssociations = associationRepository.findByStudyHousekeepingPublishDateIsNotNull();
        // iterate over all Associations and grab region info
        getLog().info("Obtained " + allAssociations.size() + " associations, starting deep load...");
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public List<Association> findPublishedAssociations(Sort sort) {
        List<Association> allAssociations = associationRepository.findByStudyHousekeepingPublishDateIsNotNull(sort);
        // iterate over all Associations and grab region info
        getLog().info("Obtained " + allAssociations.size() + " associations, starting deep load...");
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public Page<Association> findPublishedAssociations(Pageable pageable) {
        Page<Association> allAssociations = associationRepository.findByStudyHousekeepingPublishDateIsNotNull(pageable);
        // iterate over all Associations and grab region info
        getLog().info("Obtained " + allAssociations.getSize() + " associations, starting deep load...");
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public Collection<Association> findPublishedAssociationsByStudyId(Long studyId) {
        Collection<Association> associations = associationRepository.findByStudyId(studyId);
        associations.forEach(this::loadAssociatedData);
        return associations;
    }

    @Transactional(readOnly = true)
    public Collection<Association> findPublishedAssociationsBySnpId(Long snpId) {
        Collection<Association> associations = associationRepository
                .findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingPublishDateIsNotNull(snpId);
        associations.forEach(this::loadAssociatedData);
        return associations;
    }

    @Transactional
    public Collection<Association> findPublishedAssociationsByDiseaseTraitId(Long diseaseTraitId) {
        Collection<Association> associations = associationRepository.findByStudyDiseaseTraitId(diseaseTraitId);
        associations.forEach(this::loadAssociatedData);
        return associations;
    }


    public void loadAssociatedData(Association association) {
        int traitCount = association.getEfoTraits().size();
        Study study = studyService.fetchOne(association.getStudy());
        AtomicInteger reportedGeneCount = new AtomicInteger();
        Collection<SingleNucleotidePolymorphism> snps = new HashSet<>();
        Collection<Region> regions = new HashSet<>();
        Collection<Gene> mappedGenes = new HashSet<>();
        association.getLoci().forEach(
                locus -> {
                    locus.getStrongestRiskAlleles().stream().map(RiskAllele::getSnp).forEach(
                            snp -> {
                                snp.getRegions().forEach(regions::add);
                                snp.getGenomicContexts().forEach(context -> mappedGenes.add(context.getGene()));
                                snps.add(snp);
                            }
                    );

                    snps.addAll(locus.getStrongestRiskAlleles()
                                        .stream()
                                        .map(RiskAllele::getSnp)
                                        .collect(Collectors.toList()));
                    reportedGeneCount.addAndGet(locus.getAuthorReportedGenes().size());
                });
        getLog().info("Association '" + association.getId() + "' is mapped to " +
                              "" + traitCount + " EFO traits where study id = " + study.getId() + " " +
                              "(author reported " + reportedGeneCount + " gene(s)); " +
                              "this reports on " + snps.size() + " SNPs in " + regions.size() + " regions, " +
                              "mapped to " + mappedGenes.size() + " genes.");
    }
}
