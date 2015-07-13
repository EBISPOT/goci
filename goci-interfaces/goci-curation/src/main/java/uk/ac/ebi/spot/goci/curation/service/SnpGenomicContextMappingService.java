package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.GeneRepository;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by emma on 10/07/2015.
 *
 * @author emma
 */
@Service
public class SnpGenomicContextMappingService {

    private GenomicContextRepository genomicContextRepository;
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    private GeneRepository geneRepository;

    //Constructor
    @Autowired
    public SnpGenomicContextMappingService(GenomicContextRepository genomicContextRepository,
                                           SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository,
                                           GeneRepository geneRepository) {
        this.genomicContextRepository = genomicContextRepository;
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.geneRepository = geneRepository;
    }

    public void processGenomicContext(Collection<GenomicContext> genomicContexts) {

        // Need flatten down genomic context information
        // and create structure linking each RS_ID to its complete set of new mapped data
        Map<String, Set<GenomicContext>> snpToGenomicContextMap = new HashMap<>();

        for (GenomicContext genomicContext : genomicContexts) {
            String snpIdInGenomicContext = genomicContext.getSnp().getRsId();


            // Next time we see SNP, add genomic context to set
            if (snpToGenomicContextMap.containsKey(snpIdInGenomicContext)) {
                snpToGenomicContextMap.get(snpIdInGenomicContext).add(genomicContext);
            }

            // First time we see a SNP store the genomic context
            else {
                Set<GenomicContext> snpGenomicContext = new HashSet<>();
                snpGenomicContext.add(genomicContext);
                snpToGenomicContextMap.put(snpIdInGenomicContext, snpGenomicContext);
            }
        }

        storeSnpGenomicContext(snpToGenomicContextMap);
    }

    private void storeSnpGenomicContext(Map<String, Set<GenomicContext>> snpToGenomicContextMap) {

        // Go through each rs_id and its associated genomic contexts returned from the mapping pipeline
        for (String snpRsId : snpToGenomicContextMap.keySet()) {

            Set<GenomicContext> genomicContextsInForm = snpToGenomicContextMap.get(snpRsId);

            // Check if the SNP exists
            List<SingleNucleotidePolymorphism> snpsInDatabase =
                    singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(snpRsId);

            if (!snpsInDatabase.isEmpty()) {

                // For each snp with that rs_id update or add genomic context
                for (SingleNucleotidePolymorphism snpInDatabase : snpsInDatabase) {

                    for (GenomicContext genomicContextInForm : genomicContextsInForm) {

                        // Check gene exists
                        String geneName = genomicContextInForm.getGene().getGeneName().trim();
                        String entrezGeneId = genomicContextInForm.getGene().getEntrezGeneId();
                        String ensemblGeneId = genomicContextInForm.getGene().getEnsemblGeneId();

                        List<Gene> existingGenesInDatabase = geneRepository.findByGeneNameIgnoreCase(geneName);

                        // If gene is not already in database then create one
                        if (existingGenesInDatabase.size() == 0) {
                            createGene(geneName, entrezGeneId, ensemblGeneId);
                        }

                        // Update gene
                        else {
                            for (Gene existingGeneInDatabase : existingGenesInDatabase) {
                                if (entrezGeneId != null) {
                                    existingGeneInDatabase.setEntrezGeneId(entrezGeneId);
                                }

                                if (ensemblGeneId != null) {
                                    existingGeneInDatabase.setEnsemblGeneId(ensemblGeneId);
                                }

                                geneRepository.save(existingGeneInDatabase);
                            }
                        }

                        Boolean isIntergenic = genomicContextInForm.getIsIntergenic();
                        Boolean isUpstream = genomicContextInForm.getIsUpstream();
                        Boolean isDownstream = genomicContextInForm.getIsDownstream();
                        Long distance = genomicContextInForm.getDistance();
                        String source = genomicContextInForm.getSource();
                        String mappingMethod = genomicContextInForm.getMappingMethod();
                        Long snpIdInDatabase = snpInDatabase.getId();

                        Collection<GenomicContext> existingGenomicContexts =
                                genomicContextRepository.findByIsIntergenicAndIsUpstreamAndIsDownstreamAndDistanceAndGeneGeneNameAndSourceAndMappingMethodAndSnpId(
                                        isIntergenic,
                                        isUpstream,
                                        isDownstream,
                                        distance,
                                        geneName,
                                        source,
                                        mappingMethod,
                                        snpIdInDatabase);

                        // No genomic contexts exist that match that values from mapping form
                        if (existingGenomicContexts.size() == 0) {


         /*                   // Need to decide in what scenario we would update a genomic context
                            if () {}

                            // Create genomic context
                            else {

                            }*/

                        }

                    }
                }
            }

            // SNP doesn't exist, this should be extremely rare as SNP value is a copy
            // of the variant entered by the curator which
            // by the time mapping is started should already have been saved
            else {
                // TODO WHAT WILL HAPPEN FOR MERGED SNPS
                throw new RuntimeException("Adding genomic context for SNP not found in database, RS_ID: " + snpRsId);
            }


        }
    }

    private void createGene(String geneName, String entrezGeneId, String ensemblGeneId) {
        // Create new gene
        Gene newGene = new Gene();
        newGene.setGeneName(geneName);
        newGene.setEntrezGeneId(entrezGeneId);
        newGene.setEnsemblGeneId(ensemblGeneId);

        // Save gene
        geneRepository.save(newGene);
    }
}
