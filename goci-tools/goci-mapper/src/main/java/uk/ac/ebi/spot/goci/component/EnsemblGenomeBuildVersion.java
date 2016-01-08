package uk.ac.ebi.spot.goci.component;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.EnsemblRestIOException;
import uk.ac.ebi.spot.goci.service.EnsemblRestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Laurent on 10/11/2015.
 *
 * @author Laurent
 *         <p>
 *         Class getting the Ensembl Genome build version from the Ensembl REST API
 */
@Service
public class EnsemblGenomeBuildVersion {

    private String species = "homo_sapiens";
    private String endpoint = "/info/assembly/" + species + "/";
    private ArrayList<String> rest_errors = new ArrayList<>();

    /**
     * Getter for the genome build version
     *
     * @return the genome build version
     */
    public String getGenomeBuildVersion() throws EnsemblRestIOException {


        JSONObject ensembl_result = this.getSimpleRestCall();
        if (ensembl_result.length() > 0) {
            if (ensembl_result.has("error")) {
                throw new EnsemblRestIOException(checkError(ensembl_result));
            }
            // Check if there are releases key
            else if (ensembl_result.has("assembly_name")) {
                // Check if there are releases data
                return ensembl_result.getString("assembly_name");
            }
            else {
                throw new EnsemblRestIOException(
                        "No release or error information found while trying to check Ensembl genome build version");
            }
        }
        else {
            throw new EnsemblRestIOException(
                    "Empty response body found while trying to check Ensembl genome build version");
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
                throw new EnsemblRestIOException("Errors trying to get genome build information", rest_errors);
            }
        }
        catch (IOException | InterruptedException | UnirestException e) {
            throw new EnsemblRestIOException("Errors trying to get genome build information", rest_errors);
        }
        return json_result;
    }

}
