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
import uk.ac.ebi.spot.goci.model.EnsemblGene;
import uk.ac.ebi.spot.goci.model.EntrezGene;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public Collection<Association> findAllByStudyId(Long studyId) {
        Collection<Association> associations = associationRepository.findByStudyId(studyId);
        associations.forEach(this::loadAssociatedData);
        return associations;
    }

    /**
     * Get in one transaction the list of all Association with : the attached study and its publish date the attached
     * efo traits the attached loci, for each loci : their strongestRiskAlleles, for each alleles : the regions the
     * genomic contexts
     *
     * @return a List of Associations.
     */
    @Transactional(readOnly = true)
    public List<Association> findReallyAll() {
        List<Association> allAssociations = associationRepository.findAll();
        allAssociations.forEach(this::loadAssociatedDataIncludingHousekeeping);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public List<Association> findAll(Sort sort) {
        List<Association> allAssociations = associationRepository.findAll(sort);
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public Page<Association> findAll(Pageable pageable) {
        Page<Association> allAssociations = associationRepository.findAll(pageable);
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public List<Association> findPublishedAssociations() {
        List<Association> allAssociations =
                associationRepository.findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull();
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public List<Association> findPublishedAssociations(Sort sort) {
        List<Association> allAssociations =
                associationRepository.findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
                        sort);
        allAssociations.forEach(this::loadAssociatedData);
        return allAssociations;
    }

    @Transactional(readOnly = true)
    public Page<Association> findPublishedAssociations(Pageable pageable) {
        Page<Association> allAssociations =
                associationRepository.findByStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
                        pageable);
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
                .findByLociStrongestRiskAllelesSnpIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
                        snpId);
        associations.forEach(this::loadAssociatedData);
        return associations;
    }

    @Transactional(readOnly = true)
    public Collection<Association> findPublishedAssociationsByDiseaseTraitId(Long diseaseTraitId) {
        Collection<Association> associations =
                associationRepository.findByStudyDiseaseTraitIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
                        diseaseTraitId);
        associations.forEach(this::loadAssociatedData);
        return associations;
    }

    @Transactional(readOnly = true)
    public Collection<Association> findPublishedAssociationsByEfoTraitId(Long efoTraitId) {
        Collection<Association> associations =
                associationRepository.findByEfoTraitsIdAndStudyHousekeepingCatalogPublishDateIsNotNullAndStudyHousekeepingCatalogUnpublishDateIsNull(
                        efoTraitId);
        associations.forEach(this::loadAssociatedData);
        return associations;
    }

    public void loadAssociatedDataIncludingHousekeeping(Association association) {
        loadAssociatedData(association);
        association.getStudy().getHousekeeping().getCatalogPublishDate();
    }

    @Transactional(readOnly = true)
    public void loadAssociatedData(Association association) {
        int traitCount = association.getEfoTraits().size();
        Study study = studyService.fetchOne(association.getStudy());
        AtomicInteger reportedGeneCount = new AtomicInteger();
        Collection<SingleNucleotidePolymorphism> snps = new HashSet<>();
        Collection<SingleNucleotidePolymorphism> proxySnps = new HashSet<>();
        Collection<Region> regions = new HashSet<>();
        Collection<Gene> mappedGenes = new HashSet<>();
        Map<String, Set<String>> mappedGeneEntrezIds = new HashMap<>();
        Map<String, Set<String>> mappedGeneEnsemblIds = new HashMap<>();
        association.getLoci().forEach(
                locus -> {
                    locus.getStrongestRiskAlleles().stream().map(RiskAllele::getSnp).forEach(
                            snp -> {
                                Collection<Location> snpLocations = snp.getLocations();
                                for (Location location : snpLocations) {
                                    regions.add(location.getRegion());
                                }

                                snp.getGenomicContexts().forEach(context -> {
                                                                     mappedGenes.add(context.getGene());

                                                                     String geneName = context.getGene().getGeneName();
                                                                     Collection<EntrezGene> geneEntrezGeneIds =
                                                                             context.getGene().getEntrezGeneIds();
                                                                     Collection<EnsemblGene> geneEnsemblGeneIds =
                                                                             context.getGene().getEnsemblGeneIds();

                                                                     if (mappedGeneEntrezIds.containsKey(geneName)) {
                                                                         for (EntrezGene entrezGene : geneEntrezGeneIds) {
                                                                             mappedGeneEntrezIds.get(geneName).add(
                                                                                     entrezGene.getEntrezGeneId());
                                                                         }
                                                                     }

                                                                     // First time we see a SNP store the location
                                                                     else {
                                                                         Set<String> entrezIds = new HashSet<>();
                                                                         for (EntrezGene entrezGene : geneEntrezGeneIds) {
                                                                             entrezIds.add(entrezGene.getEntrezGeneId());
                                                                         }
                                                                         mappedGeneEntrezIds.put(geneName,
                                                                                                 entrezIds);
                                                                     }

                                                                     if (mappedGeneEnsemblIds.containsKey(geneName)) {
                                                                         for (EnsemblGene ensemblGene : geneEnsemblGeneIds) {
                                                                             mappedGeneEnsemblIds.get(geneName)
                                                                                     .add(ensemblGene.getEnsemblGeneId());
                                                                         }
                                                                     }

                                                                     // First time we see a SNP store the location
                                                                     else {
                                                                         Set<String> ensemblIds = new HashSet<>();
                                                                         for (EnsemblGene ensemblGene : geneEnsemblGeneIds) {
                                                                             ensemblIds.add(ensemblGene.getEnsemblGeneId());
                                                                         }
                                                                         mappedGeneEntrezIds.put(geneName,
                                                                                                 ensemblIds);
                                                                     }
                                                                 }
                                );
                                snps.add(snp);
                            }
                    );

                    snps.addAll(locus.getStrongestRiskAlleles()
                                        .stream()
                                        .map(RiskAllele::getSnp)
                                        .collect(Collectors.toList()));

                    locus.getStrongestRiskAlleles().forEach(
                            riskAllele -> {
                                if(riskAllele.getProxySnps() != null) {
                                    proxySnps.addAll(riskAllele.getProxySnps());
                                }
                            }
                    ) ;

                    reportedGeneCount.addAndGet(locus.getAuthorReportedGenes().size());
                    locus.getAuthorReportedGenes().forEach(
                            authorReportedGene -> {
                                authorReportedGene.getEnsemblGeneIds().size();
                                authorReportedGene.getEntrezGeneIds().size();
                            }
                    );
                });
        getLog().trace("Association '" + association.getId() + "' is mapped to " +
                               "" + traitCount + " EFO traits where study id = " + study.getId() + " " +
                               "(author reported " + reportedGeneCount + " gene(s)); " +
                               "this reports on " + snps.size() + " SNPs in " + regions.size() + " regions, " +
                               "mapped to " + mappedGenes.size() + " genes.");
    }

//convenience method for when an already loaded & modified association needs to be deleted - this method lazy-loads the association from scratch at deletion time
    public void deleteByAssociationId(Long associationId){
        associationRepository.delete(associationId);
    }
}
