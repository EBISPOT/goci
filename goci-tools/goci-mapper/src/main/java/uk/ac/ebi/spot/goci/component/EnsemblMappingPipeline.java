package uk.ac.ebi.spot.goci.component;

import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.EnsemblRestIOException;
import uk.ac.ebi.spot.goci.model.EnsemblGene;
import uk.ac.ebi.spot.goci.model.EnsemblMappingResult;
import uk.ac.ebi.spot.goci.model.EntrezGene;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.RestResponseResult;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.service.EnsemblRestService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Laurent on 15/07/15.
 *
 * @author Laurent
 *         <p>
 *         Class containing the Ensembl mapping and checking pipeline The structure of this pipeline is similar to the
 *         javascript pipeline developped in the scrip goci-snp-association-mapping.js
 *         (goci/goci-interfaces/goci-curation/src/main/resources/static/js/goci-snp-association-mapping.js)
 */
@Service
public class EnsemblMappingPipeline {

    // Reading these from application.properties
    @Value("${mapping.ensembl_source}")
    private String ensemblSource;

    @Value("${mapping.ncbi_source}")
    private String ncbiSource;

    @Value("${mapping.ncbi_db_type}")
    private String ncbiDbType;

    @Value("${mapping.ncbi_logic_name}")
    private String ncbiLogicName;

    @Value("${mapping.method}")
    private String mappingMethod;

    @Value("${mapping.genomic_distance}")
    private int genomicDistance; // 100kb

    private final List<String> reportedGenesToIgnore = Arrays.asList("NR", "intergenic", "genic");

    private EnsemblRestService ensemblRestService;

    private EnsemblMappingResult ensemblMappingResult;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public EnsemblMappingPipeline(EnsemblRestService ensemblRestService) {
        this.ensemblRestService = ensemblRestService;
    }

    // Run the pipeline for a given SNP
    public synchronized EnsemblMappingResult run_pipeline(String rsId, Collection<String> reportedGenes)
            throws EnsemblRestIOException {

        // Create our result object
        setEnsemblMappingResult(new EnsemblMappingResult());
        getEnsemblMappingResult().setRsId(rsId);

        // Variation call
        RestResponseResult variationDataApiResult = ensemblRestService.getRestCall("variation", rsId, "");
        String restApiError = variationDataApiResult.getError();

        // Check for any errors
        if (restApiError != null && !restApiError.isEmpty()) {
            getEnsemblMappingResult().addPipelineErrors(restApiError);
        }

        if (variationDataApiResult.getRestResult() != null) {
            JSONObject variationResult = variationDataApiResult.getRestResult().getObject();

            if (variationResult.has("error")) {
                checkError(variationResult, "variation", "Variant " + rsId + " is not found in Ensembl");
            }
            else if (variationResult.length() > 0) {

                // Merged SNP
                String currentRsId = variationResult.getString("name");
                getEnsemblMappingResult().setMerged((currentRsId.equals(rsId)) ? 0 : 1);
                if (getEnsemblMappingResult().getMerged() == 1) {
                    getEnsemblMappingResult().setCurrentSnpId(currentRsId);
                }

                // Mapping errors
                if (variationResult.has("failed")) {
                    getEnsemblMappingResult().addPipelineErrors(variationResult.getString("failed"));
                }

                // Mapping and genomic context calls
                JSONArray mappings = variationResult.getJSONArray("mappings");
                Collection<Location> locations = getMappings(mappings);
                getEnsemblMappingResult().setLocations(locations);

                // Add genomic context
                if (locations.size() > 0) {

                    // Functional class (most severe consequence).
                    // This implies there is at least one variant location.
                    if (variationResult.has("most_severe_consequence")) {
                        getEnsemblMappingResult().setFunctionalClass(variationResult.getString("most_severe_consequence"));
                    }

                    // Genomic context (loop over the "locations" object)
                    for (Location snp_location : locations) {
                        getAllGenomicContexts(snp_location);
                    }
                }
            }
        }
        else {
            getLog().error("Variation call for SNP " + rsId + " returned no result");
        }

        // Reported genes checks
        if (reportedGenes.size() > 0) {
            checkReportedGenes(reportedGenes, getEnsemblMappingResult().getLocations());
        }

        return getEnsemblMappingResult();
    }


    /**
     * Check that the reported gene symbols exist and that they are located in the same chromosome as the variant
     *
     * @param reportedGenes
     * @param locations
     */

