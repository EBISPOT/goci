package uk.ac.ebi.spot.goci.component;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.ebi.spot.goci.service.EnsemblRestService;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Laurent on 28/09/15.
 * @author Laurent
 * Class getting the dbSNP version from the Ensembl REST API
 */
public class EnsemblDbsnpVersion {

    private int dbsnpVersion = 0;
    private String species = "homo_sapiens";
    private String endpoint = "/info/variation/"+species+"/";
    private String source = "dbSNP";
    private ArrayList<String> rest_errors = new ArrayList<>();

    // JPA no-args constructor
    // Make the REST API call
    public EnsemblDbsnpVersion() {
        JSONArray ensembl_result = this.getSimpleRestCall();
        if (ensembl_result.length() > 0) {
            if (ensembl_result.getJSONObject(0).has("error")) {
                checkError(ensembl_result);
            }
            else {
                // Check if there are releases key
                for (int i = 0; i < ensembl_result.length(); ++i) {
                    JSONObject variant_source = ensembl_result.getJSONObject(i);
                    // Get version
                    if (variant_source.getString("name").equals(source) && variant_source.has("version")) {
                        dbsnpVersion = variant_source.getInt("version");
                    }
                }
            }
        }
        if (this.getDbsnpVersion() == 0) {
            rest_errors.add("dbSNP version not found");
        }
    }


    /**
     * Getter for the release version
     * @return the numeric release version
     */
    public int getDbsnpVersion() {
        return dbsnpVersion;
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
    private void checkError(JSONArray result) {
        if (result.getJSONObject(0).getString("error").contains("page not found")) {
            rest_errors.add("Web service '" + endpoint + "' not found or not working.");
        }
        else {
            rest_errors.add(result.getJSONObject(0).getString("error"));
        }
    }


    /**
     * Simple generic Ensembl REST API call method for array result.
     * @return the corresponding JSONArray
     */
    private JSONArray getSimpleRestCall () {
        EnsemblRestService ens_rest_call = new EnsemblRestService(endpoint, "", "filter="+source);
        JSONArray json_result = new JSONArray();
        try {

            ens_rest_call.getRestCall();
            JsonNode result = ens_rest_call.getRestResults();

            if (result.isArray()) {
                json_result = result.getArray();
            }
            else {
                // Errors
                ArrayList rest_errors = ens_rest_call.getErrors();
                if (rest_errors.size() > 0) {
                    json_result = new JSONArray("[{\"error\":\"1\"}]");
                    for (int i = 0; i < rest_errors.size(); ++i) {
                        this.rest_errors.add(rest_errors.get(i).toString());
                    }
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
