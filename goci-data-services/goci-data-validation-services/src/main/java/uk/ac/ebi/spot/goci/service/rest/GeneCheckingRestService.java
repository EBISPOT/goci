package uk.ac.ebi.spot.goci.service.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.GeneLookupJson;
import uk.ac.ebi.spot.goci.model.RestResponseResult;
import uk.ac.ebi.spot.goci.service.EnsemblRestTemplateService;
import uk.ac.ebi.spot.goci.service.EnsemblRestcallHistoryService;


import javax.validation.constraints.NotNull;

/**
 * Created by Emma on 22/04/16.
 *
 * @author emma
 *         <p>
 *         Checks a gene symbol is valid using standard Spring mechanism to consume a RESTful service
 */
@Service
public class GeneCheckingRestService {

    @NotNull
    @Value("${mapping.gene_lookup_endpoint}")
    private String endpoint;

    private EnsemblRestTemplateService ensemblRestTemplateService;

    private EnsemblRestcallHistoryService ensemblRestcallHistoryService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public GeneCheckingRestService(EnsemblRestTemplateService ensemblRestTemplateService,
                                   EnsemblRestcallHistoryService ensemblRestcallHistoryService) {
        this.ensemblRestTemplateService = ensemblRestTemplateService;
        this.ensemblRestcallHistoryService = ensemblRestcallHistoryService;
    }

    /**
     * Check gene name returns a response
     *
     * @param gene Gene name to check
     * @return Error message
     */
    public String checkGeneSymbolIsValid(String gene, String eRelease) {

        String error = null;
        // TODO: collpase with the below method 12-04-2017
        //GeneLookupJson geneLookupJson = new GeneLookupJson();

        try {
            RestResponseResult geneDataApiResult = ensemblRestcallHistoryService.getEnsemblRestCallByTypeAndParamAndVersion(
                    "lookup_symbol", gene, eRelease);

            if (geneDataApiResult == null ) {
                geneDataApiResult = ensemblRestTemplateService.getRestCall(getEndpoint(), gene, "");
                ensemblRestcallHistoryService.create(geneDataApiResult, "lookup_symbol", gene, eRelease);
            }
            if (geneDataApiResult.hasErorr()) {
                error = geneDataApiResult.getError();
            } else {
                if (geneDataApiResult.getRestResult().getObject().has("object_type")) {
                    String objectType = geneDataApiResult.getRestResult().getObject().get("object_type").toString();
                    if (!(objectType.compareToIgnoreCase("gene") == 0)) {
                        error = "Gene symbol ".concat(gene).concat(" is not valid");
                    }
                }
            }
        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            error = "Gene symbol ".concat(gene).concat(" was not retrieved by Ensembl Mapping. Contact Admin.");
            getLog().error("Gene Symbol".concat(gene).concat(" : was not retrieved. (exception)"), e);
        }
        return error;
    }

    /**
     * Get the chromosome a SNP resides on
     *
     * @param gene Gene name/symbol
     * @return The name of the chromosome the gene is located on
     */
    public String getGeneLocation(String gene, String eRelease) {

        String geneChromosome = null;
        // TODO: collpase with the above method 12-04-2017
        //GeneLookupJson geneLookupJson = new GeneLookupJson();

        try {
            RestResponseResult geneDataApiResult = ensemblRestcallHistoryService.getEnsemblRestCallByTypeAndParamAndVersion(
                    "lookup_symbol", gene, eRelease);

            if (geneDataApiResult == null ) {
                geneDataApiResult = ensemblRestTemplateService.getRestCall(getEndpoint(), gene, "");
                ensemblRestcallHistoryService.create(geneDataApiResult, "lookup_symbol", gene, eRelease);
            }

            if (!(geneDataApiResult.hasErorr())) {
                if (geneDataApiResult.getRestResult().getObject().has("seq_region_name")) {
                    geneChromosome = geneDataApiResult.getRestResult().getObject().getString("seq_region_name");
                }
            }
            if (geneChromosome == null) {
                getLog().error("Getting locations for gene ".concat(gene).concat("failed"));
            }
        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            getLog().error("Getting locations for gene ".concat(gene).concat("failed"), e);
        }

        return geneChromosome;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}