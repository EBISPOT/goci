package uk.ac.ebi.fgpt.goci.dao;

import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.util.Collection;

/**
 * Created by dwelter on 06/11/14.
 */
public interface TraitAssociationDAO {

    Collection<TraitAssociation> retrieveAllTraitAssociations();
}
