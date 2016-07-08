package uk.ac.ebi.spot.goci.ui.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dwelter on 24/02/15.
 */
public class JsonProcessingService {

    private String json;
    private boolean includeAnnotations;
    private String type;
    private String newline;

    public JsonProcessingService(String json, boolean includeAnnotations, String type) {
        this.json = json;
        this.includeAnnotations = includeAnnotations;
        this.type = type;
        newline = System.getProperty("line.separator");

    }

    public String processJson() throws IOException {
        String header;
        
        if(type.equals("study")){
            header =
                    "DATE ADDED TO CATALOG\tPUBMEDID\tFIRST AUTHOR\tDATE\tJOURNAL\tLINK\tSTUDY\tDISEASE/TRAIT\tINITIAL SAMPLE SIZE\tREPLICATION SAMPLE SIZE\tPLATFORM [SNPS PASSING QC]\tASSOCIATION COUNT";
        }
        else{
            header =
                    "DATE ADDED TO CATALOG\tPUBMEDID\tFIRST AUTHOR\tDATE\tJOURNAL\tLINK\tSTUDY\tDISEASE/TRAIT\tINITIAL SAMPLE SIZE\tREPLICATION SAMPLE SIZE\tREGION\tCHR_ID\tCHR_POS\tREPORTED GENE(S)\tMAPPED_GENE\tUPSTREAM_GENE_ID\tDOWNSTREAM_GENE_ID\tSNP_GENE_IDS\tUPSTREAM_GENE_DISTANCE\tDOWNSTREAM_GENE_DISTANCE\tSTRONGEST SNP-RISK ALLELE\tSNPS\tMERGED\tSNP_ID_CURRENT\tCONTEXT\tINTERGENIC\tRISK ALLELE FREQUENCY\tP-VALUE\tPVALUE_MLOG\tP-VALUE (TEXT)\tOR or BETA\t95% CI (TEXT)\tPLATFORM [SNPS PASSING QC]\tCNV";
        }

        if(includeAnnotations){
            header = header.concat("\tMAPPED_TRAIT\tMAPPED_TRAIT_URI");
        }

        header = header.concat("\r\n");

        StringBuilder result = new StringBuilder();
        result.append(header);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        JsonNode responseNode = node.get("response");
        JsonNode docs = responseNode.get("docs");


        for (JsonNode doc : docs) {
            StringBuilder line = new StringBuilder();

            if(type.equals("study")) {
                processStudyJson(line, doc);
            }
            else {
                processAssociationJson(line, doc);
            }
            result.append(line.toString());
        }

        return result.toString();


    }
    
    public void processStudyJson(StringBuilder line, JsonNode doc) throws IOException{
        line.append(getDate(doc));
        line.append("\t");

        String pubmedid = getPubmedId(doc);
        line.append(pubmedid);
        line.append("\t");
        line.append(getAuthor(doc));
        line.append("\t");
        line.append(getPublicationDate(doc));
        line.append("\t");
        line.append(getJournal(doc));
        line.append("\t");

        String publink = "www.ncbi.nlm.nih.gov/pubmed/".concat(pubmedid);

        line.append(publink);
        line.append("\t");

        line.append(getTitle(doc));
        line.append("\t");

        line.append(getTrait(doc));
        line.append("\t");

        String init = getInitSample(doc);
        if (init.contains(newline)) {
            init = init.replaceAll("\n", "").replaceAll("\r", "");
        }

        line.append(init);
        line.append("\t");

        String rep = getRepSample(doc);
        if (rep.contains(newline)) {
            rep = rep.replaceAll("\n", "").replaceAll("\r", "");
        }
        line.append(rep);
        line.append("\t");

        String platform = getPlatform(doc);
        if (platform.contains(newline)) {
            platform = platform.replaceAll("\n", "").replaceAll("\r", "");
        }
        line.append(platform);

        line.append("\t");

        line.append(getAssocCount(doc));

        if(includeAnnotations){
            line.append("\t");

            Map<String, String> traits = getEfoTraits(doc);

            line.append(traits.get("trait"));
            line.append("\t");
            line.append(traits.get("uri"));
        }

        line.append("\r\n");
        
    }

