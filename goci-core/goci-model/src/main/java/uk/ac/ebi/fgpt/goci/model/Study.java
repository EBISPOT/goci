package uk.ac.ebi.fgpt.goci.model;

import java.util.Collection;
import java.util.Date;

/**
 * A simple model object representing the basic information in a genome-wide association study.  A study identifies a
 * series of SNP/Trait associations.
 *
 * @author Tony Burdett
 * Date 24/01/12
 */
public interface Study extends GWASObject {
    /**
     * The primary author listed as the author of the publication that describes this study.
     *
     * @return the author name
     */
    String getAuthorName();

    /**
     * The ID of the publication that describes this study, as given by the PubMed database
     *
     * @return the PubMed ID of the paper describing this study
     */
    String getPubMedID();

    /**
     * The date this study was published to the GWAS catalog
     *
     * @return the publication date of this study
     */
    Date getPublishedDate();

    /**
     * The collection of trait associations identified by this study
     *
     * @return the associations identified in the publication
     */
    Collection<TraitAssociation> getIdentifiedAssociations();

    /**
     * The link title of this study, taken from publication title
     *
     * @return the link title of this study
     */

    String getTitle();

    /**
     * The platform used in this study
     *
     * @return the platform of this study
     */

    String getPlatform();

    /**
     * The journal in which the publication that describes this study was published.
     *
     * @return the publication journal
     */
    String getPublication();

}
