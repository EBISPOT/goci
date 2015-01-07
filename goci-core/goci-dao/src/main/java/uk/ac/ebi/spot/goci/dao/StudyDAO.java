package uk.ac.ebi.spot.goci.dao;

import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;

/**
 * Created by dwelter on 06/11/14.
 */
public interface StudyDAO {

    TraitAssociationDAO getTraitAssociationDAO();

    Collection<Study> retrieveAllStudies();

//    Collection<TraitAssociation> retrieveAllTraitAssociations();
}
