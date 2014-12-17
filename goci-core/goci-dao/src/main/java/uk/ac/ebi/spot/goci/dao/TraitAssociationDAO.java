package uk.ac.ebi.spot.goci.dao;

import uk.ac.ebi.spot.goci.model.TraitAssociation;

import java.util.Collection;

/**
 * Created by dwelter on 06/11/14.
 */
public interface TraitAssociationDAO {

    Collection<TraitAssociation> retrieveAllTraitAssociations();
}
