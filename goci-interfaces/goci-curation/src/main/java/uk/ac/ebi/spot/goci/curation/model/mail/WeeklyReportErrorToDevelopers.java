package uk.ac.ebi.spot.goci.curation.model.mail;

import uk.ac.ebi.spot.goci.model.GenericEmail;

public class WeeklyReportErrorToDevelopers extends GenericEmail {

    public void createWeeklyReportEmail(String body) {
        this.setBody("Error: \n" + body);
        this.setSubject("Unable to create weekly report email");
    }
}