    public void processAssociationJson(StringBuilder line, JsonNode doc) throws IOException {

        line.append(getDate(doc));
        line.append("\t");

        String pubmedid = getPubmedId(doc);
        line.append(pubmedid);
        line.append("\t");
        line.append(getAuthor(doc));
        line.append("\t");
        line.append(getPublicationDate(doc));
        line.append("\t");
        line.append(getJournal(doc));
        line.append("\t");

        String publink = "www.ncbi.nlm.nih.gov/pubmed/".concat(pubmedid);

        line.append(publink);
        line.append("\t");

        line.append(getTitle(doc));
        line.append("\t");

        line.append(getTrait(doc));
        line.append("\t");

        String init = getInitSample(doc);
        if (init.contains(newline)) {
            init = init.replaceAll("\n", "").replaceAll("\r", "");
        }

        line.append(init);
        line.append("\t");

        String rep = getRepSample(doc);
        if (rep.contains(newline)) {
            rep = rep.replaceAll("\n", "").replaceAll("\r", "");
        }
        line.append(rep);
        line.append("\t");

        Map<String, String> chromLocation = getChromDetails(doc);

        line.append(chromLocation.get("region"));
        line.append("\t");
        line.append(chromLocation.get("chromName"));
        line.append("\t");
        line.append(chromLocation.get("chromPos"));
        line.append("\t");

        line.append(getRepGene(doc));
        line.append("\t");


        Map<String, MappedGene> mappedGenes = getMappedGenes(doc);

        if(mappedGenes.get("ingene").getName() != ""){
            line.append(mappedGenes.get("ingene").getName());
        }
        else if (mappedGenes.get("upstream").getName() != "" && mappedGenes.get("downstream").getName() != ""){
            line.append(mappedGenes.get("upstream").getName().concat(" - ").concat(mappedGenes.get("downstream")
                                                                                           .getName()));
        }
        else if (mappedGenes.get("upstream").getName() != "" && mappedGenes.get("downstream").getName() == ""){
            line.append(mappedGenes.get("upstream").getName().concat(" - NA"));
        }
        else if (mappedGenes.get("upstream").getName() == "" && mappedGenes.get("downstream").getName() != ""){
            line.append(("NA - ").concat(mappedGenes.get("downstream").getName()));
        }
        else {
            line.append("");
        }

        line.append("\t");

        line.append(mappedGenes.get("upstream").getId());
        line.append("\t");
        line.append(mappedGenes.get("downstream").getId());
        line.append("\t");
        line.append(mappedGenes.get("ingene").getId());
        line.append("\t");

        line.append(mappedGenes.get("upstream").getDistance());
        line.append("\t");

        line.append(mappedGenes.get("downstream").getDistance());
        line.append("\t");

        line.append(getStrongestAllele(doc));
        line.append("\t");

        String rsId = getRsId(doc);
        line.append(rsId);
        line.append("\t");

        //            line.append(doc.get("merged").asText().trim());
        line.append(""); // todo - remove this when above solr field is available
        line.append("\t");

        if (rsId.indexOf("rs") == 0 && rsId.indexOf("rs", 2) == -1) {
            line.append(rsId.substring(2));
        }
        else {
            line.append("");
        }

        line.append("\t");

        String context = getContext(doc);
        line.append(context);
        line.append("\t");

        if (mappedGenes.get("ingene").getName() != "") {
            line.append("0");
        }
        else {
            line.append("1");
        }
        line.append("\t");

        line.append(getRiskFreq(doc));
        line.append("\t");

        String pvalue = getPvalue(doc);

        line.append(pvalue);
        line.append("\t");

         String mlog = getPvalueMlog(doc);
         line.append(mlog);
         line.append("\t");

        line.append(getQualifier(doc));
        line.append("\t");
        line.append(getOR(doc));
        line.append("\t");
        line.append(getCI(doc));
        line.append("\t");

        String platform = getPlatform(doc);
        if (platform.contains(newline)) {
            platform = platform.replaceAll("\n", "").replaceAll("\r", "");
        }
        line.append(platform);

        line.append("\tN");

        if(includeAnnotations){
            line.append("\t");

            Map<String, String> traits = getEfoTraits(doc);

            line.append(traits.get("trait"));
            line.append("\t");
            line.append(traits.get("uri"));
        }

        line.append("\r\n");
    
    }

    private String getPlatform(JsonNode doc) {
        String platform;

        if(doc.get("platform") != null){
            platform = doc.get("platform").asText().trim();
        }
        else {
            platform = "";
        }


         return platform;
    }


    private String getAssocCount(JsonNode doc) {
        String count;

        if(doc.get("associationCount") != null){
            count = doc.get("associationCount").asText().trim();
        }
        else{
            count = "";
        }
        return count;
    }


    private String getCI(JsonNode doc) {
        String ci = "";
        if (doc.get("range") != null) {
            ci = ci.concat(doc.get("range").asText().trim()).concat(" ");
        }
        if (doc.get("betaUnit") != null) {
            ci = ci.concat(doc.get("betaUnit").asText().trim()).concat(" ");
        }

        if (doc.get("betaDirection") != null) {
            ci = ci.concat(doc.get("betaDirection").asText().trim()).concat(" ");
        }
        if(doc.get("orDescription") != null){
            ci = ci.concat(doc.get("orDescription").asText().trim());
        }
        ci = ci.trim();
        return ci;
    }

