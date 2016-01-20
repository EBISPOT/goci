package uk.ac.ebi.spot.goci.component;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.EnsemblGene;
import uk.ac.ebi.spot.goci.model.EntrezGene;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.service.EnsemblRestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
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
public class EnsemblMappingPipeline {

    private String rsId;
    private String functionalClass = "";
    private int merged;
    private Collection<String> reported_genes = new ArrayList<>();
    private Collection<Location> locations = new ArrayList<>();
    private Collection<GenomicContext> genomic_contexts = new ArrayList<>();
    private ArrayList<String> pipeline_errors = new ArrayList<>();

    // Internal variables populated within the class
    private ArrayList<String> overlapping_genes = new ArrayList<>();

    // Fixed variables
    private final String ensembl_source = "Ensembl";
    private final String ncbi_source = "NCBI";
    private final String ncbi_db_type = "otherfeatures";
    private final String ncbi_logic_name = "refseq_import";
    private final String mapping_method = "Ensembl pipeline";
    private final int genomic_distance = 100000; // 100kb
    private final List<String> reported_genes_to_ignore = Arrays.asList("NR", "intergenic");

    // Request rate variables
    private int requestCount = 0;
    private long limitStartTime = System.currentTimeMillis();

    private final Logger log = LoggerFactory.getLogger(getClass());
    protected Logger getLog() {
        return log;
    }

    // JPA no-args constructor
    public EnsemblMappingPipeline() {
//        this.setEndpoints();
    }

    public EnsemblMappingPipeline(String rsId, Collection<String> reported_genes) {
        this.rsId = rsId;
        this.reported_genes = reported_genes;
//        this.setEndpoints();
    }

    public EnsemblMappingPipeline(String rsId,
                                  Collection<String> reported_genes,
                                  int requestCount,
                                  long limitStartTime) {
        this.rsId = rsId;
        this.reported_genes = reported_genes;
        this.requestCount = requestCount;
        this.limitStartTime = limitStartTime;
//        this.setEndpoints();
    }



    /**
     * Getter for the variant functional class
     *
     * @return The variant functional class.
     */
    public String getFunctionalClass() {
        return functionalClass;
    }

    /**
     * Getter for the collection of Location instances
     *
     * @return Collection of Location instances.
     */
    public Collection<Location> getLocations() {
        return locations;
    }

    /**
     * Getter for the collection of GenomicContext instances
     *
     * @return Collection of GenomicContext instances.
     */
    public Collection<GenomicContext> getGenomicContexts() {
        return genomic_contexts;
    }

    /**
     * Getter for the request count
     *
     * @return The request count.
     */
    public int getRequestCount() {
        return requestCount;
    }

    /**
     * Getter for the start time of the request count
     *
     * @return The start time of the request count.
     */
    public long getLimitStartTime() {
        return limitStartTime;
    }

    /**
     * Getter for the list of pipeline error messages
     *
     * @return List of strings.
     */
    public ArrayList<String> getPipelineErrors() {
        return pipeline_errors;
    }


    // Run the pipeline for a given SNP
    public void run_pipeline() throws EnsemblMappingException {

        // Variation call
        JSONObject variation_result = this.getVariationData();
        if (variation_result.has("error")) {
            checkError(variation_result, "variation", "Variant " + this.rsId + " is not found in Ensembl");
        }
        else if (variation_result.length() > 0) {
            // Merged SNP
            merged = (variation_result.getString("name").equals(this.rsId)) ? 0 : 1;

            // Mapping errors
            if (variation_result.has("failed")) {
                pipeline_errors.add(variation_result.getString("failed"));
            }

            // Mapping and genomic context calls
            JSONArray mappings = variation_result.getJSONArray("mappings");
            getMappings(mappings);

            // Genomic context & Reported genes
            if (locations.size() > 0) {

                // Functional class (most severe consequence).
                // This implies there is at least one variant location.
                if (variation_result.has("most_severe_consequence")) {
                    functionalClass = variation_result.getString("most_severe_consequence");
                }

                // Genomic context (loop over the "locations" object)
                for (Location snp_location : locations) {
                    getAllGenomicContexts(snp_location);
                }
            }
        }
        // Reported genes checks
        if (reported_genes.size() > 0) {
            checkReportedGenes();
        }
    }


