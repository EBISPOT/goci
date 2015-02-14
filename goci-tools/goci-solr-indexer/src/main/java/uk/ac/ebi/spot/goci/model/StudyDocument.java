package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class StudyDocument extends EmbeddableDocument<Study> {
    // basic study information
    @Field private String pubmedId;
    @Field private String title;
    @Field private String author;
    @Field private String publication;
    @Field private String publicationDate;
    @Field private String catalogAddedDate;

    @Field private String platform;
    @Field private Boolean cnv;

    @Field private String initialSampleDescription;
    @Field private String replicateSampleDescription;

    @Field private int associationCount;

    // embedded association info

    // embedded trait info

    // embedded snp info

    // embedded gene info

    // genomic info from snp, association


    public StudyDocument(Study study) {
        super(study);
        this.pubmedId = study.getPubmedId();
        this.title = study.getTitle();
        this.author = study.getAuthor();
        this.publication = study.getPublication();

        this.platform = study.getPlatform();
        this.cnv = study.getCnv();

        this.initialSampleDescription = study.getInitialSampleSize();
        this.replicateSampleDescription = study.getReplicateSampleSize();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (study.getStudyDate() != null) {
            this.publicationDate = df.format(study.getStudyDate());
        }
        if (study.getHousekeeping().getPublishDate() != null) {
            this.catalogAddedDate = df.format(study.getHousekeeping().getPublishDate());
        }
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

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getCatalogAddedDate() {
        return catalogAddedDate;
    }

    public String getPlatform() {
        return platform;
    }

    public Boolean getCnv() {
        return cnv;
    }

    public String getInitialSampleDescription() {
        return initialSampleDescription;
    }

    public String getReplicateSampleDescription() {
        return replicateSampleDescription;
    }

    public int getAssociationCount() {
        return associationCount;
    }

    public void setAssociationCount(int associationCount) {
        this.associationCount = associationCount;
    }
}
