package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.StudyExtension;

public class StudyExtensionBuilder {

    private StudyExtension studyExtension = new StudyExtension();

    public StudyExtensionBuilder setStatisticalModel(String statisticalModel) {
        studyExtension.setStatisticalModel(statisticalModel);
        return this;
    }

    public StudyExtensionBuilder setCohort(String cohort) {
        studyExtension.setCohort(cohort);
        return this;
    }

    public StudyExtensionBuilder setCohortSpecificReference(String cohortSpecificReference) {
        studyExtension.setCohortSpecificReference(cohortSpecificReference);
        return this;
    }

    public StudyExtensionBuilder setSummaryStatisticsFile(String summaryStatisticsFile) {
        studyExtension.setSummaryStatisticsFile(summaryStatisticsFile);
        return this;
    }

    public StudyExtensionBuilder setSummaryStatisticsAssembly(String summaryStatisticsAssembly) {
        studyExtension.setSummaryStatisticsAssembly(summaryStatisticsAssembly);
        return this;
    }

    public StudyExtensionBuilder setStudyDescription(String studyDescription) {
        studyExtension.setStudyDescription(studyDescription);
        return this;
    }

    public StudyExtension build() {
        return studyExtension;
    }


}
