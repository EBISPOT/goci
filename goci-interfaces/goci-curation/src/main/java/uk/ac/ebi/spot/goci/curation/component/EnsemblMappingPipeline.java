package uk.ac.ebi.spot.goci.curation.component;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.ebi.spot.goci.curation.service.EnsemblRestService;
import uk.ac.ebi.spot.goci.model.EnsemblGene;
import uk.ac.ebi.spot.goci.model.EntrezGene;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Laurent on 15/07/15.
 * @author Laurent
 * Class containing the Ensembl mapping and checking pipeline
 * The structure of this pipeline is similar to the javascript pipeline developped in the scrip goci-snp-association-mapping.js
 * (goci/goci-interfaces/goci-curation/src/main/resources/static/js/goci-snp-association-mapping.js)
 */
public class EnsemblMappingPipeline {

    private String rsId;
    private int merged;
    private Collection<String> reported_genes = new ArrayList<>();
    private Collection<Location> locations = new ArrayList<>();
    private Collection<GenomicContext> genomic_contexts = new ArrayList<>();
    private ArrayList<String> pipeline_errors = new ArrayList<>();

    // Internal variables populated within the class
    private ArrayList<String> overlapping_genes = new ArrayList<>();
    private Hashtable<String, String> endpoints = new Hashtable<String, String>();

    // Fixed variables
    private final String ensembl_source  = "Ensembl";
    private final String ncbi_source     = "NCBI";
    private final String ncbi_db_type    = "otherfeatures";
    private final String ncbi_logic_name = "refseq_import";
    private final String mapping_method  = "Ensembl pipeline";
    private final int genomic_distance   = 100000; // 100kb

    // JPA no-args constructor
    public EnsemblMappingPipeline() {
        this.setEndpoints();
    }

    public EnsemblMappingPipeline(String rsId, Collection<String> reported_genes) {
        this.rsId = rsId;
        this.reported_genes = reported_genes;
        this.setEndpoints();
    }


    // Set the different Ensembl REST API endpoints used in the pipeline
    protected void setEndpoints() {
        String species = "homo_sapiens";
        this.endpoints.put("variation", "/variation/" + species + "/");
        this.endpoints.put("lookup_symbol", "/lookup/symbol/" + species + "/");
        this.endpoints.put("overlap_region", "/overlap/region/" + species + "/");
        this.endpoints.put("info_assembly", "/info/assembly/" + species + "/");
    }


    /**
     * Getter for the collection of Location instances
     * @return Collection of Location instances.
     */
    public Collection<Location> getLocations() {
        return locations;
    }


    /**
     * Getter for the collection of GenomicContext instances
     * @return Collection of GenomicContext instances.
     */
    public Collection<GenomicContext> getGenomicContexts() {
        return genomic_contexts;
    }


    /**
     * Getter for the list of pipeline error messages
     * @return List of strings.
     */
    public ArrayList<String> getPipelineErrors() {
        return pipeline_errors;
    }


    // Run the pipeline for a given SNP
    public void run_pipeline() {
        // Variation call
        JSONObject variation_result = this.getVariationData();
        if (variation_result.has("error")) {
            pipeline_errors.add("Variant " + this.rsId + " is not found in Ensembl");
        }
        else {
            // Merged SNP
            this.merged = (variation_result.getString("name") == this.rsId) ? 0 : 1;

            // Mapping and genomic context calls
            JSONArray mappings = variation_result.getJSONArray("mappings");
            this.getMappings(mappings);

            // Genomic context & Reported genes
            if (locations.size() > 0) {

                // Genomic context (loop over the "locations" object)
                for (Location snp_location : locations) {
                    this.getAllGenomicContexts(snp_location);
                }

                // Reported genes checks
                if (reported_genes.size() > 0) {
                    this.checkReportedGenes();
                }
            }
        }
    }


    /**
     * Variation REST API call
     * @return JSONObject containing the output of the Ensembl REST API endpoint "variation"
     */
    private JSONObject getVariationData() {

        JSONObject variation_result = this.getSimpleRestCall("variation", this.rsId);

        return variation_result;
    }


    /**
     * Get the mappings data ( chromosome, position and cytogenetic band).
     * Store the location information in the class variable "locations" (list of "Location" classes)
     *
     * @param mappings A JSONArray object containing the list the variant locations
     */
    private void getMappings(JSONArray mappings) {
        for (int i = 0; i < mappings.length(); ++i) {
            JSONObject mapping = mappings.getJSONObject(i);
            if (!mapping.has("seq_region_name")) {
                continue;
            }
            String chromosome = mapping.getString("seq_region_name");
            String position = String.valueOf(mapping.getInt("start"));

            Region cytogenetic_band = this.getRegion(chromosome, position);

            Location location = new Location(chromosome,position,cytogenetic_band);
            locations.add(location);
        }
    }


