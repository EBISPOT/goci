package uk.ac.ebi.spot.goci.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.sql.Date;

/**
 * A possible genome-wide association study that has been entered into this tracking system.  Each study is assigned, at
 * any given time, an owner.  Studies can be assigned one of several states, depending on the extent to which it has
 * been curated, and can have a flag set to indicate whether it is eligible or not eligible for the GWAS catalog.
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
public interface GwasStudy {


    /**
     * Returns the ID assigned to this study in PubMed
     *
     * @return a string representing the PubMed ID.
     */
    String getPubMedID();

    /**
     * Returns the title of the paper for this study
     *
     * @return the study's title
     */
    String getTitle();


    /**
     * Returns the first author for this study
     *
     * @return the author name
     */
    String getAuthor();

    /**
     * Returns the publication date for this study
     *
     * @return the publication date
     */
    Date getPublicationDate();

    /**
     * Returns the journal for this study
     *
     * @return the journal
     */
    String getPublication();


}
