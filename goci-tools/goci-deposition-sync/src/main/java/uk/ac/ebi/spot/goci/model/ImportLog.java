package uk.ac.ebi.spot.goci.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportLog {

    private List<String> submissions;

    private Map<String, String> pmids;

    private Map<String, List<String>> errorMap;

    public ImportLog() {
        this.submissions = new ArrayList<>();
        pmids = new HashMap<>();
        errorMap = new HashMap<>();
    }

    public void addEntry(String submissionId, String pmid, List<String> errors) {
        submissions.add(submissionId);
        pmids.put(submissionId, pmid);
        if (!errors.isEmpty()) {
            errorMap.put(submissionId, errors);
        }
    }

    public String getLog() {
        if (submissions.isEmpty()) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("Auto-imported " + submissions.size() + " SUMSTATS only submissions.").append("\n");

        for (String submissionId : submissions) {
            sb.append(" - Submission [" + submissionId + "] | PMID [" + pmids.get(submissionId) + "] | Outcome: " + !errorMap.containsKey(submissionId) + "\n");
            if (errorMap.containsKey(submissionId)) {
                List<String> errors = errorMap.get(submissionId);
                sb.append(" -- Found " + errors.size() + " errors:\n");
                for (String error : errors) {
                    sb.append(" --- " + error + "\n");
                }
            }
        }

        return sb.toString();
    }
}
