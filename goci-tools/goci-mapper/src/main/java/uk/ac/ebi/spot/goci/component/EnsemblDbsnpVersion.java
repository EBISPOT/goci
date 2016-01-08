package uk.ac.ebi.spot.goci.component;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.EnsemblRestIOException;
import uk.ac.ebi.spot.goci.service.EnsemblRestService;

import java.io.IOException;
import java.util.List;

/**
 * Created by Laurent on 28/09/15.
 *
 * @author Laurent
 *         <p>
 *         Class getting the dbSNP version from the Ensembl REST API
 */
@Service
public class EnsemblDbsnpVersion {

    private String species = "homo_sapiens";
    private String endpoint = "/info/variation/" + species + "/";
    private String source = "dbSNP";

    /**
     * Getter for the dbSNP version
     *
     * @return the dbSNP version
     */
    public int getDbsnpVersion() throws EnsemblRestIOException {
        JSONArray ensembl_result = this.getSimpleRestCall();
        if (ensembl_result.length() > 0) {
            if (ensembl_result.getJSONObject(0).has("error")) {
                throw new EnsemblRestIOException(checkError(ensembl_result));
            }
            else if (!ensembl_result.getJSONObject(0).has("error")) {
                // Check if there are releases key
                JSONObject variant_source = ensembl_result.getJSONObject(0);
                // Get version
                if (variant_source.getString("name").equals(source) && variant_source.has("version")) {
                    return variant_source.getInt("version");
                }
                else {
                    throw new EnsemblRestIOException("No dbSNP version information can be identified");
                }
            }
            else {
                throw new EnsemblRestIOException(
                        "No dbSNP or error information found while trying to check Ensembl dbSNP version");
            }
        }
        else {
            throw new EnsemblRestIOException("Empty response body found while trying to check dbSNP version");
        }
    }

    /**
     * Check the type of error returned by the REST web service JSON output
     *
     * @param result The JSONObject result
     */
    private String checkError(JSONArray result) {
        if (result.getJSONObject(0).getString("error").contains("page not found")) {
            return "Web service '" + endpoint + "' not found or not working.";
        }
        else {
            return result.getJSONObject(0).getString("error");
        }
    }


    /**
     * Simple generic Ensembl REST API call method for array result.
     *
     * @return the corresponding JSONArray
     */
    private JSONArray getSimpleRestCall() throws EnsemblRestIOException {
        EnsemblRestService ens_rest_call = new EnsemblRestService(endpoint, "", "filter=" + source);
        JSONArray json_result = new JSONArray();
        try {
            ens_rest_call.getRestCall();
            JsonNode result = ens_rest_call.getRestResults();

            if (result.isArray()) {
                json_result = result.getArray();
            }
            else {
                // Errors
                List<String> rest_errors = ens_rest_call.getErrors();
                if (rest_errors.size() > 0) {
                    throw new EnsemblRestIOException("Errors trying to get dbSNP version", rest_errors);
                }
            }
        }
        catch (IOException | InterruptedException | UnirestException e) {
            throw new EnsemblRestIOException("Errors while trying to get dbSNP version", e);
        }
        return json_result;
    }
}
