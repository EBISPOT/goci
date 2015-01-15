package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class StudyDocument extends Document<Study> {
    @Field private String pubmedId;
    @Field private String title;
    @Field private String author;
    @Field private String publication;
    @Field private String resourcename;

    @Field private String trait;
    @Field("traitUri") private Collection<String> traitUris;

    public StudyDocument(Study study) {
        super(study);
        this.pubmedId = study.getPubmedId();
        this.title = study.getTitle();
        this.author = study.getAuthor();
        this.publication = study.getPublication();
        this.resourcename = study.getClass().getSimpleName();

        this.trait = study.getDiseaseTrait().getTrait();
        this.traitUris = new ArrayList<>();
        study.getEfoTraits().forEach(efoTrait -> traitUris.add(efoTrait.getUri()));
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

    public String getTrait() {
        return trait;
    }

    public Collection<String> getTraitUris() {
        return traitUris;
    }
}
