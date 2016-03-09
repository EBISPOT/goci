package uk.ac.ebi.spot.goci.curation.model.mail;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by emma on 08/03/2016.
 *
 * @author emma
 *         <p>
 *         Class to represent emails sent from the curation system
 */
public abstract class CurationSystemEmail {

    @Value("${mail.from}")
    private String from;

    @Value("${mail.link}")
    private String link;

    private String subject;

    private String body;

    private String to;

    public CurationSystemEmail() {
    }

    public CurationSystemEmail(String subject, String body, String to) {
        this.subject = subject;
        this.body = body;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getLink() {
        return link;
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

    // This will be set in subclasses
    abstract void setTo(String to);
}
