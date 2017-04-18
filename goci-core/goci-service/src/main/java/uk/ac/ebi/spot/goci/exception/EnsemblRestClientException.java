package uk.ac.ebi.spot.goci.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.spot.goci.model.RestResponseResult;

/**
 * Created by cinzia on 10/04/2017.
 */
public class EnsemblRestClientException extends RestClientException {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }


    private RestResponseResult ensemblLookup = new RestResponseResult();

    public EnsemblRestClientException(String msg) {
        super(msg);
    }


    public EnsemblRestClientException(String msg, Throwable ex) {
        super(msg,ex);
    }

    private String getMessage(String rawText, HttpHeaders headers ) {

        String message = "";
        if (headers.containsKey("Content-Type")) {
            // application/json / text/plain / text/html
            String contentTypeHeader = headers.get("Content-Type").get(0);
            switch (contentTypeHeader) {
                case "text/plain":  message = rawText;
                    break;
                case "application/json":
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.convertValue(rawText, JsonNode.class);
                    if (jsonNode.getObject()!= null) {
                         for (String key :jsonNode.getObject().keySet()) {
                            message = message.concat(jsonNode.getObject().get(key).toString().concat(";"));
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        if (message.isEmpty()) {
            message="I can't convert the error server message";
        }

        return message;
    }

    private void setTooManyRequestsDelay(ClientHttpResponse response, HttpHeaders headers) {
        getLog().debug("Response 429");
        ensemblLookup.setWaitSeconds(2);
        if (headers.containsKey("Retry-After")) {
            if (!headers.get("Retry-After").isEmpty()) {
                String retryHeader = headers.get("Retry-After").get(0);
                long waitSeconds = Long.valueOf(retryHeader);
                ensemblLookup.setWaitSeconds(waitSeconds);
            }
        }
    }

    // Tested for 400 / 429 / 503
    public EnsemblRestClientException(String msg, RestClientException restClientException, ClientHttpResponse response) {
        super(msg);

        HttpHeaders headers = response.getHeaders();
        int statusCode = ((HttpClientErrorException) restClientException).getRawStatusCode();
        ensemblLookup.setStatus(statusCode);
        String rawBody = ((HttpClientErrorException) restClientException).getResponseBodyAsString();
        String body = getMessage(rawBody, headers);
        ensemblLookup.setError(body);
        switch (statusCode) {
            case 429: setTooManyRequestsDelay(response, headers);
                break;
            case 503: ensemblLookup.setError("Ensembl is down. Contact the admin.");
                break;
            default:
                break;
        }
        getLog().debug(body);
    }

    public RestResponseResult getEnsemblLookup() { return this.ensemblLookup;}
}

