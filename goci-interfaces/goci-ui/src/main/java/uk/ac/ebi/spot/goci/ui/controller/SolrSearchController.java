package uk.ac.ebi.spot.goci.ui.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ac.ebi.spot.goci.ui.SearchConfiguration;
import uk.ac.ebi.spot.goci.ui.exception.IllegalParameterCombinationException;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 31/01/15
 */
@Controller
public class SolrSearchController {
    private SearchConfiguration searchConfiguration;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public SolrSearchController(SearchConfiguration searchConfiguration) {
        this.searchConfiguration = searchConfiguration;
    }

    protected Logger getLog() {
        return log;
    }

    @RequestMapping(value = "api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doSolrSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "jsonp", required = false, defaultValue = "false") boolean useJsonp,
            @RequestParam(value = "callback", required = false) String callbackFunction,
            @RequestParam(value = "max", required = false, defaultValue = "10") int maxResults,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "group", required = false, defaultValue = "false") boolean useGroups,
            @RequestParam(value = "group.by", required = false) String groupBy,
            @RequestParam(value = "group.limit", required = false, defaultValue = "10") int groupLimit,
            HttpServletResponse response) throws IOException {
        StringBuilder solrSearchBuilder = buildBaseSearchRequest();

        addFacet(solrSearchBuilder, searchConfiguration.getDefaultFacet());
        if (useJsonp) {
            addJsonpCallback(solrSearchBuilder, callbackFunction);
        }
        if (useGroups) {
            addGrouping(solrSearchBuilder, groupBy, groupLimit);
        }
        else {
            addRowsAndPage(solrSearchBuilder, maxResults, page);
        }
        addQuery(solrSearchBuilder, query);

        // dispatch search
        dispatchSearch(solrSearchBuilder.toString(), response.getOutputStream());
    }

    @RequestMapping(value = "api/search/study", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doStudySolrSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "jsonp", required = false, defaultValue = "false") boolean useJsonp,
            @RequestParam(value = "callback", required = false) String callbackFunction,
            @RequestParam(value = "max", required = false, defaultValue = "10") int maxResults,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            HttpServletResponse response) throws IOException {
        StringBuilder solrSearchBuilder = buildBaseSearchRequest();

        if (useJsonp) {
            addJsonpCallback(solrSearchBuilder, callbackFunction);
        }
        addRowsAndPage(solrSearchBuilder, maxResults, page);
        addFilterQuery(solrSearchBuilder, searchConfiguration.getDefaultFacet(), "Study");
        addQuery(solrSearchBuilder, query);

        // dispatch search
        dispatchSearch(solrSearchBuilder.toString(), response.getOutputStream());
    }

    @RequestMapping(value = "api/search/singlenucleotidepolymorphism", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doSnpSolrSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "jsonp", required = false, defaultValue = "false") boolean useJsonp,
            @RequestParam(value = "callback", required = false) String callbackFunction,
            @RequestParam(value = "max", required = false, defaultValue = "10") int maxResults,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            HttpServletResponse response) throws IOException {
        StringBuilder solrSearchBuilder = buildBaseSearchRequest();

        if (useJsonp) {
            addJsonpCallback(solrSearchBuilder, callbackFunction);
        }
        addRowsAndPage(solrSearchBuilder, maxResults, page);
        addFilterQuery(solrSearchBuilder,
                       searchConfiguration.getDefaultFacet(),
                       "SingleNucleotidePolymorphism");
        addQuery(solrSearchBuilder, query);

        // dispatch search
        dispatchSearch(solrSearchBuilder.toString(), response.getOutputStream());
    }

    @RequestMapping(value = "api/search/association", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doAssociationSolrSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "jsonp", required = false, defaultValue = "false") boolean useJsonp,
            @RequestParam(value = "callback", required = false) String callbackFunction,
            @RequestParam(value = "max", required = false, defaultValue = "10") int maxResults,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            HttpServletResponse response) throws IOException {
        StringBuilder solrSearchBuilder = buildBaseSearchRequest();

        if (useJsonp) {
            addJsonpCallback(solrSearchBuilder, callbackFunction);
        }
        addRowsAndPage(solrSearchBuilder, maxResults, page);
        addFilterQuery(solrSearchBuilder, searchConfiguration.getDefaultFacet(), "Association");
        addQuery(solrSearchBuilder, query);

        // dispatch search
        dispatchSearch(solrSearchBuilder.toString(), response.getOutputStream());
    }

    @RequestMapping(value = "api/search/diseasetrait", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doTraitSolrSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "jsonp", required = false, defaultValue = "false") boolean useJsonp,
            @RequestParam(value = "callback", required = false) String callbackFunction,
            @RequestParam(value = "max", required = false, defaultValue = "10") int maxResults,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            HttpServletResponse response) throws IOException {
        StringBuilder solrSearchBuilder = buildBaseSearchRequest();

        if (useJsonp) {
            addJsonpCallback(solrSearchBuilder, callbackFunction);
        }
        addRowsAndPage(solrSearchBuilder, maxResults, page);
        addFilterQuery(solrSearchBuilder, searchConfiguration.getDefaultFacet(), "DiseaseTrait");
        addQuery(solrSearchBuilder, query);

        // dispatch search
        dispatchSearch(solrSearchBuilder.toString(), response.getOutputStream());
    }

    @RequestMapping(value = "api/search/traitcount", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doTraitCountSolrSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "jsonp", required = false, defaultValue = "false") boolean useJsonp,
            @RequestParam(value = "callback", required = false) String callbackFunction,
            @RequestParam(value = "max", required = false, defaultValue = "10") int maxResults,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "facet.mincount", required = false, defaultValue = "1") int mincount,
            HttpServletResponse response) throws IOException {
        StringBuilder solrSearchBuilder = buildBaseSearchRequest();

        if (useJsonp) {
            addJsonpCallback(solrSearchBuilder, callbackFunction);
        }
        addRowsAndPage(solrSearchBuilder, maxResults, page);
        addFacet(solrSearchBuilder, "traitName_s");
        addFacetMincount(solrSearchBuilder, mincount);
        addFilterQuery(solrSearchBuilder, "resourcename", "study");
        addQuery(solrSearchBuilder, query);

        // dispatch search
        dispatchSearch(solrSearchBuilder.toString(), response.getOutputStream());
    }

    private StringBuilder buildBaseSearchRequest() {
        // build base request
        StringBuilder solrSearchBuilder = new StringBuilder();
        solrSearchBuilder.append(searchConfiguration.getGwasSearchServer().toString())
                .append("/select?")
                .append("wt=json");
        return solrSearchBuilder;
    }

    @RequestMapping(value = "api/search/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doFilterSolrSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "jsonp", required = false, defaultValue = "false") boolean useJsonp,
            @RequestParam(value = "callback", required = false) String callbackFunction,
            @RequestParam(value = "max", required = false, defaultValue = "10") int maxResults,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "group", required = false, defaultValue = "false") boolean useGroups,
            @RequestParam(value = "group.by", required = false) String groupBy,
            @RequestParam(value = "group.limit", required = false, defaultValue = "10") int groupLimit,
            @RequestParam(value = "pvalfilter", required = false) String pvalRange,
            @RequestParam(value = "orfilter", required = false) String orRange,
            @RequestParam(value = "betafilter", required = false) String betaRange,
            @RequestParam(value = "datefilter", required = false) String dateRange,
            @RequestParam(value = "traitfilter[]", required = false) String[] traits,
            HttpServletResponse response) throws IOException {
        StringBuilder solrSearchBuilder = buildBaseSearchRequest();

        addFacet(solrSearchBuilder, searchConfiguration.getDefaultFacet());
        if (useJsonp) {
            addJsonpCallback(solrSearchBuilder, callbackFunction);
        }
        if (useGroups) {
            addGrouping(solrSearchBuilder, groupBy, groupLimit);
        }
        else {
            addRowsAndPage(solrSearchBuilder, maxResults, page);
        }

        if (pvalRange != "") {
            getLog().debug(pvalRange);
            addFilterQuery(solrSearchBuilder, "pValue", pvalRange);
        }
        /**TO DO - when we split OR and beta, modify this controller to reflect that change!!***/
        if (orRange != "") {
            getLog().debug(orRange);

            addFilterQuery(solrSearchBuilder, "orPerCopyNum", orRange);
            addFilterQuery(solrSearchBuilder, "orType", "true");
        }
        if (betaRange != "") {
            getLog().debug(betaRange);

            addFilterQuery(solrSearchBuilder, "orPerCopyNum", betaRange);
            addFilterQuery(solrSearchBuilder, "orType", "false");
        }
        if (dateRange != "") {
            getLog().debug(dateRange);

            addFilterQuery(solrSearchBuilder, "publicationDate", dateRange);
        }
        if (traits != null) {
            System.out.println(String.valueOf(traits));

            addFilterQuery(solrSearchBuilder, "traitName_s", traits);
        }

        addQuery(solrSearchBuilder, query);

        // dispatch search
        dispatchSearch(solrSearchBuilder.toString(), response.getOutputStream());
    }

    @RequestMapping(value = "api/search/sort", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doSortSolrSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "jsonp", required = false, defaultValue = "false") boolean useJsonp,
            @RequestParam(value = "callback", required = false) String callbackFunction,
            @RequestParam(value = "max", required = false, defaultValue = "10") int maxResults,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "facet", required = false) String facet,
            @RequestParam(value = "group", required = false, defaultValue = "false") boolean useGroups,
            @RequestParam(value = "group.by", required = false) String groupBy,
            @RequestParam(value = "group.limit", required = false, defaultValue = "10") int groupLimit,
            @RequestParam(value = "pvalfilter", required = false) String pvalRange,
            @RequestParam(value = "orfilter", required = false) String orRange,
            @RequestParam(value = "betafilter", required = false) String betaRange,
            @RequestParam(value = "datefilter", required = false) String dateRange,
            @RequestParam(value = "traitfilter[]", required = false) String[] traits,
            @RequestParam(value = "sort", required = false) String sort,
            HttpServletResponse response) throws IOException {
        StringBuilder solrSearchBuilder = buildBaseSearchRequest();

        addFacet(solrSearchBuilder, searchConfiguration.getDefaultFacet());
        addFilterQuery(solrSearchBuilder, "resourcename", facet);
        if (useJsonp) {
            addJsonpCallback(solrSearchBuilder, callbackFunction);
        }
        if (useGroups) {
            addGrouping(solrSearchBuilder, groupBy, groupLimit);
        }
        else {
            addRowsAndPage(solrSearchBuilder, maxResults, page);
        }

        if (pvalRange != "") {
            getLog().debug(pvalRange);
            addFilterQuery(solrSearchBuilder, "pValue", pvalRange);
        }
        /**TO DO - when we split OR and beta, modify this controller to reflect that change!!***/
        if (orRange != "") {
            getLog().debug(orRange);

            addFilterQuery(solrSearchBuilder, "orPerCopyNum", orRange);
            addFilterQuery(solrSearchBuilder, "orType", "true");
        }
        if (betaRange != "") {
            getLog().debug(betaRange);

            addFilterQuery(solrSearchBuilder, "orPerCopyNum", betaRange);
            addFilterQuery(solrSearchBuilder, "orType", "false");
        }
        if (dateRange != "") {
            getLog().debug(dateRange);

            addFilterQuery(solrSearchBuilder, "publicationDate", dateRange);
        }
        if (traits != null) {
            System.out.println(String.valueOf(traits));

            addFilterQuery(solrSearchBuilder, "traitName_s", traits);
        }
        if(sort != ""){
            addSortQuery(solrSearchBuilder, sort);
        }

        addQuery(solrSearchBuilder, query);

        // dispatch search
        dispatchSearch(solrSearchBuilder.toString(), response.getOutputStream());
    }

    @RequestMapping(value = "api/search/moreresults", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doMoreResultsSolrSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "jsonp", required = false, defaultValue = "false") boolean useJsonp,
            @RequestParam(value = "callback", required = false) String callbackFunction,
            @RequestParam(value = "max", required = false, defaultValue = "10") int maxResults,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "facet", required = false) String facet,
            @RequestParam(value = "pvalfilter", required = false) String pvalRange,
            @RequestParam(value = "orfilter", required = false) String orRange,
            @RequestParam(value = "betafilter", required = false) String betaRange,
            @RequestParam(value = "datefilter", required = false) String dateRange,
            @RequestParam(value = "traitfilter[]", required = false) String[] traits,
            @RequestParam(value = "sort", required = false) String sort,
            HttpServletResponse response) throws IOException {
        StringBuilder solrSearchBuilder = buildBaseSearchRequest();

        if (useJsonp) {
            addJsonpCallback(solrSearchBuilder, callbackFunction);
        }
        addFilterQuery(solrSearchBuilder, "resourcename", facet);
         addRowsAndPage(solrSearchBuilder, maxResults, page);


        if (pvalRange != "") {
            getLog().debug(pvalRange);
            addFilterQuery(solrSearchBuilder, "pValue", pvalRange);
        }
        /**TO DO - when we split OR and beta, modify this controller to reflect that change!!***/
        if (orRange != "") {
            getLog().debug(orRange);

            addFilterQuery(solrSearchBuilder, "orPerCopyNum", orRange);
            addFilterQuery(solrSearchBuilder, "orType", "true");
        }
        if (betaRange != "") {
            getLog().debug(betaRange);

            addFilterQuery(solrSearchBuilder, "orPerCopyNum", betaRange);
            addFilterQuery(solrSearchBuilder, "orType", "false");
        }
        if (dateRange != "") {
            getLog().debug(dateRange);

            addFilterQuery(solrSearchBuilder, "publicationDate", dateRange);
        }
        if (traits != null) {
            System.out.println(String.valueOf(traits));

            addFilterQuery(solrSearchBuilder, "traitName_s", traits);
        }

        if(sort != ""){
            addSortQuery(solrSearchBuilder, sort);
        }

        addQuery(solrSearchBuilder, query);

        // dispatch search
        dispatchSearch(solrSearchBuilder.toString(), response.getOutputStream());
    }

    private void addSortQuery(StringBuilder solrSearchBuilder, String sort) {
        solrSearchBuilder.append("&sort=").append(sort);
    }

    private void addFacet(StringBuilder solrSearchBuilder, String facet) {
        // add configuration
        solrSearchBuilder.append("&facet=true&facet.field=").append(facet);
    }

    private void addFacetMincount(StringBuilder solrSearchBuilder, int min){
        solrSearchBuilder.append("&facet.mincount=").append(min);
    }

    private void addJsonpCallback(StringBuilder solrSearchBuilder, String callbackFunction) {
        if (callbackFunction == null) {
            throw new IllegalParameterCombinationException("If jsonp = true, you must specify a callback function " +
                                                                   "name with callback parameter");
        }
        else {
            solrSearchBuilder.append("&json.wrf=").append(callbackFunction);
        }
    }

    private void addGrouping(StringBuilder solrSearchBuilder, String groupBy, int maxResults) {
        solrSearchBuilder.append("&rows=10000")
                .append("&group=true")
                .append("&group.limit=").append(maxResults)
                .append("&group.field=").append(groupBy);
    }

    private void addRowsAndPage(StringBuilder solrSearchBuilder, int maxResults, int page) {
        solrSearchBuilder.append("&rows=").append(maxResults)
                .append("&start=").append((page - 1) * maxResults);
    }

    private void addFilterQuery(StringBuilder solrSearchBuilder, String filterOn, String filterBy) {
        solrSearchBuilder.append("&fq=").append(filterOn).append("%3A").append(filterBy);
    }

    private void addFilterQuery(StringBuilder solrSearchBuilder, String filterOn, String[] filterBy) {
        int counter = 0;
        String filterString = "";
        for(String filter : filterBy) {
            if(counter == 0){
                filterString = filterString.concat(filterOn).concat("%3A%22").concat(filter).concat("%22");
                counter++;
            } else{
                filterString = filterString.concat("+OR+").concat(filterOn).concat("%3A%22").concat(filter).concat("%22");
                counter++;
            }
        }
        System.out.println(filterString);
        solrSearchBuilder.append("&fq=").append(filterString);

    }

    private void addQuery(StringBuilder solrSearchBuilder, String query) throws IOException {
        try {
            solrSearchBuilder.append("&q=").append(URLEncoder.encode(query, "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new IOException("Invalid query string - " + query, e);
        }
    }

    private void dispatchSearch(String searchString, OutputStream out) throws IOException {
        System.out.println(searchString);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(searchString);
        if (System.getProperty("http.proxyHost") != null) {
            HttpHost proxy;
            if (System.getProperty("http.proxyPort") != null) {
                proxy = new HttpHost(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty
                        ("http.proxyPort")));
            }
            else {
                proxy = new HttpHost(System.getProperty("http.proxyHost"));
            }
            httpGet.setConfig(RequestConfig.custom().setProxy(proxy).build());
        }

        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            getLog().debug("Received HTTP response: " + response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();
            entity.writeTo(out);
            EntityUtils.consume(entity);
        }
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    private String handleIllegalParameterCombinationException(IllegalParameterCombinationException e) {
        getLog().error("An illegal parameter combination was received", e);
        return "An illegal parameter combination was received: " + e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    private String handleIOException(IOException e) {
        getLog().error("An IOException occurred during solr search communication", e);
        return "Your search could not be performed - we encountered a problem.  Weve been notified and will attempt " +
                "to rectify the problem as soon as possible.  If problems persist, please email gwas-info@ebi.ac.uk";
    }

    @RequestMapping(value = "api/search/downloads", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getSearchResults(
            @RequestParam("q") String query,
            @RequestParam(value = "pvalfilter", required = false) String pvalRange,
            @RequestParam(value = "orfilter", required = false) String orRange,
            @RequestParam(value = "betafilter", required = false) String betaRange,
            @RequestParam(value = "datefilter", required = false) String dateRange,
            @RequestParam(value = "traitfilter[]", required = false) String[] traits
        ) throws IOException {

        StringBuilder solrSearchBuilder = buildBaseSearchRequest();

        int maxResults = 10000;
        int page = 1;
        String facet = "association";
        addFilterQuery(solrSearchBuilder, "resourcename", facet);
        addRowsAndPage(solrSearchBuilder, maxResults, page);


        if (pvalRange != "") {
            getLog().debug(pvalRange);
            addFilterQuery(solrSearchBuilder, "pValue", pvalRange);
        }
        /**TO DO - when we split OR and beta, modify this controller to reflect that change!!***/
        if (orRange != "") {
            getLog().debug(orRange);

            addFilterQuery(solrSearchBuilder, "orPerCopyNum", orRange);
            addFilterQuery(solrSearchBuilder, "orType", "true");
        }
        if (betaRange != "") {
            getLog().debug(betaRange);

            addFilterQuery(solrSearchBuilder, "orPerCopyNum", betaRange);
            addFilterQuery(solrSearchBuilder, "orType", "false");
        }
        if (dateRange != "") {
            getLog().debug(dateRange);

            addFilterQuery(solrSearchBuilder, "publicationDate", dateRange);
        }
        if (traits != null) {
            System.out.println(String.valueOf(traits));

            addFilterQuery(solrSearchBuilder, "traitName_s", traits);
        }

        addQuery(solrSearchBuilder, query);

        String searchString = solrSearchBuilder.toString();

        /*this step is necessary as something about calling the URL directly rather than through $.getJSON messes
        up the URL encoding but explicitly URL encoding causes other interference
        */
        searchString = searchString.replace(" ", "+");

        // dispatch search
        return dispatchSearch(searchString);
    }

    @RequestMapping(value = "api/search/stats", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Map<String, Object> getCatalogStats() {
        Map<String, Object> response = new HashMap<>();

        String releasedate;
        String studycount;
        String associationcount;
        String genebuild;

        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResource("catalogstats.properties").openStream());
            releasedate = properties.getProperty("releasedate");
            studycount = properties.getProperty("studycount");
            associationcount = properties.getProperty("assocationcount");
            genebuild = properties.getProperty("genomebuild");

            response.put("date", releasedate);
            response.put("studies", studycount);
            response.put("associations", associationcount);
            response.put("genebuild", genebuild);

        }
        catch (IOException e) {
            throw new RuntimeException(
                    "Unable to create dispatcher service: failed to read pubmed.properties resource", e);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Unable to create dispatcher service: you must provide a integer query interval " +
                            "in minutes (pubmed.query.interval.mins)", e);
        }

        return response;
    }

    //TO DO use jackson to read the json and parse it into a string
    private String dispatchSearch(String searchString) throws IOException {
        System.out.println(searchString);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(searchString);
        if (System.getProperty("http.proxyHost") != null) {
            HttpHost proxy;
            if (System.getProperty("http.proxyPort") != null) {
                proxy = new HttpHost(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty
                        ("http.proxyPort")));
            }
            else {
                proxy = new HttpHost(System.getProperty("http.proxyHost"));
            }
            httpGet.setConfig(RequestConfig.custom().setProxy(proxy).build());
        }

        String file = null;
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            getLog().debug("Received HTTP response: " + response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();

            BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {

                file = processJson(output);


            }

            EntityUtils.consume(entity);
        }
        if(file == null){
            file = "Some error occurred during your request. Please try again or contact the GWAS Catalog team for assistance";
        }

        return file;
    }

    private String processJson(String output) throws IOException {

        String header = "Date Added to Catalog\tPUBMEDID\tFirst Author\tDate\tJournal\tLink\tStudy\tDisease/Trait\tInitial Sample Size\tReplication Sample Size\tRegion\tChr_id\tChr_pos\tReported Gene(s)\tMapped_gene\tUpstream_gene_id\tDownstream_gene_id\tSnp_gene_ids\tUpstream_gene_distance\tDownstream_gene_distance\tStrongest SNP-Risk Allele\tSNPs\tMerged\tSnp_id_current\tContext\tIntergenic\tRisk Allele Frequency\tp-Value\tPvalue_mlog\tp-Value (text)\tOR or beta\t95% CI (text)\tPlatform [SNPs passing QC]\tCNV\r\n";

        StringBuilder result = new StringBuilder();
        result.append(header);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(output);
        JsonNode responseNode = node.get("response");
        JsonNode docs = responseNode.get("docs");

        for(JsonNode doc : docs) {
            StringBuilder line = new StringBuilder();

            if(doc.get("catalogAddedDate") != null){
                String date = doc.get("catalogAddedDate").asText();
                line.append(date.substring(0,10));

            }
            else{
                line.append(" ");
            }
            line.append("\t");
            line.append(doc.get("pubmedId").asText());
            line.append("\t");
            line.append(doc.get("author_s").asText());
            line.append("\t");
            if(doc.get("publicationDate") != null){
                String date = doc.get("publicationDate").asText();
                line.append(date.substring(0,10));

            }
            else{
                line.append(" ");
            }
            line.append("\t");
            line.append(doc.get("publication").asText());
            line.append("\t");

            String pubLink = "www.ncbi.nlm.nih.gov/pubmed/".concat(doc.get("pubmedId").asText());
            line.append(pubLink);
            line.append("\t");

            line.append(doc.get("title").get(0).asText());
            line.append("\t");
            line.append(doc.get("traitName_s").asText());
            line.append("\t");
            line.append(doc.get("initialSampleDescription").asText());
            line.append("\t");
            line.append(doc.get("replicateSampleDescription").asText());
            line.append("\t");
            if(doc.get("region") != null) {
                line.append(doc.get("region").get(0).asText());
            }
            else{
                line.append(" ");
            }
            line.append("\t");
            if(doc.get("chromosomeName") != null) {
                line.append(doc.get("chromosomeName").get(0).asText());
            }
            else{
                line.append(" ");
            }
            line.append("\t");
            if(doc.get("chromosomePosition") != null) {
                line.append(doc.get("chromosomePosition").get(0).asText());
            }
            else{
                line.append(" ");
            }
            line.append("\t");

            if(doc.get("reportedGene") != null){
                int it = 0;
                for(JsonNode gene : doc.get("reportedGene")){
                    if(it > 0){
                       line.append(", ");
                    }
                    line.append(gene.asText());
                    it++;
                }
            }
            line.append("\t");
            if(doc.get("mappedGene") != null) {
                line.append(doc.get("mappedGene").get(0).asText());
            }
            else{
                line.append(" ");
            }
            line.append("\t");
            if(doc.get("mappedGeneLinks") != null) {
                if(doc.get("mappedGeneLinks").size() == 1){
                    line.append(" ");
                    line.append("\t");
                    line.append(" ");
                    line.append("\t");
                    String geneLink = doc.get("mappedGeneLinks").get(0).asText();
                    int index = geneLink.indexOf("|");
                    String geneId = geneLink.substring(index+1);
                    line.append(geneId);
                    line.append("\t");
                }
                else{
                    if(doc.get("mappedGene") != null) {
                        String upstream = null;
                        String downstream = null;
                        String mapped = doc.get("mappedGene").get(0).asText();

                        for(JsonNode geneLink : doc.get("mappedGeneLinks")) {
                            int index = geneLink.asText().indexOf("|");
                            String gene = geneLink.asText().substring(0, index - 1);
                            String geneId = geneLink.asText().substring(index + 1);

                            if(mapped.indexOf(gene) == 0){
                                upstream = geneId;
                            }
                            else{
                                downstream = geneId;
                            }

                        }
                        line.append(upstream);
                        line.append("\t");
                        line.append(downstream);
                        line.append("\t");
                        line.append(" ");
                        line.append("\t");

                    }
                    else{
                        line.append(" ");
                        line.append("\t");
                        line.append(" ");
                        line.append("\t");
                        line.append(" ");
                        line.append("\t");
                    }

                }

            }
            else{
                line.append(" ");
                line.append("\t");
                line.append(" ");
                line.append("\t");
                line.append(" ");
                line.append("\t");
            }

//            line.append(doc.get("upstreamDistance").asText());
            line.append(" ");
            line.append("\t");
//            line.append(doc.get("downstreamDistance").asText());
            line.append(" ");
            line.append("\t");

            if(doc.get("strongestAllele") != null) {
                line.append(doc.get("strongestAllele").get(0).asText());
            }
            else{
                line.append(" ");
            }
            line.append("\t");
            if(doc.get("rsId") != null){
                line.append(doc.get("rsId").get(0).asText());
            }
            else{
                line.append(" ");
            }
            line.append("\t");
//            line.append(doc.get("merged").asText());
            line.append(" ");
            line.append("\t");
            if(doc.get("rsId") != null){
                String rsId = doc.get("rsId").get(0).asText();

                if(rsId.indexOf("rs") == 0 && rsId.indexOf("rs", 2) == -1) {
                    line.append(rsId.substring(2));
                }
                else{
                    line.append(" ");
                }
            }
            else{
                line.append(" ");
            }
            line.append("\t");

            if(doc.get("context") != null) {
                line.append(doc.get("context").get(0).asText());
                line.append("\t");
                line.append("0");
                line.append("\t");
            }
            else{
                line.append(" ");
                line.append("\t");
                line.append("1");
                line.append("\t");

            }

            if(doc.get("riskFrequency") != null) {
                line.append(doc.get("riskFrequency").asText());
            }
            else{
                line.append(" ");
            }
            line.append("\t");
            if(doc.get("pValue") != null){
                if(!doc.get("pValue").asText().equals("NR")) {
                    double pvalue = doc.get("pValue").asDouble();
                    line.append(pvalue);
                    line.append("\t");
                    double mlog = Math.log10(pvalue);
                    line.append(-mlog);
                }
                else {
                    line.append(doc.get("pValue").asText());
                    line.append("\t");
                    line.append(" ");
                }
            }
            else{
                line.append(" ");
                line.append("\t");
                line.append(" ");
            }

            line.append("\t");
            if(doc.get("qualifier") != null) {
                line.append(doc.get("qualifier").get(0).asText());
            }
            else{
                line.append(" ");
            }            line.append("\t");
            line.append(doc.get("orPerCopyNum").asText());
            line.append("\t");
            if(doc.get("orPerCopyRange") != null) {
                line.append(doc.get("orPerCopyRange").asText());
            }
            else{
                line.append(" ");
            }
            line.append("\t");
//            line.append(doc.get("platform").asText());
            line.append(" ");
            line.append("\t");
//            line.append(doc.get("cnv").asText());
            line.append("N");
            line.append("\r\n");

            result.append(line.toString());

        }
//        return "Hello world. Your search results are coming soon to a browser near you.";
        return result.toString();
    }
}
