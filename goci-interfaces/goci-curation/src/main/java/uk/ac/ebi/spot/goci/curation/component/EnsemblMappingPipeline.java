package uk.ac.ebi.spot.goci.curation.component;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.ebi.spot.goci.curation.service.EnsemblRestService;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Laurent on 15/07/15.
 */
public class EnsemblMappingPipeline {

    private String rsId;
    private int merged;
    private Collection<String> reported_genes = new ArrayList<>();
    private Collection<Location> locations = new ArrayList<>();
    private Collection<GenomicContext> genomic_contexts = new ArrayList<>();
    private ArrayList<String> pipeline_errors = new ArrayList<>();

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


    protected void setEndpoints() {
        String species = "homo_sapiens";
        this.endpoints.put("variation", "/variation/"+species+"/");
        this.endpoints.put("lookup_symbol", "/lookup/symbol/" + species + "/");
        this.endpoints.put("overlap_region", "/overlap/region/"+species+"/");
        this.endpoints.put("info_assembly", "/info/assembly/" + species + "/");
    }


    // Run the pipeline for a given SNP
    public void run_pipeline() {
        // Variation call
        JSONObject variation_result = this.getVariationData();
        // System.out.println(variation_result.toString());

        // Merged SNP
        this.merged = (variation_result.getString("name") == this.rsId) ? 0 : 1;

        // Mapping and genomic context calls
        JSONArray mappings = variation_result.getJSONArray("mappings");
        // TODO: mapping method
        this.getMappings(mappings);

        // Genomic context (loop over the "locations" object)
        if (locations.size() > 0) {
            for (Location location : locations) {
                String chromosome = location.getChromosomeName();
                String position   = location.getChromosomePosition();
                this.getAllGenomicContexts(chromosome,position);
            }
        }

        // TODO: reported genes checks

    }


