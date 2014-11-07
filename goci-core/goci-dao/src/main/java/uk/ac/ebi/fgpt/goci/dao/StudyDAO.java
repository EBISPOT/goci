package uk.ac.ebi.fgpt.goci.dao;

import uk.ac.ebi.fgpt.goci.model.Study;

import java.util.Collection;

/**
 * Created by dwelter on 06/11/14.
 */
public interface StudyDAO {

    TraitAssociationDAO getTraitAssociationDAO();

    Collection<Study> retrieveAllStudies();

//    Collection<TraitAssociation> retrieveAllTraitAssociations();
}