    private String getOR(JsonNode doc) {
        String or;
        if (doc.get("orPerCopyNum") != null) {
            or = doc.get("orPerCopyNum").asText().trim();
        }
        else if (doc.get("betaNum") != null) {
            or = doc.get("betaNum").asText().trim();
        }
        else {
            or = "";
        }
        return or;
    }

    private String getQualifier(JsonNode doc) {
        String qualifier;
        if (doc.get("qualifier") != null) {
            qualifier = doc.get("qualifier").get(0).asText().trim();
        }
        else {
            qualifier = "";
        }
        return qualifier;
    }

    private String getPvalue(JsonNode doc) {
        String pvalue;
        if (doc.get("pValueMantissa") != null && doc.get("pValueExponent") != null) {
            String mant = doc.get("pValueMantissa").asText().trim();
            String exp = doc.get("pValueExponent").asText().trim();
            pvalue = mant.concat("E").concat(exp);
        }
        else {
            pvalue = "";
        }
        return pvalue;
    }

    private String getPvalueMlog(JsonNode doc){
        String pvalue;
        if (doc.get("pValueMantissa") != null && doc.get("pValueExponent") != null) {
            int mant = doc.get("pValueMantissa").asInt();
            int exp = doc.get("pValueExponent").asInt();
            double p = -(Math.log(mant)+exp);
            pvalue = String.valueOf(p);
        }
        else {
            pvalue = "";
        }
        return pvalue;
    }

    private String getRiskFreq(JsonNode doc) {
        String riskFreq;
        if (doc.get("riskFrequency") != null) {
            riskFreq = doc.get("riskFrequency").asText().trim();
        }
        else {
            riskFreq = "";
        }
        return riskFreq;
    }

    private String getContext(JsonNode doc) {
        String context;
        if (doc.get("context") != null) {
            context = doc.get("context").get(0).asText().trim();

        }
        else {
            context = "";
        }
        return context;
    }

    private String getRsId(JsonNode doc) {
        String rsId;
        if (doc.get("rsId") != null) {
            rsId = doc.get("rsId").get(0).asText().trim();
        }
        else {
            rsId = "";
        }
        return rsId;
    }

    private String getStrongestAllele(JsonNode doc) {
        String strongestAllele;
        if (doc.get("strongestAllele") != null) {
            strongestAllele = doc.get("strongestAllele").get(0).asText().trim();
        }
        else {
            strongestAllele = "";
        }
        return strongestAllele;
    }

    private String getMapGene(JsonNode doc) {
        String genes = "";
        if (doc.get("entrezMappedGenes") != null) {
            int it = 0;

            for(JsonNode m : doc.get("entrezMappedGenes")) {
                if (it > 0) {
                    genes = genes.concat(", ");
                }
                genes = genes.concat(m.asText().trim());
                it++;
            }
        }

        return genes;
    }

    private String getRepGene(JsonNode doc) {
        String genes = "";
        if (doc.get("reportedGene") != null) {
            int it = 0;
            for (JsonNode gene : doc.get("reportedGene")) {
                if (it > 0) {
                    genes = genes.concat(", ");
                }
                genes = genes.concat(gene.asText().trim());
                it++;
            }
        }
        return genes;
    }


    private Map<String, String> getChromDetails(JsonNode doc) {
        String chromName = "";
        String chromPos = "";
        String region = "";
        Map<String, String> location = new HashMap<>();

        if (doc.get("positionLinks") != null) {
            //            if(doc.get("positionLinks").size() > 1){
            for (int i = 0; i < doc.get("positionLinks").size(); i++) {
                String loc = doc.get("positionLinks").get(i).asText().trim();

                String[] locs = loc.split("\\|");

                String chrom = locs[0];

                String pattern = "^\\d+$";

                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(chrom);

                if (m.find() || chrom.equals("X") || chrom.equals("Y")) {
                    chromName = locs[0];
                    chromPos = locs[1];
                    region = locs[2];
                }
                else {
                    System.out.println(loc);
                }
            }
        }

        location.put("chromName", chromName);
        location.put("chromPos", chromPos);
        location.put("region", region);

        return location;
    }


    private String getRepSample(JsonNode doc) {
        String sampleDesc;
        if (doc.get("replicateSampleDescription") != null) {
            sampleDesc = doc.get("replicateSampleDescription").asText().trim();
        }
        else {
            sampleDesc = "";
        }
        return sampleDesc;
    }

    private String getInitSample(JsonNode doc) {
        String sampleDesc;
        if (doc.get("initialSampleDescription") != null) {
            sampleDesc = doc.get("initialSampleDescription").asText().trim();
        }
        else {
            sampleDesc = "";
        }
        return sampleDesc;
    }

    private String getTrait(JsonNode doc) {
        String traitName;
        if (doc.get("traitName_s") != null) {
            traitName = doc.get("traitName_s").asText().trim();
        }
        else {
            traitName = "";
        }
        return traitName;
    }