    // Variation REST API call
    private JSONObject getVariationData() {
        String endpoint = this.getEndpoint("variation");
        EnsemblRestService rest_variation = new EnsemblRestService(endpoint, this.rsId);
        JSONObject variation_result = new JSONObject();
        try {
            rest_variation.getRestCall();
            variation_result = rest_variation.getRestResults();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return variation_result;
    }


    // Get the mappings data ( chromosome, position and cytogenetic band) + genomic context
    private void getMappings(JSONArray mappings) {
        for (int i = 0; i < mappings.length(); ++i) {
            JSONObject mapping = mappings.getJSONObject(i);
            String chromosome = mapping.getString("seq_region_name");
            String position = String.valueOf(mapping.getInt("start"));

            Region cytogenetic_band = this.getRegion(chromosome, position);
            //System.out.println("Mapping: " + cytogenetic_band.getName() + " | " + chromosome + " | " + position);

            Location location = new Location(chromosome,position,cytogenetic_band);
            locations.add(location);
        }
    }


    /* Get the cytogenetic band from a given location
     * @params chromosome chromosome name
     * @params position the position of the SNP
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


    // Run the genomic context pipeline for both sources
    private void getAllGenomicContexts(String chromosome, String position) {
        this.getGenomicContext(chromosome, position, this.ensembl_source);
        this.getGenomicContext(chromosome, position, this.ncbi_source);
    }


    private void getGenomicContext(String chromosome, String position, String source) {
        // By default the db_type is 'core' (i.e. Ensembl)
        String rest_opt = "feature=gene";
        if (source == this.ncbi_source) {
            rest_opt += "&logic_name="+this.ncbi_logic_name;
            rest_opt += "&db_type="+this.ncbi_db_type;
        }
        // Overlapping genes
        this.getOverlappingGenes(chromosome, position, source, rest_opt);

        // Upstream genes
        this.getUpstreamGenes(chromosome, position, source, rest_opt);

        // Downstream genes
        this.getDownstreamGenes(chromosome, position, source, rest_opt);
    }


    // Get the list of overlapping genes
    private void getOverlappingGenes(String chromosome, String position, String source, String rest_opt) {

        // Check if there are overlap genes
        JSONArray overlap_gene_result = this.getOverlapRegionCalls(chromosome, position, position, rest_opt);

        for (int i = 0; i < overlap_gene_result.length(); ++i) {
            JSONObject gene_json_object = overlap_gene_result.getJSONObject(i);

            String gene_name = gene_json_object.getString("external_name");
            overlapping_genes.add(gene_name);
        }
        this.addGenomicContext(overlap_gene_result, chromosome, position, source, "overlap");
    }


    // Get the list of upstream genes
    private void getUpstreamGenes(String chromosome, String position, String source, String rest_opt) {
        String type = "upstream";

        int position_up = Integer.parseInt(position) - genomic_distance;
        if (position_up < 0) {
            position_up = 1;
        }
        String pos_up = String.valueOf(position_up);

        // Check if there are overlap genes
        JSONArray overlap_gene_result = this.getOverlapRegionCalls(chromosome,pos_up,position,rest_opt);

        boolean closest_found = this.addGenomicContext(overlap_gene_result, chromosome, position, source, type);
        if (!closest_found) {
            if (position_up > 1) {
                JSONArray closest_gene = this.getNearestGene(chromosome,position, pos_up, 1, rest_opt, type);
                if (closest_gene.length() > 0 ) {
                    addGenomicContext(closest_gene, chromosome, position, source, type);
                }
            }
        }
    }


    // Get the list of downstream genes
    private void getDownstreamGenes(String chromosome, String position, String source, String rest_opt) {
        String type = "downstream";

        int position_down = Integer.parseInt(position) + genomic_distance;

        // Check the downstream position to avoid having a position over the 3' end of the chromosome
        int chr_end = this.getChromosomeEnd(chromosome);
        if (chr_end != 0 && position_down > chr_end) {
            position_down = chr_end;
        }
        String pos_down = String.valueOf(position_down);

        // Check if there are overlap genes
        JSONArray overlap_gene_result = this.getOverlapRegionCalls(chromosome,position,pos_down,rest_opt);

        boolean closest_found = this.addGenomicContext(overlap_gene_result, chromosome, position, source, type);
        if (!closest_found) {
            if (position_down != chr_end) {
                JSONArray closest_gene = this.getNearestGene(chromosome,position, pos_down, chr_end, rest_opt, type);
                if (closest_gene.length() > 0 ) {
                    addGenomicContext(closest_gene, chromosome, position, source, type);
                }
            }
        }
    }

    private boolean addGenomicContext(JSONArray json_gene_list, String chromosome, String position, String source, String type) {
        String closest_gene = "";
        int closest_distance = 0;
        boolean intergenic = (type == "overlap") ? false : true;
        boolean upstream   = (type == "upstream") ? true : false;
        boolean downstream = (type == "downstream") ? true: false;

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
            Gene gene_object = new Gene(gene_name,ncbi_id,ensembl_id);
            GenomicContext gc = new GenomicContext(intergenic,upstream,downstream,dist,snp_tmp,gene_object,source,mapping_method);
            // TODO add the closest upstream/downstream info
            genomic_contexts.add(gc);
        }
        return (closest_gene != "") ? true : false;
    }


    private JSONArray getNearestGene (String chromosome,String snp_position, String position, int boundary, String rest_opt, String type) {

        //chr,snp_position,position,boundary,overlap_list,rest_opt,type
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


    private int getChromosomeEnd (String chromosome) {
        int chr_end = 0;

        String endpoint = this.getEndpoint("info_assembly");
        String data = chromosome;
        EnsemblRestService rest_info = new EnsemblRestService(endpoint, data);
        JSONObject info_result = new JSONObject();
        try {
            rest_info.getRestCall();
            info_result = rest_info.getRestResults();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        chr_end = info_result.getInt("length");

        return chr_end;
    }


    private String getEndpoint(String endpoint) {
        return this.endpoints.get(endpoint);
    }
}
