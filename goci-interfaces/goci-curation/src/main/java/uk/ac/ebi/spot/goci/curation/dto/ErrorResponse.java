package uk.ac.ebi.spot.goci.curation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

	private int errorCode;
	private String error;
	private String errorMessage;
	private String servicePath;
	private String timestamp;
	private Map<String, String> report;

	public static ErrorResponse basicResponse(HttpStatus httpStatus,
                                              RuntimeException ex,
                                              HttpServletRequest req,
                                              SimpleDateFormat dateFormat){

		return ErrorResponse.builder()
				.errorCode(httpStatus.value())
				.error(httpStatus.getReasonPhrase())
				.errorMessage(ex.getMessage())
				.servicePath(req.getRequestURL().toString())
				.timestamp(dateFormat.format(new Date())).build();
	}

	public static ErrorResponse hibernateValidationResponse(ErrorResponse errorResponse,
                                                            BindingResult bindingResult){

		Map<String, String> errorMap = new HashMap<>();
		bindingResult.getFieldErrors().forEach(field -> errorMap.put(field.getField(), field.getDefaultMessage()));
		errorResponse.setReport(errorMap);
		return errorResponse;
	}

}