    /**
     * Get the cytogenetic band from a given location
     *
     * @param chromosome the chromosome name
     * @param position the position of the variant
     * @return Region object only containing a region name
     */
    private Region getRegion(String chromosome, String position) {

        String band = "NA"; // Default value
        String rest_opt = "feature=band";

        // REST Call
        JSONArray cytogenetic_band_result = this.getOverlapRegionCalls(chromosome, position, position, rest_opt);

        String cytogenetic_band = cytogenetic_band_result.getJSONObject(0).getString("id");

        Matcher matcher1 = Pattern.compile("^[0-9]+|[XY]$").matcher(chromosome); // Chromosomes
        Matcher matcher2 = Pattern.compile("^MT$").matcher(chromosome);          // Mitochondria
        if (matcher1.matches() || matcher2.matches()) {
            band = chromosome + cytogenetic_band;
        }

        Region region = new Region(band);

        return region;
    }


    /**
     * Run the genomic context pipeline for both sources (Ensembl and NCBI)
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     */
    private void getAllGenomicContexts(Location snp_location) {
        this.getGenomicContext(snp_location, this.ensembl_source);
        this.getGenomicContext(snp_location, this.ncbi_source);
    }


    /**
     * Get the genomic context in 3 calls: overlap, upstream and downstream genes
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     * @param source the source of the data (Ensembl or NCBI)
     */
    private void getGenomicContext(Location snp_location, String source) {
        // By default the db_type is 'core' (i.e. Ensembl)
        String rest_opt = "feature=gene";
        if (source == this.ncbi_source) {
            rest_opt += "&logic_name="+this.ncbi_logic_name;
            rest_opt += "&db_type="+this.ncbi_db_type;
        }
        // Overlapping genes
        this.getOverlappingGenes(snp_location, source, rest_opt);

        // Upstream genes
        this.getUpstreamGenes(snp_location, source, rest_opt);

        // Downstream genes
        this.getDownstreamGenes(snp_location, source, rest_opt);
    }


    /**
     * Get the list of overlapping genes
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     * @param source the source of the data (Ensembl or NCBI)
     * @param rest_opt the extra parameters to add at the end of the REST call url
     */
    private void getOverlappingGenes(Location snp_location, String source, String rest_opt) {

        String chromosome = snp_location.getChromosomeName();
        String position   = snp_location.getChromosomePosition();

        // Check if there are overlap genes
        JSONArray overlap_gene_result = this.getOverlapRegionCalls(chromosome, position, position, rest_opt);

        for (int i = 0; i < overlap_gene_result.length(); ++i) {
            JSONObject gene_json_object = overlap_gene_result.getJSONObject(i);

            String gene_name = gene_json_object.getString("external_name");
            overlapping_genes.add(gene_name);
        }
        this.addGenomicContext(overlap_gene_result, snp_location, source, "overlap");
    }


    /**
     * Get the list of upstream genes
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     * @param source the source of the data (Ensembl or NCBI)
     * @param rest_opt the extra parameters to add at the end of the REST call url
     */
    private void getUpstreamGenes(Location snp_location, String source, String rest_opt) {
        String type = "upstream";

        String chromosome = snp_location.getChromosomeName();
        String position   = snp_location.getChromosomePosition();

        int position_up = Integer.parseInt(position) - genomic_distance;
        if (position_up < 0) {
            position_up = 1;
        }
        String pos_up = String.valueOf(position_up);

        // Check if there are overlap genes
        JSONArray overlap_gene_result = this.getOverlapRegionCalls(chromosome,pos_up,position,rest_opt);

        boolean closest_found = this.addGenomicContext(overlap_gene_result, snp_location, source, type);
        if (!closest_found) {
            if (position_up > 1) {
                JSONArray closest_gene = this.getNearestGene(chromosome,position, pos_up, 1, rest_opt, type);
                if (closest_gene.length() > 0 ) {
                    addGenomicContext(closest_gene, snp_location, source, type);
                }
            }
        }
    }


