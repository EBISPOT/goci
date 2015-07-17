package uk.ac.ebi.spot.goci.curation.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Laurent on 15/07/15.
 * Class running the Ensembl REST API calls
 */
@Service
public class EnsemblRestService {

    private static String server = "http://rest.ensembl.org";

    private String rest_endpoint;
    private String rest_data;
    private String rest_parameters = "";

    private JSONObject rest_results;
    private ArrayList<String> rest_errors = new ArrayList<String>();

    // Default constructor
    public EnsemblRestService () {
    }


    /* Simple constructor with endpoint and data
     * @params rest_endpoint the endpoint part of the URL
     * @params rest_data the data/id/symbol we want to query
     */
    public EnsemblRestService (String rest_endpoint, String rest_data) {
        this.rest_endpoint = rest_endpoint;
        this.rest_data = rest_data;
    }


    /* More complex contructor with extra parameters
     * @params rest_endpoint the endpoint part of the URL
     * @params rest_data the data/id/symbol we want to query
     * @params rest_parameters the extra parameters to add at the end of the REST call url
     */
    public EnsemblRestService (String rest_endpoint, String rest_data, String rest_parameters) {
        this.rest_endpoint = rest_endpoint;
        this.rest_data = rest_data;
        this.rest_parameters = rest_parameters;
    }


    /* Run the Ensembl REST API call, using the parameters from the constructor */
    public void getRestCall() throws IOException {

        URL url = null;
        try {
            if (this.rest_parameters != "") {
                Matcher matcher = Pattern.compile("^\\?").matcher(this.rest_parameters);
                if (!matcher.matches()) {
                    this.rest_parameters = '?' + this.rest_parameters;
                }
            }
            url = new URL(server + this.rest_endpoint + this.rest_data + this.rest_parameters);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        URLConnection connection = (url != null) ? url.openConnection() : null;
        HttpURLConnection httpConnection = (HttpURLConnection)connection;

        httpConnection.setRequestMethod("GET"); // Method by default
        httpConnection.setRequestProperty("Content-Type", "application/json"); // Default output format


        InputStream response = connection.getInputStream();
        int responseCode = httpConnection.getResponseCode();

        if(responseCode != 200) {
            throw new RuntimeException("Response code was not 200. Detected response was "+responseCode);
        }

        Reader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            String json_string = builder.toString();
            // JSONObject issue id the JSON string doesn't start with the "{" character (pb with overlap_region REST endpoint)
            if (!json_string.substring(0, 1).matches("\\{")) {
                json_string = "{ \"array\" : " + json_string + "}";
            }
            this.rest_results = new JSONObject(json_string);
        }
        finally {
            if (reader != null) try {
                reader.close();
            } catch (IOException logOrIgnore) {
                logOrIgnore.printStackTrace();
            }
        }
    }


    /* Return the results of the Ensembl REST API call
     * @return JSONObject containing the returned JSON data
     */
    public JSONObject getRestResults () {
        return this.rest_results;
    }


    /* Return the list of error messages from the Ensembl REST API call
     * @return List of error messages
     */
    public ArrayList<String> getErrors() {
        return this.rest_errors;
    }
}