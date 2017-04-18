package uk.ac.ebi.spot.goci.service.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.RestResponseResult;
import uk.ac.ebi.spot.goci.model.SnpLookupJson;
import uk.ac.ebi.spot.goci.service.EnsemblRestTemplateService;
import uk.ac.ebi.spot.goci.service.EnsemblRestcallHistoryService;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Emma on 22/04/16.
 *
 * @author emma
 *         <p>
 *         Checks a SNP identifier is valid using standard Spring mechanism to consume a RESTful service
 */
@Service
public class SnpCheckingRestService {

    @NotNull
    @Value("${mapping.snp_lookup_endpoint}")
    private String endpoint;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    private EnsemblRestTemplateService ensemblRestTemplateService;

    private EnsemblRestcallHistoryService ensemblRestcallHistoryService;

    @Autowired
    public SnpCheckingRestService(EnsemblRestTemplateService ensemblRestTemplateService,
                                  EnsemblRestcallHistoryService ensemblRestcallHistoryService) {
        this.ensemblRestTemplateService = ensemblRestTemplateService;
        this.ensemblRestcallHistoryService = ensemblRestcallHistoryService;
    }

    /**
     * Check gene name returns a response
     *
     * @param snp Snp identifier to check
     * @return Error message
     */
    public String checkSnpIdentifierIsValid(String snp, String eRelease) {
        String error = null;
        try {

            RestResponseResult snpDataApiResult = ensemblRestcallHistoryService.getEnsemblRestCallByTypeAndParamAndVersion(
                    "snp", snp, eRelease);

            if (snpDataApiResult == null) {
                snpDataApiResult = ensemblRestTemplateService.getRestCall(getEndpoint(), snp, "");
                ensemblRestcallHistoryService.create(snpDataApiResult, "snp", snp, eRelease);
            }

            if ((snpDataApiResult.hasErorr())) {
                error = snpDataApiResult.getError();
            } else {
                if (snpDataApiResult.getRestResult().getObject().has("error")) {
                    error = "SNP identifier ".concat(snp).concat(" is not valid");
                }
            }

        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            error = "SNP error!";
            getLog().error("The SNP impossible to retrieve: : ".concat(snp), e);
        }

        return error;
    }

    /**
     * Get the chromosome a SNP resides on
     *
     * @param snp Snp identifier to check
     * @return Set of all SNP chromosome names
     */
    public Set<String> getSnpLocations(String snp, String eRelease) {

        Set<String> snpChromosomeNames = new HashSet<>();
        SnpLookupJson snpLookupJson = new SnpLookupJson();
        try {

            RestResponseResult snpDataApiResult = ensemblRestcallHistoryService.getEnsemblRestCallByTypeAndParamAndVersion(
                    "snp", snp, eRelease);

            if (snpDataApiResult == null) {
                snpDataApiResult = ensemblRestTemplateService.getRestCall(getEndpoint(), snp, "");
                ensemblRestcallHistoryService.create(snpDataApiResult, "snp", snp, eRelease);
            }

            if (!(snpDataApiResult.hasErorr())) {
                JSONObject snpResult = snpDataApiResult.getRestResult().getObject();
                JSONArray mappings = snpResult.getJSONArray("mappings");

                for (int i = 0; i < mappings.length(); ++i) {
                    JSONObject mapping = mappings.getJSONObject(i);
                    if (!mapping.has("seq_region_name")) {
                        continue;
                    }
                    String chromosome = mapping.getString("seq_region_name");
                    //Integer position = Integer.valueOf(mapping.getInt("start"));
                    snpChromosomeNames.add(chromosome);
                    //System.out.println("Snp chromosome: ".concat(chromosome));
                }
            }

        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            //error = "Imnpossible retrieve SNP Mapping info."
            getLog().error("Getting locations for SNP ".concat(snp).concat(" failed"), e);
        }

        return snpChromosomeNames;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}