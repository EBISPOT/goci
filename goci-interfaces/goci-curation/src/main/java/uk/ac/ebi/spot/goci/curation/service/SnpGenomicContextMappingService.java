package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.GeneRepository;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.ArrayList;
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
 *         <p>
 *         Service class to store genomic context information returned from mapping pipeline.
 */
@Service
public class SnpGenomicContextMappingService {

    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    private GeneRepository geneRepository;
    private GenomicContextRepository genomicContextRepository;

    //Constructor
    @Autowired
    public SnpGenomicContextMappingService(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository,
                                           GeneRepository geneRepository,
                                           GenomicContextRepository genomicContextRepository) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.geneRepository = geneRepository;
        this.genomicContextRepository = genomicContextRepository;
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

                // For each snp with that rs_id add new genomic context
                for (SingleNucleotidePolymorphism snpInDatabase : snpsInDatabase) {

                    Collection<GenomicContext> newSnpGenomicContexts = new ArrayList<>();

                    for (GenomicContext genomicContextInForm : genomicContextsInForm) {

                        // Check gene exists
                        String geneName = genomicContextInForm.getGene().getGeneName().trim();

                        if (!geneName.equalsIgnoreCase("undefined")) {

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

                                    // Save changes
                                    geneRepository.save(existingGeneInDatabase);
                                }
                            }

                            // Create new genomic context
                            Boolean isIntergenic = genomicContextInForm.getIsIntergenic();
                            Boolean isUpstream = genomicContextInForm.getIsUpstream();
                            Boolean isDownstream = genomicContextInForm.getIsDownstream();
                            Long distance = genomicContextInForm.getDistance();
                            String source = genomicContextInForm.getSource();
                            String mappingMethod = genomicContextInForm.getMappingMethod();

                            GenomicContext genomicContext = createGenomicContext(isIntergenic, isUpstream,
                                                                                 isDownstream,
                                                                                 distance,
                                                                                 source,
                                                                                 mappingMethod,
                                                                                 geneName, snpInDatabase);
                         
                            newSnpGenomicContexts.add(genomicContext);
                        }
                    }

                    // Remove old genomic contexts
                    Collection<GenomicContext> snpInDatabaseGenomicContexts = snpInDatabase.getGenomicContexts();
                    for (GenomicContext snpInDatabaseGenomicContext : snpInDatabaseGenomicContexts) {
                        genomicContextRepository.delete(snpInDatabaseGenomicContext);
                    }

                    // Save latest mapped information
                    snpInDatabase.setGenomicContexts(newSnpGenomicContexts);
                    singleNucleotidePolymorphismRepository.save(snpInDatabase);
                }
            }

            // SNP doesn't exist, this should be extremely rare as SNP value is a copy
            // of the variant entered by the curator which
            // by the time mapping is started should already have been saved
            else {
                // TODO WHAT WILL HAPPEN FOR MERGED SNPS
                throw new RuntimeException(
                        "Adding genomic context for SNP not found in database, RS_ID: " + snpRsId);
            }
        }
    }

    // Method to create a gene
    private void createGene(String geneName, String entrezGeneId, String ensemblGeneId) {
        // Create new gene
        Gene newGene = new Gene();
        newGene.setGeneName(geneName);
        newGene.setEntrezGeneId(entrezGeneId);
        newGene.setEnsemblGeneId(ensemblGeneId);

        // Save gene
        geneRepository.save(newGene);
    }

    // Method to create genomic context
    private GenomicContext createGenomicContext(Boolean isIntergenic,
                                                Boolean isUpstream,
                                                Boolean isDownstream,
                                                Long distance,
                                                String source,
                                                String mappingMethod,
                                                String geneName,
                                                SingleNucleotidePolymorphism snpIdInDatabase) {

        GenomicContext genomicContext = new GenomicContext();

        // Find gene
        List<Gene> genesWithMatchingName = geneRepository.findByGeneNameIgnoreCase(geneName);

        // Account for duplicates
        Gene gene = genesWithMatchingName.get(0);

        genomicContext.setGene(gene);
        genomicContext.setIsIntergenic(isIntergenic);
        genomicContext.setIsDownstream(isDownstream);
        genomicContext.setIsUpstream(isUpstream);
        genomicContext.setDistance(distance);
        genomicContext.setSource(source);
        genomicContext.setMappingMethod(mappingMethod);
        genomicContext.setSnp(snpIdInDatabase);

        // Save genomic context
        genomicContextRepository.save(genomicContext);

        return genomicContext;
    }
}
