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

    private String subject;

    private String body;

    public CurationSystemEmail() {
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

    public void addToBody(String textToAddToBody) {
        String newBody = getBody() + textToAddToBody;
        setBody(newBody);
    }
}
