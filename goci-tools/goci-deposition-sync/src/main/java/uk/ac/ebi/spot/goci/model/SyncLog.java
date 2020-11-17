package uk.ac.ebi.spot.goci.model;

import java.util.HashMap;
import java.util.Map;

public class SyncLog {

    private int noNewCreated;

    private int noPublished;

    private int noSumStats;

    private int noRetired;

    private int noErrors;

    private Map<String, String> errorMap;

    private Map<String, String> newMap;

    private Map<String, String> retiredMap;

    public SyncLog() {
        noNewCreated = 0;
        noPublished = 0;
        noSumStats = 0;
        noRetired = 0;
        noErrors = 0;

        errorMap = new HashMap<>();
        newMap = new HashMap<>();
        retiredMap = new HashMap<>();
    }

    public void addError(String key, String message) {
        noErrors++;
        this.errorMap.put(key, message);
    }

    public void addRetired(String pubmedId, String status) {
        noRetired++;
        retiredMap.put(pubmedId, status);
    }

    public void addNewPublication(String pubmedId, String status) {
        noNewCreated++;
        newMap.put(pubmedId, status);
    }

    public void addPublishedPublication() {
        noPublished++;
    }

    public void addSSPublication() {
        noSumStats++;
    }

    public String getLog() {
        StringBuffer sb = new StringBuffer();
        sb.append(" - Errors: " + noErrors + "\n");
        if (noErrors != 0) {
            if (errorMap.containsKey("PROCESS")) {
                sb.append(errorMap.get("PROCESS") + "\n");
                sb.append("-----------------------------\n");
            }

            for (String key : errorMap.keySet()) {
                if (!key.equalsIgnoreCase("PROCESS")) {
                    sb.append(" -- [" + key + "]: " + errorMap.get(key) + "\n");
                }
            }
        }
        sb.append("-----------------------------\n");
        sb.append(" - Publications retired: " + noRetired + "\n");
        if (!retiredMap.isEmpty()) {
            for (String key : retiredMap.keySet()) {
                sb.append(" -- [" + key + "]: " + retiredMap.get(key) + "\n");
            }

        }
        sb.append(" - New publications: " + noNewCreated + "\n");
        if (!newMap.isEmpty()) {
            for (String key : newMap.keySet()) {
                sb.append(" -- [" + key + "]: " + newMap.get(key) + "\n");
            }

        }
        sb.append(" - Published publications: " + noPublished + "\n");
        sb.append(" - Publications published with summary stats: " + noSumStats + "\n");

        return sb.toString().trim();
    }
}
