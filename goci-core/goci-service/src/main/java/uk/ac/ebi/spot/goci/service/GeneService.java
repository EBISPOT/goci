package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.repository.GeneRepository;

import java.util.Collection;

/**
 * Created by dwelter on 12/02/15.
 */
@Service
public class GeneService {

    private GeneRepository geneRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public GeneService(GeneRepository geneRepository) {
        this.geneRepository = geneRepository;
    }

    protected Logger getLog() {
        return log;
    }

    @Transactional(readOnly = true)
    public Collection<Gene> findReportedGenesByStudyId(Long studyId) {
        Collection<Gene> genes = geneRepository.findByAuthorReportedFromLociAssociationStudyId(studyId);
        genes.forEach(this::loadAssociatedData);
        return genes;
    }

    @Transactional
    public Collection<Gene> findMappedGenesByStudyId(Long studyId) {
        Collection<Gene> genes = geneRepository.findByGenomicContextsSnpRiskAllelesLociAssociationStudyId(studyId);
        genes.forEach(this::loadAssociatedData);
        return genes;
    }

    @Transactional(readOnly = true)
    public Collection<Gene> findReportedGenesBySnpId(Long snpId) {
        Collection<Gene> genes = geneRepository.findByAuthorReportedFromLociStrongestRiskAllelesSnpId(snpId);
        genes.forEach(this::loadAssociatedData);
        return genes;
    }

    public Collection<Gene> findMappedGenesBySnpId(Long snpId) {
        Collection<Gene> genes = geneRepository.findByGenomicContextsSnpId(snpId);
        genes.forEach(this::loadAssociatedData);
        return genes;
    }

    public Collection<Gene> findReportedGenesByAssociationId(Long associationId) {
        Collection<Gene> genes = geneRepository.findByAuthorReportedFromLociAssociationId(associationId);
        genes.forEach(this::loadAssociatedData);
        return genes;
    }

    public Collection<Gene> findMappedGenesByAssociationId(Long associationId) {
        Collection<Gene> genes = geneRepository.findByGenomicContextsSnpRiskAllelesLociAssociationId(associationId);
        genes.forEach(this::loadAssociatedData);
        return genes;
    }

    public Collection<Gene> findReportedGenesByDiseaseTraitId(Long traitId) {
        Collection<Gene> genes = geneRepository.findByGenomicContextsSnpRiskAllelesLociAssociationStudyDiseaseTraitId(
                traitId);
        genes.forEach(this::loadAssociatedData);
        return genes;
    }

    public Collection<Gene> findMappedGenesByDiseaseTraitId(Long traitId) {
        Collection<Gene> genes = geneRepository.findByGenomicContextsSnpRiskAllelesLociAssociationEfoTraitsId(traitId);
        genes.forEach(this::loadAssociatedData);
        return genes;
    }

    public void loadAssociatedData(Gene gene) {
//        int efoTraitCount = gene.getEfoTraits().size();
//        int associationCount = gene.getAssociations().size();
//        Date publishDate = gene.getHousekeeping().getPublishDate();
//        if (publishDate != null) {
//            getLog().info(
//                    "Study '" + gene.getId() + "' is mapped to " + efoTraitCount + " traits, " +
//                            "has " + associationCount + " associations and was published on " + publishDate.toString());
//        }
//        else {
//            getLog().info(
//                    "Study '" + gene.getId() + "' is mapped to " + efoTraitCount + " traits, " +
//                            "has " + associationCount + " associations and is not yet published");
//        }
    }
}