    /**
     * Get the list of downstream genes
     *
     * @param snp_location an instance of the Location class (chromosome name and position)
     * @param source the source of the data (Ensembl or NCBI)
     * @param rest_opt the extra parameters to add at the end of the REST call url
     */
    private void getDownstreamGenes(Location snp_location, String source, String rest_opt) {
        String type = "downstream";

        String chromosome = snp_location.getChromosomeName();
        String position   = snp_location.getChromosomePosition();

        int position_down = Integer.parseInt(position) + genomic_distance;

        // Check the downstream position to avoid having a position over the 3' end of the chromosome
        int chr_end = this.getChromosomeEnd(chromosome);
        if (chr_end != 0 && position_down > chr_end) {
            position_down = chr_end;
        }
        String pos_down = String.valueOf(position_down);

        // Check if there are overlap genes
        JSONArray overlap_gene_result = this.getOverlapRegionCalls(chromosome,position,pos_down,rest_opt);

        boolean closest_found = this.addGenomicContext(overlap_gene_result, snp_location, source, type);
        if (!closest_found) {
            if (position_down != chr_end) {
                JSONArray closest_gene = this.getNearestGene(chromosome, position, pos_down, chr_end, rest_opt, type);
                if (closest_gene.length() > 0 ) {
                    addGenomicContext(closest_gene, snp_location, source, type);
                }
            }
        }
    }

