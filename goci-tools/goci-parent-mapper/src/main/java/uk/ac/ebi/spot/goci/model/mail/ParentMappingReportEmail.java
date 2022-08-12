package uk.ac.ebi.spot.goci.model.mail;

import uk.ac.ebi.spot.goci.model.GenericEmail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ParentMappingReportEmail extends GenericEmail {

    public void createBody(List<String> unmappedTerms) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = df.format(date);
        this.setSubject("Data Release: EFO Download File Generation Report, " + today);

        if(unmappedTerms.size() != 0) {
            this.setBody("The following " + unmappedTerms.size()+ " terms were not mapped on " + today + ":\n");

            for (String s : unmappedTerms) {
                this.addToBody(s + "\n");
            }
        }
        else {
            this.setBody("Parent mapping successful for all terms on " + today + "\n");
        }
    }
}