    /**
     * Variation REST API call
     *
     * @return JSONObject containing the output of the Ensembl REST API endpoint "variation"
     */
    private JSONObject getVariationData() throws EnsemblMappingException {

        JSONObject variation_result = this.getSimpleRestCall("variation", this.rsId);

        return variation_result;
    }


    /**
     * Get the mappings data ( chromosome, position and cytogenetic band). Store the location information in the class
     * variable "locations" (list of "Location" classes)
     *
     * @param mappings A JSONArray object containing the list the variant locations
     */
    private void getMappings(JSONArray mappings) throws EnsemblMappingException {
        for (int i = 0; i < mappings.length(); ++i) {
            JSONObject mapping = mappings.getJSONObject(i);
            if (!mapping.has("seq_region_name")) {
                continue;
            }
            String chromosome = mapping.getString("seq_region_name");
            String position = String.valueOf(mapping.getInt("start"));

            Region cytogenetic_band = this.getRegion(chromosome, position);

            Location location = new Location(chromosome, position, cytogenetic_band);
            locations.add(location);
        }
    }


    /**
     * Get the cytogenetic band from a given location
     *
     * @param chromosome the chromosome name
     * @param position   the position of the variant
     * @return Region object only containing a region name
     */
    private Region getRegion(String chromosome, String position) throws EnsemblMappingException {

        String band = null; // Default value
        String rest_opt = "feature=band";

        // REST Call
        JSONArray cytogenetic_band_result = this.getOverlapRegionCalls(chromosome, position, position, rest_opt);

        if (cytogenetic_band_result.length() != 0 && !cytogenetic_band_result.getJSONObject(0).has("overlap_error")) {
            String cytogenetic_band = cytogenetic_band_result.getJSONObject(0).getString("id");

            Matcher matcher1 = Pattern.compile("^[0-9]+|[XY]$").matcher(chromosome); // Chromosomes
            Matcher matcher2 = Pattern.compile("^MT$").matcher(chromosome);          // Mitochondria
            if (matcher1.matches() || matcher2.matches()) {
                band = chromosome + cytogenetic_band;
            }
        }

        Region region = new Region(band);

        return region;
    }


