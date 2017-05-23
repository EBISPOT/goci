package uk.ac.ebi.spot.goci.model;

/**
 * Created by cinzia on 03/05/2017.
 * Abstract class to use for the specific email. (Curation/DataRelease)
 * Refactoring Email GOCI
 */

public abstract class GenericEmail{

    private String to;

    private String from;

    private String link;

    private String subject;

    private String body;

    public GenericEmail() {
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void addToBody(String textToAddToBody) {
        String newBody = getBody() + textToAddToBody;
        setBody(newBody);
    }
}