    private void checkReportedGenes(Collection<String> reportedGenes, Collection<Location> locations)
            throws EnsemblRestIOException {

        for (String reportedGene : reportedGenes) {

            reportedGene = reportedGene.replaceAll(" ", ""); // Remove extra spaces

            // Skip the iteration if the gene name is in the "gene-to-ignore" list
            if (!getReportedGenesToIgnore().contains(reportedGene)) {

                String webservice = "lookup_symbol";
                RestResponseResult reportedGeneApiResult = ensemblRestService.getRestCall(webservice, reportedGene, "");

                // Check for errors
                if (reportedGeneApiResult.getError() != null && !reportedGeneApiResult.getError().isEmpty()) {
                    getEnsemblMappingResult().addPipelineErrors(reportedGeneApiResult.getError());
                }

                if (reportedGeneApiResult.getRestResult() != null) {
                    JSONObject reported_gene_result = reportedGeneApiResult.getRestResult().getObject();

                    // Check if the gene is in the same chromosome as the variant
                    if (reported_gene_result.has("seq_region_name")) {
                        if (locations.size() > 0) {
                            String gene_chromosome = reported_gene_result.getString("seq_region_name");
                            int same_chromosome = 0;
                            for (Location location : locations) {
                                String snp_chromosome = location.getChromosomeName();
                                if (gene_chromosome.equals(snp_chromosome)) {
                                    same_chromosome = 1;
                                    break;
                                }
                            }
                            if (same_chromosome == 0) {
                                getEnsemblMappingResult().addPipelineErrors(
                                        "Reported gene " + reportedGene + " is on a different chromosome (chr" +
                                                gene_chromosome + ")");
                            }
                        }
                        else {
                            getEnsemblMappingResult().addPipelineErrors("Can't compare the " + reportedGene +
                                                                                " location in Ensembl: no mapping available for the variant");
                        }
                    }
                    // No gene location found
                    else {
                        getEnsemblMappingResult().addPipelineErrors(
                                "Can't find a location in Ensembl for the reported gene " + reportedGene);
                    }

                }
                else {
                    getLog().error("Reported gene check for " + reportedGene + " returned no result");
                }
            }
        }
    }


    /**
     * Get the mappings data ( chromosome, position and cytogenetic band). Store the location information in the class
     * variable "locations" (list of "Location" classes)
     *
     * @param mappings A JSONArray object containing the list the variant locations
     */
    private Collection<Location> getMappings(JSONArray mappings) throws EnsemblRestIOException {

        Collection<Location> locations = new ArrayList<>();

        for (int i = 0; i < mappings.length(); ++i) {
            JSONObject mapping = mappings.getJSONObject(i);
            if (!mapping.has("seq_region_name")) {
                continue;
            }
            String chromosome = mapping.getString("seq_region_name");
            String position = String.valueOf(mapping.getInt("start"));

            Region cytogeneticBand = getRegion(chromosome, position);

            Location location = new Location(chromosome, position, cytogeneticBand);
            locations.add(location);
        }
        return locations;
    }


    /**
     * Get the cytogenetic band from a given location
     *
     * @param chromosome the chromosome name
     * @param position   the position of the variant
     * @return Region object only containing a region name
     */
    private Region getRegion(String chromosome, String position) throws EnsemblRestIOException {

        String band = null; // Default value
        String rest_opt = "feature=band";

        // REST Call
        JSONArray cytogenetic_band_result = getOverlapRegionCalls(chromosome, position, position, rest_opt);

        if (cytogenetic_band_result.length() != 0 && !cytogenetic_band_result.getJSONObject(0).has("overlap_error")) {
            String cytogenetic_band = cytogenetic_band_result.getJSONObject(0).getString("id");

            Matcher matcher1 = Pattern.compile("^[0-9]+|[XY]$").matcher(chromosome); // Chromosomes
            Matcher matcher2 = Pattern.compile("^MT$").matcher(chromosome);          // Mitochondria
            if (matcher1.matches() || matcher2.matches()) {
                band = chromosome + cytogenetic_band;
            }
        }

        return new Region(band);
    }


    /**
     * Run the genomic context pipeline for both sources (Ensembl and NCBI)
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     */
    private void getAllGenomicContexts(Location snp_location) throws EnsemblRestIOException {

        int chr_start = 1;
        int chr_end = getChromosomeEnd(snp_location.getChromosomeName());

        getGenomicContext(snp_location, chr_start, chr_end, getEnsemblSource());
        getGenomicContext(snp_location, chr_start, chr_end, getNcbiSource());
    }


