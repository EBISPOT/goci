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
import java.util.Date;
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
 *         Service class to store genomic context information returned from mapping pipeline. Begins by storing gene
 *         information and then creating genomic context information. The information from the current run of the
 *         pipeline is always considered most up-to-date therefore in most cases previous information is deleted.
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

        // Process the gene information first so all IDs cna be updated and any new genes created
        processGenes(genomicContexts);

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

    private void processGenes(Collection<GenomicContext> genomicContexts) {

        // Need to flatten down genomic context gene information
        // and create structure linking each gene symbol to its
        // complete set of current Ensembl and Entrez IDs
        Map<String, Set<String>> geneToEnsemblIdMap = new HashMap<>();
        Map<String, Set<String>> geneToEntrezIdMap = new HashMap<>();


        // Loop over each genomic context and store information on external IDs linked to gene symbol
        for (GenomicContext genomicContext : genomicContexts) {

            // Check gene exists
            String geneName = genomicContext.getGene().getGeneName().trim();

            if (!geneName.equalsIgnoreCase("undefined")) {

                // TODO THIS WILL NEED TO CHANGE AS THIS WILL BE A COLLECTION
                String entrezGeneId = genomicContext.getGene().getEntrezGeneId();
                String ensemblGeneId = genomicContext.getGene().getEnsemblGeneId();

                // Store gene name and Ensembl Id(s)
                if (geneToEnsemblIdMap.containsKey(geneName)) {
                    geneToEnsemblIdMap.get(geneName).add(ensemblGeneId);
                }

                else {
                    Set<String> ensemblGeneIds = new HashSet<>();
                    ensemblGeneIds.add(ensemblGeneId);
                    geneToEnsemblIdMap.put(geneName, ensemblGeneIds);
                }

                // Store gene name and Entrez Id(s)
                if (geneToEntrezIdMap.containsKey(geneName)) {
                    geneToEntrezIdMap.get(geneName).add(entrezGeneId);
                }

                else {
                    Set<String> entrezGeneIds = new HashSet<>();
                    entrezGeneIds.add(entrezGeneId);
                    geneToEntrezIdMap.put(geneName, entrezGeneIds);
                }
            }
        }

        // Store genes
        if (geneToEnsemblIdMap.size() > 0) {
            storeGenes(geneToEnsemblIdMap, "Ensembl");
        }

        if (geneToEntrezIdMap.size() > 0) {
            storeGenes(geneToEntrezIdMap, "Entrez");
        }
    }

    // Create/update genes with latest mapping information
    private void storeGenes(Map<String, Set<String>> geneToExternalIdMap, String source) {
        for (String geneName : geneToExternalIdMap.keySet()) {

            Set<String> externalIds = geneToExternalIdMap.get(geneName);
            List<Gene> existingGenesInDatabase = geneRepository.findByGeneNameIgnoreCase(geneName);

            // If gene is not already in database then create one
            if (existingGenesInDatabase.size() == 0) {
                createGene(geneName, externalIds, source);
            }

            // Update gene
            else {
                // TODO PROBABLY NEED TO REMOVE OLD IDS
                // 1. FIND IDS LINKED TO GENE
                // 2. MAKE SURE ID IS NOT LINKED TO ANOTHER GENE
                // 3. DELETE ID
                for (Gene existingGeneInDatabase : existingGenesInDatabase) {
                    if (source.equalsIgnoreCase("Ensembl")) {
                        // SET ENSEMBL IDS
                    }

                    if (source.equalsIgnoreCase("Entrez")) {
                        // SET ENTREZ IDS
                    }

                    // Save changes
                    geneRepository.save(existingGeneInDatabase);
                }
            }


        }
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

                    // Remove old genomic contexts, as these will be updated with latest mapping
                    Collection<GenomicContext> snpInDatabaseGenomicContexts = snpInDatabase.getGenomicContexts();
                    for (GenomicContext snpInDatabaseGenomicContext : snpInDatabaseGenomicContexts) {
                        genomicContextRepository.delete(snpInDatabaseGenomicContext);
                    }

                    Collection<GenomicContext> newSnpGenomicContexts = new ArrayList<>();

                    for (GenomicContext genomicContextInForm : genomicContextsInForm) {

                        // Gene should already have been created
                        String geneName = genomicContextInForm.getGene().getGeneName().trim();

                        if (!geneName.equalsIgnoreCase("undefined")) {

                            // Create new genomic context
                            Boolean isIntergenic = genomicContextInForm.getIsIntergenic();
                            Boolean isUpstream = genomicContextInForm.getIsUpstream();
                            Boolean isDownstream = genomicContextInForm.getIsDownstream();
                            Long distance = genomicContextInForm.getDistance();
                            String source = genomicContextInForm.getSource();
                            String mappingMethod = genomicContextInForm.getMappingMethod();
                            Boolean isClosestGene = genomicContextInForm.getIsClosestGene();

                            GenomicContext genomicContext = createGenomicContext(isIntergenic,
                                                                                 isUpstream,
                                                                                 isDownstream,
                                                                                 distance,
                                                                                 source,
                                                                                 mappingMethod,
                                                                                 geneName,
                                                                                 snpInDatabase,
                                                                                 isClosestGene);

                            newSnpGenomicContexts.add(genomicContext);
                        }
                    }

                    // Save latest mapped information
                    snpInDatabase.setGenomicContexts(newSnpGenomicContexts);
                    // Update the last update date
                    snpInDatabase.setLastUpdateDate(new Date());
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
    private void createGene(String geneName, Set<String> externalIds, String source) {
        // Create new gene
        Gene newGene = new Gene();
        newGene.setGeneName(geneName);

        if (source.equalsIgnoreCase("Ensembl")) {
            // SET ENSEMBL IDS
        }
        if (source.equalsIgnoreCase("Entrez")) {
            // SET ENTREZ IDS
        }

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
                                                SingleNucleotidePolymorphism snpIdInDatabase,
                                                Boolean isClosestGene) {

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
        genomicContext.setIsClosestGene(isClosestGene);

        // Save genomic context
        genomicContextRepository.save(genomicContext);

        return genomicContext;
    }
}
