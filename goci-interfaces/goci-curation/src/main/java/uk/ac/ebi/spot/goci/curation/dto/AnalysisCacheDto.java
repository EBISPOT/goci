package uk.ac.ebi.spot.goci.curation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisCacheDto {

    private String uniqueId;

    private List<AnalysisDTO> analysisResult;

}