    private String getTitle(JsonNode doc) {
        String title;
        if (doc.get("title") != null) {
            title = doc.get("title").asText().trim();
        }
        else {
            title = "";
        }
        return title;
    }


    private String getJournal(JsonNode doc) {
        String journal;
        if (doc.get("publication") != null) {
            journal = doc.get("publication").asText().trim();
        }
        else {
            journal = "";
        }
        return journal;

    }

    private String getAuthor(JsonNode doc) {
        String author;
        if (doc.get("author_s") != null) {
            author = doc.get("author_s").asText().trim();
        }
        else {
            author = "";
        }
        return author;
    }

    private String getPubmedId(JsonNode doc) {
        String pmid;
        if (doc.get("pubmedId") != null) {
            pmid = doc.get("pubmedId").asText().trim();
        }
        else {
            pmid = "";
        }
        return pmid;
    }

    private String getPublicationDate(JsonNode doc) {
        String date;
        if (doc.get("publicationDate") != null) {
            date = doc.get("publicationDate").asText().trim().substring(0, 10);

        }
        else {
            date = "";
        }
        return date;
    }

    private String getDate(JsonNode doc) {
        String date;
        if (doc.get("catalogPublishDate") != null) {
            date = doc.get("catalogPublishDate").asText().trim().substring(0, 10);
        }
        else {
            date = "";
        }
        return date;
    }

    private Map<String, MappedGene> getMappedGenes(JsonNode doc) {
        List<String> actuallyMapped = new ArrayList<>();
        List<String> processed = new ArrayList<>();

        if (doc.get("entrezMappedGenes") != null) {

            for(JsonNode m : doc.get("entrezMappedGenes")) {
               actuallyMapped.add(m.asText().trim());
            }
        }

        Map<String, MappedGene> genes = new HashMap<String, MappedGene>();
        MappedGene upstream = new MappedGene();
        MappedGene downstream = new MappedGene();
        MappedGene ingene = new MappedGene();

        if (doc.get("entrezMappedGeneLinks") != null) {
            for (JsonNode geneLink : doc.get("entrezMappedGeneLinks")) {
                String[] data = geneLink.asText().trim().split("\\|");

                String gene = data[0];

                if(actuallyMapped.contains(gene)){

                    String chrom = data[3];

                    String pattern = "^\\d+$";

                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(chrom);

                    if (m.find() || chrom.equals("X") || chrom.equals("Y")) {
                        processed.add(gene);
                        String geneId, geneDist;


                        geneId = data[1];
                        geneDist = data[2];


                        int dist = Integer.parseInt(geneDist);

                        if (dist == 0) {
                            ingene.setOrAppendId(geneId);
                            ingene.setDistance(geneDist);
                            ingene.setOrAppendName(gene);
                        }
                        else if (dist > 0) {
                            upstream.setId(geneId);
                            upstream.setDistance(geneDist);
                            upstream.setName(gene);
                        }
                        else {
                            downstream.setId(geneId);
                            downstream.setDistance(geneDist.substring(1));
                            downstream.setName(gene);
                        }
                    }
                }

            }
        }

        String lit = "";
        for(String am : actuallyMapped){
            if(!processed.contains(am)){
                if(lit.equals("")){
                    lit = am;
                }
                else {
                    lit = lit.concat(", ").concat(am);
                }
            }
        }
        if(!lit.equals("")){
            ingene.setOrAppendName(lit);
        }
        genes.put("upstream", upstream);
        genes.put("downstream", downstream);
        genes.put("ingene", ingene);

        return genes;
    }


    private Map<String, String> getEfoTraits(JsonNode doc) {
        Map<String, String> traits = new HashMap<>();

        String trait = "";
        String uri = "";

        if(doc.get("efoLink") != null){
            for(JsonNode efoLink : doc.get("efoLink")){
                String[] data = efoLink.asText().trim().split("\\|");

                if(trait == ""){
                    trait = data[0];
                    uri = data[2];
                }
                else{
                    trait = trait.concat(", ").concat(data[0]);
                    uri = uri.concat(", ").concat(data[2]);
                }
            }
        }

        traits.put("trait", trait);
        traits.put("uri", uri);

        return traits;
    }


    private class MappedGene{
        private String name, id, distance;

        public MappedGene(){
            name = "";
            id = "";
            distance = "";
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getDistance() {
            return distance;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public void setOrAppendId(String id){
            if(this.id.equals("")){
                this.id = id;
            }
            else{
                this.id = this.id.concat(", ").concat(id);
            }
        }

        public void setOrAppendName(String name){
            if(this.name.equals("")){
                this.name = name;
            }
            else{
                this.name = this.name.concat(", ").concat(name);
            }
        }
    }
}


