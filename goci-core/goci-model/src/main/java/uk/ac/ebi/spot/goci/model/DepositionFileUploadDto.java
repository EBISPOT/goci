package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionFileUploadDto {
    private String fileUploadId;
    private String fileName;
    private Long fileSize;
    private String status;
    private List<String> errors;
    private List<DepositionSummaryStatsStatusDto> summaryStatsStatuses;
}
