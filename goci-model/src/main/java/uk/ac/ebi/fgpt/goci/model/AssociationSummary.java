package uk.ac.ebi.fgpt.goci.model;

import java.net.URI;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 01/09/14
 */
public interface AssociationSummary {
    String getPubMedID();

    String getFirstAuthor();

    String getPublicationDate();

    String getSNP();

    String getPvalue();

    String getGWASTraitName();

    String getEFOTraitLabel();

    URI getEFOTraitURI();
}
