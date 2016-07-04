package uk.ac.ebi.spot.goci.model.mail;

import uk.ac.ebi.spot.goci.model.PublishedStudy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by dwelter on 29/06/16.
 */
public class ReleaseSummaryEmail {

    private String to;

    private String from;

    private String subject;

    private String body;

    public ReleaseSummaryEmail() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void addToBody(String textToAddToBody) {
        String newBody = getBody() + textToAddToBody;
        setBody(newBody);
    }

    public void createBody(List<PublishedStudy> studies){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = df.format(date);
        this.setSubject("Studies added to the Solr index on " + today);

        if(studies.size() != 0) {
            this.setBody("The following studies were added to the Solr index on " + today + "\n");

            for (PublishedStudy s : studies) {
                StringBuilder line = new StringBuilder();

                line.append(s.getAuthor());
                line.append(" (");
                line.append(s.getPubmedId());
                line.append(") - ");
                line.append(s.getTitle());
                line.append(" (");
                line.append(s.getPublicationDate());
                line.append(", ");
                line.append(s.getJournal());
                line.append("); ");
                line.append(s.getAssociationCount());
                line.append(" associations.\n");
                line.append("\n");

                this.addToBody(line.toString());
            }
        }
        else {
            this.setBody("No studies were added to the Solr index on " + today + "\n");
        }
    }
}