    /**
     * Get the genomic context in 3 calls: overlap, upstream and downstream genes
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     * @param chr_start    5' start position of the chromosome
     * @param chr_end      3' end position of the chromosome
     * @param source       the source of the data (Ensembl or NCBI)
     */
    private void getGenomicContext(Location snp_location, int chr_start, int chr_end, String source)
            throws EnsemblRestIOException {
        // By default the db_type is 'core' (i.e. Ensembl)
        String rest_opt = "feature=gene";
        if (source.equals(getNcbiSource())) {
            rest_opt += "&logic_name=" + getNcbiLogicName();
            rest_opt += "&db_type=" + getNcbiDbType();
        }
        // Overlapping genes
        getLog().debug("Getting overlapping genes from " + source + " for " + getEnsemblMappingResult().getRsId());
        getOverlappingGenes(snp_location, source, rest_opt);

        // Upstream genes
        getLog().debug("Getting upstream genes from " + source + " for " + getEnsemblMappingResult().getRsId());
        getUpstreamGenes(snp_location, source, chr_start, rest_opt);

        // Downstream genes
        getLog().debug("Getting downstream genes from " + source + " for " + getEnsemblMappingResult().getRsId());
        getDownstreamGenes(snp_location, source, chr_end, rest_opt);
    }


    /**
     * Get the list of overlapping genes
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     * @param source       the source of the data (Ensembl or NCBI)
     * @param rest_opt     the extra parameters to add at the end of the REST call url
     */
    private void getOverlappingGenes(Location snp_location, String source, String rest_opt)
            throws EnsemblRestIOException {

        String chromosome = snp_location.getChromosomeName();
        String position = snp_location.getChromosomePosition();

        // Check if there are overlap genes
        JSONArray overlap_gene_result = getOverlapRegionCalls(chromosome, position, position, rest_opt);

        if (overlap_gene_result.length() != 0 && !overlap_gene_result.getJSONObject(0).has("overlap_error")) {
            for (int i = 0; i < overlap_gene_result.length(); ++i) {
                JSONObject gene_json_object = overlap_gene_result.getJSONObject(i);

                String geneName = gene_json_object.getString("external_name");

                if (source.equals(getNcbiSource())) {
                    getEnsemblMappingResult().addNcbiOverlappingGene(geneName);
                }
                else {
                    getEnsemblMappingResult().addEnsemblOverlappingGene(geneName);
                }
            }
            addGenomicContext(overlap_gene_result, snp_location, source, "overlap");
        }
    }


    /**
     * Get the list of upstream genes
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     * @param source       the source of the data (Ensembl or NCBI)
     * @param chr_start    5' start position of the chromosome
     * @param rest_opt     the extra parameters to add at the end of the REST call url
     */
    private void getUpstreamGenes(Location snp_location, String source, int chr_start, String rest_opt)
            throws EnsemblRestIOException {
        String type = "upstream";

        String chromosome = snp_location.getChromosomeName();
        String position = snp_location.getChromosomePosition();

        int position_up = Integer.parseInt(snp_location.getChromosomePosition()) - getGenomicDistance();
        if (position_up < 0) {
            position_up = chr_start;
        }
        String pos_up = String.valueOf(position_up);

        // Check if there are overlap genes
        JSONArray overlap_gene_result = getOverlapRegionCalls(chromosome, pos_up, position, rest_opt);

        if ((overlap_gene_result.length() != 0 && !overlap_gene_result.getJSONObject(0).has("overlap_error")) ||
                overlap_gene_result.length() == 0) {
            boolean closest_found = addGenomicContext(overlap_gene_result, snp_location, source, type);
            if (!closest_found) {
                if (position_up > chr_start) {
                    JSONArray closest_gene = getNearestGene(chromosome, position, pos_up, 1, rest_opt, type, source);
                    if (closest_gene.length() > 0) {
                        addGenomicContext(closest_gene, snp_location, source, type);
                    }
                }
            }
        }
    }


