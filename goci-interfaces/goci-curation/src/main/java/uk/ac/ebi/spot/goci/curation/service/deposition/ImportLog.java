package uk.ac.ebi.spot.goci.curation.service.deposition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImportLog {

    public final static int NA = 0;

    public final static int SUCCESS = 1;

    public final static int FAIL = -1;

    private List<String> errorList;

    private Map<Integer, ImportLogStep> steps;

    private Map<String, Integer> stepOutcomes;

    public ImportLog() {
        errorList = new ArrayList<>();
        steps = new LinkedHashMap<>();
        stepOutcomes = new LinkedHashMap<>();
    }

    public ImportLogStep addStep(ImportLogStep importLogStep) {
        int next = steps.size() + 1;
        this.steps.put(next, importLogStep);
        this.stepOutcomes.put(importLogStep.getId(), NA);
        return importLogStep;
    }

    public void addErrors(List<String> errors, String prefix) {
        if (errors != null) {
            for (String error : errors) {
                this.addError(error, prefix);
            }
        }
    }

    public void addError(String error, String prefix) {
        errorList.add(prefix + " | " + error);
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void updateStatus(String stepId, int status) {
        this.stepOutcomes.put(stepId, status);
    }

    public String pretty() {
        StringBuffer stringBuffer = new StringBuffer();
        boolean overall = true;
        for (int value : stepOutcomes.values()) {
            if (value == FAIL) {
                overall = false;
                break;
            }
        }
        if (overall) {
            stringBuffer.append("=== GENERAL OUTCOME: SUCCESS\n");
        } else {
            stringBuffer.append("=== GENERAL OUTCOME: FAIL\n");
        }
        for (int stepNo : steps.keySet()) {
            ImportLogStep importLogStep = steps.get(stepNo);
            int outcome = stepOutcomes.get(importLogStep.getId());

            stringBuffer.append(stepNo).append("[").append(outcomeAsString(outcome)).append("] :: ").append(importLogStep.toString());
            stringBuffer.append("\n");
        }
        if (!errorList.isEmpty()) {
            stringBuffer.append("** Errors: **\n");
            for (String error : errorList) {
                stringBuffer.append(error).append("\n");
            }
        }

        return "\n" + stringBuffer.toString().trim();
    }

    private String outcomeAsString(int outcome) {
        if (outcome == 0) {
            return "N/A";
        } else {
            if (outcome == 1) {
                return "Success";
            } else {
                return "Fail";
            }
        }
    }
}
