package uk.ac.ebi.spot.goci.curation.model.errors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

	private int errorCode;
	private String error;
	private String errorMessage;
	private String servicePath;
	private String timestamp;

}


