package uk.ac.ebi.spot.goci.model.projection;

import java.util.Date;

public interface StudySearchProjection {

    String getAccessionId();
    Long getStudyId();
    String getAuthor();
    String getTitle();
    Date getDate();
    String getPubmedId();
    String getPublication();
    String getDiseaseTrait();
    String getCuratorLastName();
    String getCurationStatus();
    String getTrait();
    String getTextNote();

}