    /**
     * Create GenomicContext objects from the JSONObjects and add them to the class variable "genomic_contexts"
     * (list of "GenomicContext" classes)
     *
     * @param json_gene_list the list of overlapping genes in JSONObject format
     * @param snp_location an instance of the Location class (chromosome name and position)
     * @param source the source of the data (Ensembl or NCBI)
     * @param type the type of genomic context (i.e. overlap, upstream, downstream)
     * @return boolean to indicate whether a closest gene has been found or not (only relevant for upstream and downstream gene)
     */
    private boolean addGenomicContext(JSONArray json_gene_list, Location snp_location, String source, String type) {
        String closest_gene = "";
        int closest_distance = 0;
        boolean intergenic = (type == "overlap") ? false : true;
        boolean upstream   = (type == "upstream") ? true : false;
        boolean downstream = (type == "downstream") ? true: false;

        String position = snp_location.getChromosomePosition();

        SingleNucleotidePolymorphism snp_tmp = new SingleNucleotidePolymorphism(); // TODO Try to use the repository to find existing SNP ?
        snp_tmp.setRsId(this.rsId);

        // Get closest gene
        if (intergenic) {
            int pos = Integer.parseInt(position);
            for (int i = 0; i < json_gene_list.length(); ++i) {
                JSONObject json_gene = json_gene_list.getJSONObject(i);
                String gene_id   = json_gene.getString("id");
                String gene_name = json_gene.getString("external_name");

                if ((gene_name != null && overlapping_genes.contains(gene_name)) || gene_name == null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                    continue;
                }

                int distance = 0;
                if (type == "upstream") {
                    distance = pos - json_gene.getInt("end");
                }
                else if (type == "downstream") {
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
            String gene_id    = json_gene.getString("id");
            String gene_name  = json_gene.getString("external_name");
            String ncbi_id    = (source == "NCBI") ? gene_id : null;
            String ensembl_id = (source == "Ensembl") ? gene_id : null;

            int distance = 0;

            if (intergenic) {
                if ((gene_name != null && overlapping_genes.contains(gene_name)) || gene_name ==
                        null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                    continue;
                }
                int pos = Integer.parseInt(position);
                if (type == "upstream") {
                    distance = pos - json_gene.getInt("end");
                }
                else if (type == "downstream") {
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
            boolean is_closest_gene = (closest_gene == gene_id && closest_gene != "") ? true : false;

            GenomicContext gc = new GenomicContext(intergenic, upstream, downstream, dist, snp_tmp, gene_object, snp_location, source, mapping_method, is_closest_gene);

            genomic_contexts.add(gc);
        }
        return (closest_gene != "") ? true : false;
    }


    /**
     * Recursive method to get the closest upstream or downstream gene over the 100kb range, jumping 100kb by 100kb
     * until a gene is found or the boundary of the chromosome is reached.
     *
     * @param chromosome the chromosome name
     * @param snp_position the position of the variant
     * @param position the start position for the search (at least 100kb upstream or downstream from the variant)
     * @param boundary the chromosome boundary (upstream: beginning of the chromosome (position 1), downstream: end of the chromosome)
     * @param rest_opt the extra parameters to add at the end of the REST call url (inherited from other methods)
     * @param type the type of genomic context (i.e. overlap, upstream, downstream)
     * @return A JSONArray object containing a single JSONObject corresponding to the closest gene (upstream or downstream) over the 100kb range
     */
    private JSONArray getNearestGene (String chromosome,String snp_position, String position, int boundary, String rest_opt, String type) {

        int position1 = Integer.parseInt(position);
        int position2 = Integer.parseInt(position);
        int snp_pos   = Integer.parseInt(snp_position);

        String new_pos = position;

        JSONArray closest_gene = new JSONArray();
        int closest_distance = 0;

        if (type == "upstream") {
            position1 = position2 - genomic_distance;
            position1 = (position1 < 0) ? boundary : position1;
            new_pos = String.valueOf(position1);
        }
        else {
            if (type == "downstream") {
                position2 = position1 + genomic_distance;
                position2 = (position2 > boundary) ? boundary : position2;
                new_pos = String.valueOf(position2);
            }
        }

        String pos1 = String.valueOf(position1);
        String pos2 = String.valueOf(position2);

        JSONArray json_gene_list = this.getOverlapRegionCalls(chromosome,pos1,pos2,rest_opt);

        for (int i = 0; i < json_gene_list.length(); ++i) {
            JSONObject json_gene = json_gene_list.getJSONObject(i);
            String gene_id = json_gene.getString("id");
            String gene_name = json_gene.getString("external_name");

            if ((gene_name != null && overlapping_genes.contains(gene_name)) || gene_name ==
                    null) { // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                continue;
            }

            int distance = 0;
            if (type == "upstream") {
                distance = snp_pos - json_gene.getInt("end");
            }
            else if (type == "downstream") {
                distance = json_gene.getInt("start") - snp_pos;
            }

            if ((distance < closest_distance && distance > 0) || closest_distance == 0) {
                closest_gene = new JSONArray("["+json_gene.toString()+"]");
                closest_distance = distance;
            }
        }
        if (closest_gene.length() == 0) {
            if (position2 != boundary) {
                // Recursive code to find the nearest upstream or downstream gene
                closest_gene = this.getNearestGene(chromosome, snp_position, new_pos, boundary, rest_opt, type);
            }
        }
        return closest_gene;
    }


    /**
     * Ensembl REST API call for the overlap region endpoint
     *
     * @param chromosome the chromosome name
     * @param position1 the 5' position of the region
     * @param position2 the 3' position of the region
     * @param rest_opt the extra parameters to add at the end of the REST call url (inherited from other methods)
     * @return A JSONArray object containing a list of JSONObjects corresponding to the genes overlapping the region
     */
    private JSONArray getOverlapRegionCalls (String chromosome, String position1, String position2, String rest_opt) {
        String endpoint = this.getEndpoint("overlap_region");
        String data = chromosome+":"+position1+"-"+position2;

        EnsemblRestService rest_overlap = new EnsemblRestService(endpoint, data, rest_opt);
        JSONArray overlap_result = new JSONArray();
        try {
            rest_overlap.getRestCall();
            JSONObject result = rest_overlap.getRestResults();
            overlap_result = result.getJSONArray("array");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return overlap_result;
    }


    /**
     * Simple generic Ensembl REST API call method.
     *
     * @param endpoint_type the endpoint name
     * @param data the data/id/symbol we want to query
     * @return the corresponding JSONObject
     */
    private JSONObject getSimpleRestCall (String endpoint_type, String data) {
        String endpoint = this.getEndpoint(endpoint_type);
        EnsemblRestService ens_rest_call = new EnsemblRestService(endpoint, data);
        JSONObject json_result = new JSONObject();
        try {
            ens_rest_call.getRestCall();
            json_result = ens_rest_call.getRestResults();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return json_result;
    }


    /**
     * Get the end position of a given chromosome, using an Ensembl REST API call
     *
     * @param chromosome the chromosome name
     * @return the position of the end of the chromosome
     */
    private int getChromosomeEnd (String chromosome) {
        int chr_end = 0;

        JSONObject info_result = this.getSimpleRestCall("info_assembly", chromosome);
        chr_end = info_result.getInt("length");

        return chr_end;
    }


    /**
     * Check that the reported gene symbols exist and that they are located in the same chromosome as the variant
     */
    private void checkReportedGenes() {
        for (String reported_gene : this.reported_genes) {

            reported_gene = reported_gene.replaceAll(" ",""); // Remove extra spaces

            JSONObject reported_gene_result = this.getSimpleRestCall("lookup_symbol",reported_gene);
            // Gene symbol not found in Ensembl
            if (reported_gene_result.has("error")) {
                pipeline_errors.add("Reported gene "+reported_gene+" is not found in Ensembl");
            }
            // Gene not in the same chromosome as the variant
            else {
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
                    pipeline_errors.add("Reported gene "+reported_gene+" is on a different chromosome (chr"+gene_chromosome+")");
                }
            }
        }
    }

    /**
     * Return the Ensembl REST API endpoint URL corresponding the the endpoint name provided
     *
     * @param endpoint_name the name of the REST API endpoint
     * @return the URL part specific to the queried endpoint
     */
    private String getEndpoint(String endpoint_name) {
        return this.endpoints.get(endpoint_name);
    }
}
