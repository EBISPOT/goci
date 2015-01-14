package uk.ac.ebi.spot.goci.dao;

import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.util.Collection;

/**
 * Created by dwelter on 06/11/14.
 */
public interface SingleNucleotidePolymorphismDAO {

    Collection<SingleNucleotidePolymorphism> retrieveAllSNPs();

}
