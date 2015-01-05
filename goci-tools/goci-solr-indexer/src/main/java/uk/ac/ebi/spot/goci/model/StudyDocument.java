package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
@SolrDocument(solrCoreName = "gwas")
public class StudyDocument {
    @Id @Field
    private String id;
    @Field
    private String pubmedId;
    @Field
    private String title;
    @Field
    private String author;
    @Field
    private String publication;
    @Field
    private String resourcename;

    public StudyDocument(Study study) {
        this.id = "study_".concat(study.getId().toString());
        this.pubmedId = study.getPubmedId();
        this.title = study.getTitle();
        this.author = study.getAuthor();
        this.publication = study.getPublication();
        this.resourcename = study.getClass().getSimpleName();
    }

    public String getId() {
        return id;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublication() {
        return publication;
    }

    public String getResourcename() {
        return resourcename;
    }
}
