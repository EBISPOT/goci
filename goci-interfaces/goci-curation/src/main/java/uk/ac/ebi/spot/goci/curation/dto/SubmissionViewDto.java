package uk.ac.ebi.spot.goci.curation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.goci.model.deposition.Submission;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionPageInfo;

import java.util.Map;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionViewDto {

    private Map<String, Submission> submissionList;

    private DepositionPageInfo page;

    private Integer currentIndex = 0;
    private Integer beginIndex = 0;
    private Integer endIndex = 0;

    public void setPageIndexes(){
        int pageNumber = (this.page == null) ? 0 : this.page.getNumber();
        int totalPages = (this.page == null) ? 0 : this.page.getTotalPages();

        this.currentIndex = pageNumber + 1;
        this.beginIndex = Math.max(1, this.currentIndex - 4);
        this.endIndex = Math.min(beginIndex + 7, totalPages);
    }


        /*
            let currentIndex = pageData.number + 1;
        let beginIndex = Math.max(1, page - 4);
        let endIndex = Math.min(beginIndex + 7, totalPages);
     */

}