    /**
     * Get the list of downstream genes
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     * @param source       the source of the data (Ensembl or NCBI)
     * @param chr_end      3' end position of the chromosome
     * @param rest_opt     the extra parameters to add at the end of the REST call url
     */
    private void getDownstreamGenes(Location snp_location, String source, int chr_end, String rest_opt)
            throws EnsemblRestIOException {
        String type = "downstream";

        String chromosome = snp_location.getChromosomeName();
        String position = snp_location.getChromosomePosition();

        int position_down = Integer.parseInt(snp_location.getChromosomePosition()) + getGenomicDistance();
        // Check the downstream position to avoid having a position over the 3' end of the chromosome
        if (chr_end != 0) {
            if (position_down > chr_end) {
                position_down = chr_end;
            }
            String pos_down = String.valueOf(position_down);

            // Check if there are overlap genes
            JSONArray overlap_gene_result = getOverlapRegionCalls(chromosome, position, pos_down, rest_opt);

            if ((overlap_gene_result.length() != 0 && !overlap_gene_result.getJSONObject(0).has("overlap_error")) ||
                    overlap_gene_result.length() == 0) {
                boolean closest_found = addGenomicContext(overlap_gene_result, snp_location, source, type);
                if (!closest_found) {
                    if (position_down != chr_end) {
                        JSONArray closest_gene =
                                getNearestGene(chromosome, position, pos_down, chr_end, rest_opt, type, source);
                        if (closest_gene.length() > 0) {
                            addGenomicContext(closest_gene, snp_location, source, type);
                        }
                    }
                }
            }
        }
    }

    /**
     * Create GenomicContext objects from the JSONObjects and add them to the class variable "genomic_contexts" (list of
     * "GenomicContext" classes)
     *
     * @param json_gene_list the list of overlapping genes in JSONObject format
     * @param snp_location   an instance of the Location class (chromosome name and position)
     * @param source         the source of the data (Ensembl or NCBI)
     * @param type           the type of genomic context (i.e. overlap, upstream, downstream)
     * @return boolean to indicate whether a closest gene has been found or not (only relevant for upstream and
     * downstream gene)
     */
    private boolean addGenomicContext(JSONArray json_gene_list, Location snp_location, String source, String type) {
        String closest_gene = "";
        int closest_distance = 0;
        boolean intergenic = (type.equals("overlap")) ? false : true;
        boolean upstream = (type.equals("upstream")) ? true : false;
        boolean downstream = (type.equals("downstream")) ? true : false;

        String position = snp_location.getChromosomePosition();

        SingleNucleotidePolymorphism snp_tmp =
                new SingleNucleotidePolymorphism();
        snp_tmp.setRsId(getEnsemblMappingResult().getRsId());

        // Get closest gene
        if (intergenic) {
            int pos = Integer.parseInt(position);

            for (int i = 0; i < json_gene_list.length(); ++i) {
                JSONObject json_gene = json_gene_list.getJSONObject(i);
                String gene_id = json_gene.getString("id");
                String gene_name = json_gene.getString("external_name");

                if (source.equals(getNcbiSource())) {
                    if ((gene_name != null && getEnsemblMappingResult().getNcbiOverlappingGene().contains(gene_name)) ||
                            gene_name ==
                                    null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                        continue;
                    }
                }
                else {
                    if ((gene_name != null && getEnsemblMappingResult().getEnsemblOverlappingGene().contains(gene_name)) ||
                            gene_name ==
                                    null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                        continue;
                    }
                }

                int distance = 0;
                if (type.equals("upstream")) {
                    distance = pos - json_gene.getInt("end");
                }
                else if (type.equals("downstream")) {
                    distance = json_gene.getInt("start") - pos;
                }

                if ((distance < closest_distance && distance > 0) || closest_distance == 0) {
                    closest_gene = gene_id;
                    closest_distance = distance;
                }
            }
        }

        for (int i = 0; i < json_gene_list.length(); ++i) {
            JSONObject json_gene = json_gene_list.getJSONObject(i);
            String gene_id = json_gene.getString("id");
            String gene_name = json_gene.getString("external_name");
            String ncbi_id = (source.equals("NCBI")) ? gene_id : null;
            String ensembl_id = (source.equals("Ensembl")) ? gene_id : null;
            int distance = 0;

            if (intergenic) {
                if (source.equals(getNcbiSource())) {
                    if ((gene_name != null && getEnsemblMappingResult().getNcbiOverlappingGene().contains(gene_name)) ||
                            gene_name ==
                                    null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                        continue;
                    }
                }
                else {
                    if ((gene_name != null && getEnsemblMappingResult().getEnsemblOverlappingGene().contains(gene_name)) ||
                            gene_name ==
                                    null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                        continue;
                    }
                }

                int pos = Integer.parseInt(position);
                if (type.equals("upstream")) {
                    distance = pos - json_gene.getInt("end");
                }
                else if (type.equals("downstream")) {
                    distance = json_gene.getInt("start") - pos;
                }
            }
            Long dist = (long) distance;

            EntrezGene entrezGene = new EntrezGene();
            entrezGene.setEntrezGeneId(ncbi_id);
            Collection<EntrezGene> entrezGenes = new ArrayList<>();
            entrezGenes.add(entrezGene);

            EnsemblGene ensemblGene = new EnsemblGene();
            ensemblGene.setEnsemblGeneId(ensembl_id);
            Collection<EnsemblGene> ensemblGenes = new ArrayList<>();
            ensemblGenes.add(ensemblGene);

            Gene gene_object = new Gene(gene_name, entrezGenes, ensemblGenes);

            // Check if the gene corresponds to the closest gene
            boolean is_closest_gene = (closest_gene.equals(gene_id) && closest_gene != "") ? true : false;

            GenomicContext gc = new GenomicContext(intergenic,
                                                   upstream,
                                                   downstream,
                                                   dist,
                                                   snp_tmp,
                                                   gene_object,
                                                   snp_location,
                                                   source,
                                                   getMappingMethod(),
                                                   is_closest_gene);

            getEnsemblMappingResult().addGenomicContext(gc);
        }
        return (closest_gene != "") ? true : false;
    }


