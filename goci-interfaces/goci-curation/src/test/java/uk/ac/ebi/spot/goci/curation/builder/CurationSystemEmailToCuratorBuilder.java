package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmailToCurator;

/**
 * Created by emma on 09/03/2016.
 *
 * @author emma
 *         <p>
 *         Builder for email class
 */
public class CurationSystemEmailToCuratorBuilder {

    private CurationSystemEmailToCurator curationSystemEmailToCurator = new CurationSystemEmailToCurator();

    public CurationSystemEmailToCuratorBuilder setTo(String to) {
        curationSystemEmailToCurator.setTo(to);
        return this;
    }

    public CurationSystemEmailToCuratorBuilder setFrom(String from) {
        curationSystemEmailToCurator.setFrom(from);
        return this;
    }

    public CurationSystemEmailToCuratorBuilder setLink(String link) {
        curationSystemEmailToCurator.setLink(link);
        return this;
    }

    public CurationSystemEmailToCuratorBuilder setBody(String body) {
        curationSystemEmailToCurator.setBody(body);
        return this;
    }

    public CurationSystemEmailToCuratorBuilder setSubject(String subject) {
        curationSystemEmailToCurator.setSubject(subject);
        return this;
    }

    public CurationSystemEmailToCurator buiid() {
        return curationSystemEmailToCurator;
    }
}