package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.EnsemblGene;
import uk.ac.ebi.spot.goci.model.EntrezGene;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.EnsemblGeneRepository;
import uk.ac.ebi.spot.goci.repository.EntrezGeneRepository;
import uk.ac.ebi.spot.goci.repository.GeneRepository;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.LocationRepository;
import uk.ac.ebi.spot.goci.repository.RegionRepository;
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
    private EnsemblGeneRepository ensemblGeneRepository;
    private EntrezGeneRepository entrezGeneRepository;
    private LocationRepository locationRepository;
    private RegionRepository regionRepository;

    //Constructor
    @Autowired
    public SnpGenomicContextMappingService(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository,
                                           GeneRepository geneRepository,
                                           GenomicContextRepository genomicContextRepository,
                                           EnsemblGeneRepository ensemblGeneRepository,
                                           EntrezGeneRepository entrezGeneRepository,
                                           LocationRepository locationRepository,
                                           RegionRepository regionRepository) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.geneRepository = geneRepository;
        this.genomicContextRepository = genomicContextRepository;
        this.ensemblGeneRepository = ensemblGeneRepository;
        this.entrezGeneRepository = entrezGeneRepository;
        this.locationRepository = locationRepository;
        this.regionRepository = regionRepository;
    }

    /**
     * Takes genomic context information returned by mapping pipeline and creates a structure that links an rs_id to all
     * its genomic context objects. This ensures we can do a single update based on latest mapping information.
     *
     * @param genomicContexts object holding gene and snp mapping information
     */
    public void processGenomicContext(Collection<GenomicContext> genomicContexts) {

        // Process the gene information first so all IDs can be updated and any new genes created
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

        // Store genomic context information
        storeSnpGenomicContext(snpToGenomicContextMap);
    }


    /**
     * Extract gene information from genomic contexts returned from mapping pipeline
     *
     * @param genomicContexts object holding gene and snp mapping information
     */
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

                // Retrieve the latest Ensembl/Entrez IDs for the named gene from the latest mapping run
                Collection<EnsemblGene> ensemblGeneIds = genomicContext.getGene().getEnsemblGeneIds();
                for (EnsemblGene ensemblGene : ensemblGeneIds) {

                    String ensemblId = ensemblGene.getEnsemblGeneId();
                    if (ensemblId != null) {
                        if (geneToEnsemblIdMap.containsKey(geneName)) {
                            geneToEnsemblIdMap.get(geneName).add(ensemblId);
                        }

                        else {
                            Set<String> ensemblGeneIdsSet = new HashSet<>();
                            ensemblGeneIdsSet.add(ensemblId);
                            geneToEnsemblIdMap.put(geneName, ensemblGeneIdsSet);
                        }
                    }
                }

                Collection<EntrezGene> entrezGeneIds = genomicContext.getGene().getEntrezGeneIds();
                for (EntrezGene entrezGene : entrezGeneIds) {

                    String entrezId = entrezGene.getEntrezGeneId();
                    if (entrezId != null) {
                        if (geneToEntrezIdMap.containsKey(geneName)) {
                            geneToEntrezIdMap.get(geneName).add(entrezId);
                        }

                        else {
                            Set<String> entrezGeneIdsSet = new HashSet<>();
                            entrezGeneIdsSet.add(entrezId);
                            geneToEntrezIdMap.put(geneName, entrezGeneIdsSet);
                        }
                    }
                }
            }
        }

        // Store genes, source is required so we know what table to add them to
        if (geneToEnsemblIdMap.size() > 0) {
            storeGenes(geneToEnsemblIdMap, "Ensembl");
        }

        if (geneToEntrezIdMap.size() > 0) {
            storeGenes(geneToEntrezIdMap, "Entrez");
        }
    }

    /**
     * Create/update genes with latest mapping information
     *
     * @param geneToExternalIdMap map of a gene name and all external database IDs from current mapping run
     * @param source              the source of mapping, either Ensembl or Entrez
     */
    private void storeGenes(Map<String, Set<String>> geneToExternalIdMap, String source) {

        for (String geneName : geneToExternalIdMap.keySet()) {

            Set<String> externalIds = geneToExternalIdMap.get(geneName);

            // Find any existing database genes that match the gene name
            List<Gene> existingGenesInDatabase = geneRepository.findByGeneNameIgnoreCase(geneName);

            // If gene is not found in database then create one
            if (existingGenesInDatabase.size() == 0) {
                createGene(geneName, externalIds, source);
            }

            // Update gene
            else {

                for (Gene existingGeneInDatabase : existingGenesInDatabase) {
                    if (source.equalsIgnoreCase("Ensembl")) {

                        // Get a list of current Ensembl IDs linked to existing gene
                        Collection<EnsemblGene> oldEnsemblIdsLinkedToGene = existingGeneInDatabase.getEnsemblGeneIds();

                        Collection<EnsemblGene> newEnsemblGenes = new ArrayList<>();
                        for (String id : externalIds) {
                            EnsemblGene ensemblGene = createOrRetrieveEnsemblExternalId(id, geneName);
                            newEnsemblGenes.add(ensemblGene);
                        }

                        // Set latest IDs from mapping run
                        existingGeneInDatabase.setEnsemblGeneIds(newEnsemblGenes);

                        // Clean-up any Ensembl IDs that may now be left without a gene linked
                        for (EnsemblGene oldEnsemblIdLinkedToGene : oldEnsemblIdsLinkedToGene) {
                            cleanUpEnsemblGenes(oldEnsemblIdLinkedToGene);
                        }

                    }

                    if (source.equalsIgnoreCase("Entrez")) {

                        // Get a list of of current Entrez IDs linked to existing gene
                        Collection<EntrezGene> oldEntrezGenesLinkedToGene = existingGeneInDatabase.getEntrezGeneIds();

                        Collection<EntrezGene> newEntrezGenes = new ArrayList<>();
                        for (String id : externalIds) {
                            EntrezGene entrezGene = createOrRetrieveEntrezExternalId(id, geneName);
                            newEntrezGenes.add(entrezGene);
                        }

                        // Set latest IDs from mapping run
                        existingGeneInDatabase.setEntrezGeneIds(newEntrezGenes);

                        // Clean-up any Entrez IDs that may now be left without a gene linked
                        for (EntrezGene oldEntrezGeneLinkedToGene : oldEntrezGenesLinkedToGene) {
                            cleanUpEntrezGenes(oldEntrezGeneLinkedToGene);
                        }

                    }

                    // Save changes
                    geneRepository.save(existingGeneInDatabase);
                }
            }

        }
    }

    /**
     * Saves genomic context information to database
     *
     * @param snpToGenomicContextMap map of rs_id and all genomic context details returned from current mapping run
     */
    private void storeSnpGenomicContext(Map<String, Set<GenomicContext>> snpToGenomicContextMap) {

        // Go through each rs_id and its associated genomic contexts returned from the mapping pipeline
        for (String snpRsId : snpToGenomicContextMap.keySet()) {

            Set<GenomicContext> genomicContextsFromMapping = snpToGenomicContextMap.get(snpRsId);

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

                    for (GenomicContext genomicContextFromMapping : genomicContextsFromMapping) {

                        // Gene should already have been created
                        String geneName = genomicContextFromMapping.getGene().getGeneName().trim();

                        if (!geneName.equalsIgnoreCase("undefined")) {

                            // Create new genomic context
                            Boolean isIntergenic = genomicContextFromMapping.getIsIntergenic();
                            Boolean isUpstream = genomicContextFromMapping.getIsUpstream();
                            Boolean isDownstream = genomicContextFromMapping.getIsDownstream();
                            Long distance = genomicContextFromMapping.getDistance();
                            String source = genomicContextFromMapping.getSource();
                            String mappingMethod = genomicContextFromMapping.getMappingMethod();
                            Boolean isClosestGene = genomicContextFromMapping.getIsClosestGene();

                            // Location details
                            String chromosomeName = genomicContextFromMapping.getLocation().getChromosomeName();
                            String chromosomePosition = genomicContextFromMapping.getLocation().getChromosomePosition();
                            String regionName = genomicContextFromMapping.getLocation().getRegion().getName();

                            // Check if location already exists
                            Location location =
                                    locationRepository.findByChromosomeNameAndChromosomePositionAndRegionName(
                                            chromosomeName,
                                            chromosomePosition,
                                            regionName);

                            if (location != null){
                                location = createLocation(chromosomeName,
                                                          chromosomePosition,
                                                          regionName);
                            }

                            GenomicContext genomicContext = createGenomicContext(isIntergenic,
                                                                                 isUpstream,
                                                                                 isDownstream,
                                                                                 distance,
                                                                                 source,
                                                                                 mappingMethod,
                                                                                 geneName,
                                                                                 snpInDatabase,
                                                                                 isClosestGene, location);

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

    /**
     * Method to create a gene
     *
     * @param geneName    gene symbol or name
     * @param externalIds external gene IDs
     * @param source      the source of mapping, either Ensembl or Entrez
     */
    private void createGene(String geneName, Set<String> externalIds, String source) {
        // Create new gene
        Gene newGene = new Gene();
        newGene.setGeneName(geneName);

        if (source.equalsIgnoreCase("Ensembl")) {
            // Set Ensembl Ids for new gene
            Collection<EnsemblGene> ensemblGeneIds = new ArrayList<>();
            for (String id : externalIds) {
                EnsemblGene ensemblGene = createOrRetrieveEnsemblExternalId(id, geneName);
                ensemblGeneIds.add(ensemblGene);
            }
            newGene.setEnsemblGeneIds(ensemblGeneIds);
        }

        if (source.equalsIgnoreCase("Entrez")) {
            // Set Entrez Ids for new gene
            Collection<EntrezGene> entrezGeneIds = new ArrayList<>();
            for (String id : externalIds) {
                EntrezGene entrezGene = createOrRetrieveEntrezExternalId(id, geneName);
                entrezGeneIds.add(entrezGene);
            }
            newGene.setEntrezGeneIds(entrezGeneIds);
        }

        // Save gene
        geneRepository.save(newGene);
    }

    /**
     * Method to create an Ensembl gene, this database table holds ensembl gene IDs
     *
     * @param id       Ensembl gene ID
     * @param geneName Gene name allows method to check if this id is actually already linked to another gene
     */
    private EnsemblGene createOrRetrieveEnsemblExternalId(String id, String geneName) {
        EnsemblGene ensemblGene = ensemblGeneRepository.findByEnsemblGeneId(id);

        // Create new entry in ENSEMBL_GENE table for this ID
        if (ensemblGene == null) {
            ensemblGene = new EnsemblGene();
            ensemblGene.setEnsemblGeneId(id);
            ensemblGeneRepository.save(ensemblGene);
        }

        // Check this ID is not linked to a gene with a different name,
        // this case should be extremely rare
        else {
            if (!geneName.equals(ensemblGene.getGene().getGeneName())) {
                throw new RuntimeException(
                        "Ensembl ID: " + id + ", is already used in database by gene: " +
                                ensemblGene.getGene().getGeneName() + ". Cannot link to " + geneName);
            }

        }

        return ensemblGene;
    }

    /**
     * Method to create an Entrez gene, this database table holds entrez gene IDs
     *
     * @param id       Entrez gene ID
     * @param geneName Gene name allows method to check if this id is actually already linked to another gene
     */
    private EntrezGene createOrRetrieveEntrezExternalId(String id, String geneName) {
        EntrezGene entrezGene = entrezGeneRepository.findByEntrezGeneId(id);

        // Create new entry in ENSEMBL_GENE table for this ID
        if (entrezGene == null) {
            entrezGene = new EntrezGene();
            entrezGene.setEntrezGeneId(id);
            entrezGeneRepository.save(entrezGene);
        }

        // Check this ID is not linked to a gene with a different name,
        // this case should be extremely rare
        else {
            if (!geneName.equals(entrezGene.getGene().getGeneName())) {
                throw new RuntimeException(
                        "Entrez ID: " + id + ", is already used in database by gene: " +
                                entrezGene.getGene().getGeneName() + ". Cannot link to " + geneName);
            }
        }

        return entrezGene;
    }

    /**
     * Method to clean-up an Ensembl gene ID in database that has no linked gene
     *
     * @param ensemblGene Ensembl gene object to delete
     */

    private void cleanUpEnsemblGenes(EnsemblGene ensemblGene) {

        // Find any genes with this Ensembl ID
        List<Gene> genesWithEnsemblId =
                geneRepository.findByEnsemblGeneIdsEnsemblGeneId(ensemblGene.getEnsemblGeneId());

        // If this ID is not linked to a gene then delete it
        if (genesWithEnsemblId.size() == 0) {
            ensemblGeneRepository.delete(ensemblGene);
        }
    }

    /**
     * Method to clean-up an Entrez gene ID in database that has no linked gene
     *
     * @param entrezGene Entrez gene object to delete
     */
    private void cleanUpEntrezGenes(EntrezGene entrezGene) {

        // Find any genes with this Entrez ID
        List<Gene> geneWithEntrezIds =
                geneRepository.findByEntrezGeneIdsEntrezGeneId(entrezGene.getEntrezGeneId());

        // If this ID is not linked to a gene then delete it
        if (geneWithEntrezIds.size() == 0) {
            entrezGeneRepository.delete(entrezGene);
        }
    }

    /**
     * Method to create genomic context
     *
     * @param isIntergenic
     * @param isUpstream
     * @param isDownstream
     * @param distance
     * @param source
     * @param mappingMethod
     * @param geneName
     * @param snpIdInDatabase
     * @param isClosestGene
     * @param location
     */
    private GenomicContext createGenomicContext(Boolean isIntergenic,
                                                Boolean isUpstream,
                                                Boolean isDownstream,
                                                Long distance,
                                                String source,
                                                String mappingMethod,
                                                String geneName,
                                                SingleNucleotidePolymorphism snpIdInDatabase,
                                                Boolean isClosestGene, Location location) {

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
        genomicContext.setLocation(location);

        // Save genomic context
        genomicContextRepository.save(genomicContext);

        return genomicContext;
    }

    private Location createLocation(String chromosomeName,
                                    String chromosomePosition,
                                    String regionName) {


        Region region = null;
        region = regionRepository.findByName(regionName);

        // If the region doesn't exist, save it
        if (region == null) {
            Region newRegion = new Region();
            newRegion.setName(regionName);
            region = regionRepository.save(newRegion);
        }

        Location newLocation = new Location();
        newLocation.setChromosomeName(chromosomeName);
        newLocation.setChromosomePosition(chromosomePosition);
        newLocation.setRegion(region);

        // Save location
        locationRepository.save(newLocation);
        return newLocation;
    }
}
