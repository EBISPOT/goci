package uk.ac.ebi.spot.goci.component;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.ebi.spot.goci.service.EnsemblRestService;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Laurent on 22/09/15.
 * @author Laurent
 * Class getting the Ensembl Release version from the Ensembl REST API
 */
public class EnsemblRelease {

    private int releaseVersion;
    private String endpoint = "/info/data/";
    private ArrayList<String> rest_errors = new ArrayList<>();

    // JPA no-args constructor
    // Make the REST API call
    public EnsemblRelease() {
        JSONObject ensembl_result = this.getSimpleRestCall();
        if (ensembl_result.length() > 0) {
            if (ensembl_result.has("error")) {
                checkError(ensembl_result);
            }
            // Check if there are releases key
            else if (ensembl_result.has("releases")) {
                // Check if there are releases data
                JSONArray releases = ensembl_result.getJSONArray("releases");
                if (releases.length() > 0) {
                    releaseVersion = releases.getInt(0);
                }
            }
        }
        else {
            rest_errors.add("Release version not found");
        }
    }


    /**
     * Getter for the release version
     * @return the numeric release version
     */
    public int getReleaseVersion() {
        return releaseVersion;
    }


    /**
     * Getter for the list of REST API error messages
     * @return List of strings.
     */
    public ArrayList<String> getRestErrors() {
        return rest_errors;
    }


    /**
     * Check the type of error returned by the REST web service JSON output
     * @param result The JSONObject result
     */
    private void checkError(JSONObject result) {
        if (result.getString("error").contains("page not found")) {
            rest_errors.add("Web service '" + endpoint + "' not found or not working.");
        }
        else {
            rest_errors.add(result.getString("error"));
        }
    }


    /**
     * Simple generic Ensembl REST API call method.
     * @return the corresponding JSONObject
     */
    private JSONObject getSimpleRestCall () {
        EnsemblRestService ens_rest_call = new EnsemblRestService(endpoint, "");
        JSONObject json_result = new JSONObject();
        try {
            ens_rest_call.getRestCall();
            json_result = ens_rest_call.getRestResults().getObject();

            // Errors
            ArrayList rest_errors = ens_rest_call.getErrors();
            if (rest_errors.size() > 0) {
                for (int i = 0; i < rest_errors.size(); ++i) {
                    this.rest_errors.add(rest_errors.get(i).toString());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (UnirestException e) {
            e.printStackTrace();
        }
        return json_result;
    }
}