    /**
     * Recursive method to get the closest upstream or downstream gene over the 100kb range, jumping 100kb by 100kb
     * until a gene is found or the boundary of the chromosome is reached.
     *
     * @param chromosome   the chromosome name
     * @param snp_position the position of the variant
     * @param position     the start position for the search (at least 100kb upstream or downstream from the variant)
     * @param boundary     the chromosome boundary (upstream: beginning of the chromosome (position 1), downstream: end
     *                     of the chromosome)
     * @param rest_opt     the extra parameters to add at the end of the REST call url (inherited from other methods)
     * @param type         the type of genomic context (i.e. overlap, upstream, downstream)
     * @param source
     * @return A JSONArray object containing a single JSONObject corresponding to the closest gene (upstream or
     * downstream) over the 100kb range
     */
    private JSONArray getNearestGene(String chromosome,
                                     String snp_position,
                                     String position,
                                     int boundary,
                                     String rest_opt,
                                     String type, String source) throws EnsemblRestIOException {

        int position1 = Integer.parseInt(position);
        int position2 = Integer.parseInt(position);
        int snp_pos = Integer.parseInt(snp_position);

        int new_pos = position1;

        JSONArray closest_gene = new JSONArray();
        int closest_distance = 0;

        if (type.equals("upstream")) {
            position1 = position2 - getGenomicDistance();
            position1 = (position1 < 0) ? boundary : position1;
            new_pos = position1;
        }
        else {
            if (type.equals("downstream")) {
                position2 = position1 + getGenomicDistance();
                position2 = (position2 > boundary) ? boundary : position2;
                new_pos = position2;
            }
        }

        String pos1 = String.valueOf(position1);
        String pos2 = String.valueOf(position2);
        String new_pos_string = String.valueOf(new_pos);

        JSONArray json_gene_list = this.getOverlapRegionCalls(chromosome, pos1, pos2, rest_opt);

        boolean gene_error = false;

        if (json_gene_list.length() > 0) {
            if (json_gene_list.getJSONObject(0).has("overlap_error")) {
                gene_error = true;
            }
            else {
                for (int i = 0; i < json_gene_list.length(); ++i) {
                    JSONObject json_gene = json_gene_list.getJSONObject(i);
                    String gene_id = json_gene.getString("id");
                    String gene_name = json_gene.getString("external_name");

                    if (source.equals(getNcbiSource())) {
                        if ((gene_name != null && getEnsemblMappingResult().getNcbiOverlappingGene().contains(gene_name)) ||
                                gene_name ==
                                        null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                            continue;
                        }
                    }
                    else {
                        if ((gene_name != null && getEnsemblMappingResult().getEnsemblOverlappingGene().contains(gene_name)) ||
                                gene_name ==
                                        null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                            continue;
                        }
                    }

                    int distance = 0;
                    if (type.equals("upstream")) {
                        distance = snp_pos - json_gene.getInt("end");
                    }
                    else if (type.equals("downstream")) {
                        distance = json_gene.getInt("start") - snp_pos;
                    }

                    if ((distance < closest_distance && distance > 0) || closest_distance == 0) {
                        closest_gene = new JSONArray("[" + json_gene.toString() + "]");
                        closest_distance = distance;
                    }
                }
                if (closest_gene.length() == 0 && new_pos != boundary) {
                    // Recursive code to find the nearest upstream or downstream gene
                    closest_gene =
                            this.getNearestGene(chromosome, snp_position, new_pos_string, boundary, rest_opt, type,
                                                source);
                }
            }
        }
        else {
            if (new_pos != boundary) {
                // Recursive code to find the nearest upstream or downstream gene
                closest_gene = this.getNearestGene(chromosome, snp_position, new_pos_string, boundary, rest_opt, type,
                                                   source);
            }
        }
        return closest_gene;
    }


