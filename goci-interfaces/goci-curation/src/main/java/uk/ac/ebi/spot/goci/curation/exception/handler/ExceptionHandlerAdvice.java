package uk.ac.ebi.spot.goci.curation.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.ac.ebi.spot.goci.curation.dto.ErrorResponse;
import uk.ac.ebi.spot.goci.curation.exception.FileValidationException;
import uk.ac.ebi.spot.goci.curation.exception.ResourceNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        ErrorResponse error = ErrorResponse.basicResponse(HttpStatus.NOT_FOUND, ex, req, dateFormat);
        log.error(error.toString());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileValidationException ex, HttpServletRequest req) {
        ErrorResponse error = ErrorResponse.basicResponse(HttpStatus.BAD_REQUEST, ex, req, dateFormat);
        ErrorResponse response = ErrorResponse.hibernateValidationResponse(error, ex.getBindingResult());
        log.error(response.toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