    /**
     * Run the genomic context pipeline for both sources (Ensembl and NCBI)
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     */
    private void getAllGenomicContexts(Location snp_location) throws EnsemblMappingException {

        int chr_start = 1;
        int chr_end = this.getChromosomeEnd(snp_location.getChromosomeName());

        this.getGenomicContext(snp_location, chr_start, chr_end, this.ensembl_source);
        this.getGenomicContext(snp_location, chr_start, chr_end, this.ncbi_source);
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
            throws EnsemblMappingException {
        // By default the db_type is 'core' (i.e. Ensembl)
        String rest_opt = "feature=gene";
        if (source.equals(this.ncbi_source)) {
            rest_opt += "&logic_name=" + this.ncbi_logic_name;
            rest_opt += "&db_type=" + this.ncbi_db_type;
        }
        // Overlapping genes
        this.getOverlappingGenes(snp_location, source, rest_opt);

        // Upstream genes
        this.getUpstreamGenes(snp_location, source, chr_start, rest_opt);

        // Downstream genes
        this.getDownstreamGenes(snp_location, source, chr_end, rest_opt);
    }


    /**
     * Get the list of overlapping genes
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     * @param source       the source of the data (Ensembl or NCBI)
     * @param rest_opt     the extra parameters to add at the end of the REST call url
     */
    private void getOverlappingGenes(Location snp_location, String source, String rest_opt)
            throws EnsemblMappingException {

        String chromosome = snp_location.getChromosomeName();
        String position = snp_location.getChromosomePosition();

        // Check if there are overlap genes
        JSONArray overlap_gene_result = this.getOverlapRegionCalls(chromosome, position, position, rest_opt);

        if (overlap_gene_result.length() != 0 && !overlap_gene_result.getJSONObject(0).has("overlap_error")) {
            for (int i = 0; i < overlap_gene_result.length(); ++i) {
                JSONObject gene_json_object = overlap_gene_result.getJSONObject(i);

                String gene_name = gene_json_object.getString("external_name");
                overlapping_genes.add(gene_name);
            }

            this.addGenomicContext(overlap_gene_result, snp_location, source, "overlap");
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
            throws EnsemblMappingException {
        String type = "upstream";

        String chromosome = snp_location.getChromosomeName();
        String position = snp_location.getChromosomePosition();

        int position_up = Integer.parseInt(snp_location.getChromosomePosition()) - genomic_distance;
        if (position_up < 0) {
            position_up = chr_start;
        }
        String pos_up = String.valueOf(position_up);

        // Check if there are overlap genes
        JSONArray overlap_gene_result = this.getOverlapRegionCalls(chromosome, pos_up, position, rest_opt);

        if ((overlap_gene_result.length() != 0 && !overlap_gene_result.getJSONObject(0).has("overlap_error")) ||
                overlap_gene_result.length() == 0) {
            boolean closest_found = this.addGenomicContext(overlap_gene_result, snp_location, source, type);
            if (!closest_found) {
                if (position_up > chr_start) {
                    JSONArray closest_gene = this.getNearestGene(chromosome, position, pos_up, 1, rest_opt, type);
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
            throws EnsemblMappingException {
        String type = "downstream";

        String chromosome = snp_location.getChromosomeName();
        String position = snp_location.getChromosomePosition();

        int position_down = Integer.parseInt(snp_location.getChromosomePosition()) + genomic_distance;
        // Check the downstream position to avoid having a position over the 3' end of the chromosome
        if (chr_end != 0) {
            if (position_down > chr_end) {
                position_down = chr_end;
            }
            String pos_down = String.valueOf(position_down);

            // Check if there are overlap genes
            JSONArray overlap_gene_result = this.getOverlapRegionCalls(chromosome, position, pos_down, rest_opt);

            if ((overlap_gene_result.length() != 0 && !overlap_gene_result.getJSONObject(0).has("overlap_error")) ||
                    overlap_gene_result.length() == 0) {
                boolean closest_found = this.addGenomicContext(overlap_gene_result, snp_location, source, type);
                if (!closest_found) {
                    if (position_down != chr_end) {
                        JSONArray closest_gene =
                                this.getNearestGene(chromosome, position, pos_down, chr_end, rest_opt, type);
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
                new SingleNucleotidePolymorphism(); // TODO Try to use the repository to find existing SNP ?
        snp_tmp.setRsId(this.rsId);

        // Get closest gene
        if (intergenic) {
            int pos = Integer.parseInt(position);

            for (int i = 0; i < json_gene_list.length(); ++i) {
                JSONObject json_gene = json_gene_list.getJSONObject(i);
                String gene_id = json_gene.getString("id");
                String gene_name = json_gene.getString("external_name");

                if ((gene_name != null && overlapping_genes.contains(gene_name)) || gene_name ==
                        null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                    continue;
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
                if ((gene_name != null && overlapping_genes.contains(gene_name)) || gene_name ==
                        null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                    continue;
                }
                int pos = Integer.parseInt(position);
                if (type.equals("upstream")) {
                    distance = pos - json_gene.getInt("end");
                }
                else if (type.equals("downstream")) {
                    distance = json_gene.getInt("start") - pos;
                }
            }
            Long dist = new Long(distance);

            // TODO: (Emma) Updated GENE model object so it can have
            // TODO:  collection of entrez/ensembl gene IDs
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
                                                   mapping_method,
                                                   is_closest_gene);

            genomic_contexts.add(gc);
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
     * @return A JSONArray object containing a single JSONObject corresponding to the closest gene (upstream or
     * downstream) over the 100kb range
     */
    private JSONArray getNearestGene(String chromosome,
                                     String snp_position,
                                     String position,
                                     int boundary,
                                     String rest_opt,
                                     String type) throws EnsemblMappingException {

        int position1 = Integer.parseInt(position);
        int position2 = Integer.parseInt(position);
        int snp_pos = Integer.parseInt(snp_position);

        String new_pos = position;

        JSONArray closest_gene = new JSONArray();
        int closest_distance = 0;

        if (type.equals("upstream")) {
            position1 = position2 - genomic_distance;
            position1 = (position1 < 0) ? boundary : position1;
            new_pos = String.valueOf(position1);
        }
        else {
            if (type.equals("downstream")) {
                position2 = position1 + genomic_distance;
                position2 = (position2 > boundary) ? boundary : position2;
                new_pos = String.valueOf(position2);
            }
        }

        String pos1 = String.valueOf(position1);
        String pos2 = String.valueOf(position2);

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

                    if ((gene_name != null && overlapping_genes.contains(gene_name)) || gene_name ==
                            null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                        continue;
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
                if (closest_gene.length() == 0 && position2 != boundary) {
                    // Recursive code to find the nearest upstream or downstream gene
                    closest_gene = this.getNearestGene(chromosome, snp_position, new_pos, boundary, rest_opt, type);
                }
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
            throws EnsemblMappingException {
        String webservice = "overlap_region";
        String endpoint = this.getEndpoint(webservice);
        String data = chromosome + ":" + position1 + "-" + position2;

        EnsemblRestService rest_overlap = new EnsemblRestService(endpoint, data, rest_opt);
        JSONArray overlap_result = new JSONArray();
        try {
            rateLimit();
            rest_overlap.getRestCall();
            JsonNode result = rest_overlap.getRestResults();

            if (result.isArray()) {
                overlap_result = result.getArray();
            }
            else {
                // Errors
                ArrayList rest_errors = rest_overlap.getErrors();
                if (rest_errors.size() > 0) {
                    overlap_result = new JSONArray("[{\"overlap_error\":\"1\"}]");
                    for (int i = 0; i < rest_errors.size(); ++i) {
                        this.pipeline_errors.add(rest_errors.get(i).toString());
                    }
                }
            }
        }
        catch (IOException | InterruptedException | UnirestException e) {
            getLog().error("Encountered a " + e.getClass().getSimpleName() +
                                   " whilst trying to run mapping of SNP", e);
            throw new EnsemblMappingException();
        }
        return overlap_result;
    }





    /**
     * Get the end position of a given chromosome, using an Ensembl REST API call
     *
     * @param chromosome the chromosome name
     * @return the position of the end of the chromosome
     */
    private int getChromosomeEnd(String chromosome) throws EnsemblMappingException {
        int chr_end = 0;
        String webservice = "info_assembly";
        JSONObject info_result = this.getSimpleRestCall(webservice, chromosome);

        if (info_result.length() > 0) {
            if (info_result.has("length")) {
                chr_end = info_result.getInt("length");
            }
        }

        return chr_end;
    }


    /**
     * Check that the reported gene symbols exist and that they are located in the same chromosome as the variant
     */
    private void checkReportedGenes() throws EnsemblMappingException {
        for (String reported_gene : this.reported_genes) {

            reported_gene = reported_gene.replaceAll(" ", ""); // Remove extra spaces

            // Skip the iteration if the gene name is in the "gene-to-ignore" list
            if (this.reported_genes_to_ignore.contains(reported_gene)) {
                continue;
            }

            String webservice = "lookup_symbol";
            JSONObject reported_gene_result = this.getSimpleRestCall(webservice, reported_gene);

            // Gene symbol found in Ensembl
            if (reported_gene_result.length() > 0) {
                // Check if the gene is in the same chromosome as the variant
                if (reported_gene_result.has("seq_region_name")) {
                    if (this.locations.size() > 0) {
                        String gene_chromosome = reported_gene_result.getString("seq_region_name");
                        int same_chromosome = 0;
                        for (Location location : this.locations) {
                            String snp_chromosome = location.getChromosomeName();
                            if (gene_chromosome.equals(snp_chromosome)) {
                                same_chromosome = 1;
                                break;
                            }
                        }
                        if (same_chromosome == 0) {
                            pipeline_errors.add(
                                    "Reported gene " + reported_gene + " is on a different chromosome (chr" +
                                            gene_chromosome + ")");
                        }
                    }
                    else {
                        pipeline_errors.add("Can't compare the " + reported_gene +
                                                    " location in Ensembl: no mapping available for the variant");
                    }
                }
                // No gene location found
                else {
                    pipeline_errors.add("Can't find a location in Ensembl for the reported gene " + reported_gene);
                }
            }
        }
    }


    /**
     * Check the type of error returned by the REST web service JSON output
     *
     * @param result          The JSONObject result
     * @param webservice      The name of the REST web service
     * @param default_message The default error message
     */
    private void checkError(JSONObject result, String webservice, String default_message) {
        if (result.getString("error").contains("page not found")) {
            pipeline_errors.add("Web service '" + webservice + "' not found or not working.");
        }
        else {
            if (default_message.equals("")) {
                pipeline_errors.add(result.getString("error"));
            }
            else {
                pipeline_errors.add(default_message);
            }
        }
    }



}