    /**
     * Ensembl REST API call for the overlap region endpoint
     *
     * @param chromosome the chromosome name
     * @param position1  the 5' position of the region
     * @param position2  the 3' position of the region
     * @param rest_opt   the extra parameters to add at the end of the REST call url (inherited from other methods)
     * @return A JSONArray object containing a list of JSONObjects corresponding to the genes overlapping the region
     */
    private JSONArray getOverlapRegionCalls(String chromosome, String position1, String position2, String rest_opt)
            throws EnsemblRestIOException {

        String data = chromosome + ":" + position1 + "-" + position2;
        RestResponseResult restResponseResult = ensemblRestService.getRestCall("overlap_region", data, rest_opt);
        JsonNode result = restResponseResult.getRestResult();
        JSONArray overlap_result = new JSONArray();

        if (result.isArray()) {
            overlap_result = result.getArray();
        }

        else {
            // Errors
            getEnsemblMappingResult().addPipelineErrors(restResponseResult.getError());
            overlap_result = new JSONArray("[{\"overlap_error\":\"1\"}]");
        }

        return overlap_result;
    }


    /**
     * Get the end position of a given chromosome, using an Ensembl REST API call
     *
     * @param chromosome the chromosome name
     * @return the position of the end of the chromosome
     */
    private int getChromosomeEnd(String chromosome) throws EnsemblRestIOException {
        int chr_end = 0;
        String webservice = "info_assembly";
        RestResponseResult restResponseResult = ensemblRestService.getRestCall(webservice, chromosome, "");
        JSONObject info_result = restResponseResult.getRestResult().getObject();

        if (info_result.length() > 0) {
            if (info_result.has("length")) {
                chr_end = info_result.getInt("length");
            }
        }

        return chr_end;
    }


    /**
     * Check the type of error returned by the REST web service JSON output
     *
     * @param result          The JSONObject result
     * @param webservice      The name of the REST web service
     * @param default_message The default error message
     */
    private void checkError(JSONObject result, String webservice, String default_message)
            throws EnsemblRestIOException {

        if (result.getString("error").contains("page not found")) {
            getEnsemblMappingResult().addPipelineErrors("Web service '" + webservice + "' not found or not working.");
            throw new EnsemblRestIOException("Web service " + webservice + " not found or not working.");
        }
        else {
            if (default_message.equals("")) {
                getEnsemblMappingResult().addPipelineErrors(result.getString("error"));
            }
            else {
                getEnsemblMappingResult().addPipelineErrors(default_message);
            }
        }
    }


    // Getters and setters
    public String getEnsemblSource() {
        return ensemblSource;
    }

    public void setEnsemblSource(String ensemblSource) {
        this.ensemblSource = ensemblSource;
    }

    public String getNcbiSource() {
        return ncbiSource;
    }

    public void setNcbiSource(String ncbiSource) {
        this.ncbiSource = ncbiSource;
    }

    public String getNcbiDbType() {
        return ncbiDbType;
    }

    public void setNcbiDbType(String ncbiDbType) {
        this.ncbiDbType = ncbiDbType;
    }

    public String getNcbiLogicName() {
        return ncbiLogicName;
    }

    public void setNcbiLogicName(String ncbiLogicName) {
        this.ncbiLogicName = ncbiLogicName;
    }

    public String getMappingMethod() {
        return mappingMethod;
    }

    public void setMappingMethod(String mappingMethod) {
        this.mappingMethod = mappingMethod;
    }

    public int getGenomicDistance() {
        return genomicDistance;
    }

    public void setGenomicDistance(int genomicDistance) {
        this.genomicDistance = genomicDistance;
    }

    public List<String> getReportedGenesToIgnore() {
        return reportedGenesToIgnore;
    }

    public EnsemblMappingResult getEnsemblMappingResult() {
        return ensemblMappingResult;
    }

    public void setEnsemblMappingResult(EnsemblMappingResult ensemblMappingResult) {
        this.ensemblMappingResult = ensemblMappingResult;
    }
}
