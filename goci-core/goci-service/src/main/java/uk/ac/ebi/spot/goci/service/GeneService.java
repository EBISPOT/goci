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
        return geneRepository.findByAuthorReportedFromLociAssociationStudyId(studyId);
    }

    @Transactional
    public Collection<Gene> findMappedGenesByStudyId(Long studyId) {
        return geneRepository.findByGenomicContextsSnpRiskAllelesLociAssociationStudyId(studyId);
    }

    @Transactional(readOnly = true)
    public Collection<Gene> findReportedGenesBySnpId(Long snpId) {
        return geneRepository.findByAuthorReportedFromLociStrongestRiskAllelesSnpId(snpId);
    }

    public Collection<Gene> findMappedGenesBySnpId(Long snpId) {
        return geneRepository.findByGenomicContextsSnpId(snpId);
    }

    public Collection<Gene> findReportedGenesByAssociationId(Long associationId) {
        return geneRepository.findByAuthorReportedFromLociAssociationId(associationId);
    }

    public Collection<Gene> findMappedGenesByAssociationId(Long associationId) {
        return geneRepository.findByGenomicContextsSnpRiskAllelesLociAssociationId(associationId);
    }

    public Collection<Gene> findReportedGenesByDiseaseTraitId(Long traitId) {
        return geneRepository.findByGenomicContextsSnpRiskAllelesLociAssociationStudyDiseaseTraitId(
                traitId);
    }

    public Collection<Gene> findMappedGenesByDiseaseTraitId(Long traitId) {
        return geneRepository.findByGenomicContextsSnpRiskAllelesLociAssociationEfoTraitsId(traitId);
    }
}
