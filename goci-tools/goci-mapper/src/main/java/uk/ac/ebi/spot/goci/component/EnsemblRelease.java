package uk.ac.ebi.spot.goci.component;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.EnsemblRestIOException;
import uk.ac.ebi.spot.goci.service.EnsemblRestService;

import java.io.IOException;
import java.util.List;

/**
 * Created by Laurent on 22/09/15.
 *
 * @author Laurent
 *         <p>
 *         Class getting the Ensembl Release version from the Ensembl REST API
 */
@Service
public class EnsemblRelease {

    private String endpoint = "/info/data/";

    /**
     * Getter for the release version
     *
     * @return the numeric release version
     */
    public int getReleaseVersion() throws EnsemblRestIOException {

        JSONObject ensembl_result = this.getSimpleRestCall();
        if (ensembl_result.length() > 0) {

            // Check if there are releases key
            if (ensembl_result.has("releases")) {
                // Check if there are releases data
                JSONArray releases = ensembl_result.getJSONArray("releases");
                if (releases.length() > 0) {
                    return releases.getInt(0);
                }
                else {
                    throw new EnsemblRestIOException("Release information field empty");
                }
            }
            else {
                if (ensembl_result.has("error")) {
                    throw new EnsemblRestIOException(checkError(ensembl_result));
                }
                else {
                    throw new EnsemblRestIOException("No release or error information found while trying to check Ensembl release");
                }
            }
        }

        else {
            throw new EnsemblRestIOException("Empty response body found while trying to check Ensembl release");
        }
    }


    /**
     * Check the type of error returned by the REST web service JSON output
     *
     * @param result The JSONObject result
     */
    private String checkError(JSONObject result) {
        if (result.getString("error").contains("page not found")) {
            return "Web service '" + endpoint + "' not found or not working.";
        }
        else {
            return result.getString("error");
        }
    }


    /**
     * Simple generic Ensembl REST API call method.
     *
     * @return the corresponding JSONObject
     */
    private JSONObject getSimpleRestCall() throws EnsemblRestIOException {
        EnsemblRestService ens_rest_call = new EnsemblRestService(endpoint, "");
        JSONObject json_result = new JSONObject();
        try {
            ens_rest_call.getRestCall();
            json_result = ens_rest_call.getRestResults().getObject();

            // Errors
            List<String> rest_errors = ens_rest_call.getErrors();
            if (rest_errors.size() > 0) {
                throw new EnsemblRestIOException("Errors trying to get release information", rest_errors);
            }
        }
        catch (IOException | InterruptedException | UnirestException e) {
            throw new EnsemblRestIOException("Errors while trying to get release information", e);
        }
        return json_result;
    }
}
