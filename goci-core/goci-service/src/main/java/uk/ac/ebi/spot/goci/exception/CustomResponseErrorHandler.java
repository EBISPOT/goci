package uk.ac.ebi.spot.goci.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

/**
 * Created by cinzia on 10/04/2017.
 */
@ControllerAdvice

public class CustomResponseErrorHandler implements ResponseErrorHandler {

    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    public boolean hasError(ClientHttpResponse response) throws IOException {
        return errorHandler.hasError(response);
    }

    public void handleError(ClientHttpResponse response) throws IOException {
        try {
            errorHandler.handleError(response);
        } catch (RestClientException restClientException) {
            throw new EnsemblRestClientException("Custom RestTemplate", restClientException,response);
        }
    }
}