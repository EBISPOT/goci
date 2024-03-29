package uk.ac.ebi.spot.goci.util;

import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.spot.goci.model.deposition.DepositionAssociationDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;
import uk.ac.ebi.spot.goci.model.deposition.Submission;

import java.net.URI;

public class DepositionUtil {

    public static Submission.SubmissionType getSubmissionType(DepositionSubmission submission) {
        if (submission.getBodyOfWork() != null && submission.getPublication() == null) {
            return Submission.SubmissionType.PRE_PUBLISHED;
        } else if (submission.getBodyOfWork() == null && submission.getPublication() == null) {
            return Submission.SubmissionType.UNKNOWN;
        } else if (submission.getPublication() != null) {
            String publicationStatus = submission.getPublication().getStatus();
            boolean hasSumStats = false;
            boolean hasMetadata = false;
            boolean hasAssociations = false;
            if (publicationStatus.equals("UNDER_SUBMISSION")) {
                hasMetadata = true;
            } else if (publicationStatus.equals("UNDER_SUMMARY_STATS_SUBMISSION")) {
                hasSumStats = true;
            }
            if (submission.getStudies() != null) {
                for (DepositionStudyDto studyDto : submission.getStudies()) {
                    if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("") &&
                            !studyDto.getSummaryStatisticsFile().equals("NR")) {
                        hasSumStats = true;
                    }
                }
            }
            if (submission.getAssociations() != null) {
                for (DepositionAssociationDto associationDto : submission.getAssociations()) {
                    if (associationDto.getStudyTag() != null) {
                        hasAssociations = true;
                    }
                }
            }
            if (hasMetadata && hasSumStats && hasAssociations) {
                return Submission.SubmissionType.METADATA_AND_SUM_STATS_AND_TOP_ASSOCIATIONS;
            }
            if (hasMetadata && hasSumStats && !hasAssociations) {
                return Submission.SubmissionType.METADATA_AND_SUM_STATS;
            }
            if (hasMetadata && !hasSumStats && hasAssociations) {
                return Submission.SubmissionType.METADATA_AND_TOP_ASSOCIATIONS;
            }
            if (hasMetadata && !hasSumStats && !hasAssociations) {
                return Submission.SubmissionType.METADATA;
            }
            if (!hasMetadata && hasSumStats && !hasAssociations) {
                return Submission.SubmissionType.SUM_STATS;
            }
        }
        return Submission.SubmissionType.UNKNOWN;
    }

    public static URI buildUrl(String uri, Pageable pageable){
        return buildUrl(uri, pageable, "");
    }

    public static URI buildUrl(String uri, Pageable pageable, String status){
        UriComponentsBuilder targetUrl = UriComponentsBuilder.fromUriString(uri)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        targetUrl = (status.isEmpty()) ? targetUrl : targetUrl.queryParam("status", status);
        return targetUrl.build().encode().toUri();
    }

}
