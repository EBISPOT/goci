package uk.ac.ebi.spot.goci.curation.service.deposition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImportLog {

    public final static int NA = 0;

    public final static int SUCCESS = 1;

    public final static int SUCCESS_WITH_WARNINGS = 2;

    public final static int FAIL = -1;

    private List<String> errorList;

    private List<String> warnings;

    private Map<Integer, ImportLogStep> steps;

    private Map<String, Integer> stepOutcomes;

    public ImportLog() {
        errorList = new ArrayList<>();
        warnings = new ArrayList<>();
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

    public void addWarnings(List<String> warningList, String prefix) {
        if (warningList != null) {
            for (String warning : warningList) {
                this.addWarning(warning, prefix);
            }
        }
    }

    public void addError(String error, String prefix) {
        errorList.add(prefix + " | " + error);
    }

    public void addWarning(String warning, String prefix) {
        warnings.add(prefix + " | " + warning);
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void updateStatus(String stepId, int status) {
        this.stepOutcomes.put(stepId, status);
    }

    public String prettyShort() {
        StringBuffer stringBuffer = new StringBuffer();
        String separator = "<br>";
        boolean overall = true;
        boolean hasWarnings = false;
        for (int value : stepOutcomes.values()) {
            if (value == FAIL) {
                overall = false;
                break;
            }
            if (value == SUCCESS_WITH_WARNINGS) {
                hasWarnings = true;
            }
        }
        if (overall) {
            String txt = hasWarnings ? "SUCCESS_WITH_WARNINGS" : "SUCCESS";
            stringBuffer.append("=== GENERAL OUTCOME: ").append(txt).append(separator);
        } else {
            stringBuffer.append("=== GENERAL OUTCOME: FAIL").append(separator);
        }
        if (!errorList.isEmpty()) {
            stringBuffer.append("** Errors: **").append(separator);
            for (String error : errorList) {
                stringBuffer.append(error).append(separator);
            }
        }

        return separator + stringBuffer.toString().trim();
    }

    public String pretty(boolean web) {
        StringBuffer stringBuffer = new StringBuffer();
        String separator = web ? "<br>" : "\n";
        boolean overall = true;
        boolean hasWarnings = false;
        for (int value : stepOutcomes.values()) {
            if (value == FAIL) {
                overall = false;
                break;
            }
            if (value == SUCCESS_WITH_WARNINGS) {
                hasWarnings = true;
            }
        }
        if (overall) {
            String txt = hasWarnings ? "SUCCESS_WITH_WARNINGS" : "SUCCESS";
            stringBuffer.append("=== GENERAL OUTCOME: ").append(txt).append(separator);
        } else {
            stringBuffer.append("=== GENERAL OUTCOME: FAIL").append(separator);
        }
        for (int stepNo : steps.keySet()) {
            ImportLogStep importLogStep = steps.get(stepNo);
            int outcome = stepOutcomes.get(importLogStep.getId());

            stringBuffer.append(stepNo).append("[").append(outcomeAsString(outcome)).append("] :: ").append(importLogStep.toString());
            stringBuffer.append(separator);
        }
        if (!errorList.isEmpty()) {
            stringBuffer.append("** Errors: **").append(separator);
            for (String error : errorList) {
                stringBuffer.append(error).append(separator);
            }
        }
        if (!warnings.isEmpty()) {
            stringBuffer.append("** Warnings: **").append(separator);
            for (String warning : warnings) {
                stringBuffer.append(warning).append(separator);
            }
        }

        return separator + stringBuffer.toString().trim();
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
