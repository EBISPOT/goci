package uk.ac.ebi.fgpt.goci.model;

import uk.ac.ebi.fgpt.goci.lang.UniqueID;

import java.util.Collection;
import java.util.Date;

/**
 * A simple model object representing the basic information in a genome-wide association study.  A study identifies a
 * series of SNP/Trait associations.
 *
 * @author Tony Burdett
 * @date 24/01/12
 */
public interface Study {
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
     * @return the assocations identified in the publication
     */
    Collection<TraitAssociation> getIdentifiedAssociations();
}
