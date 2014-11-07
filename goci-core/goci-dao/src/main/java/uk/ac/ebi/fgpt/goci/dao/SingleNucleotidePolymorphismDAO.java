package uk.ac.ebi.fgpt.goci.dao;

import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;

import java.util.Collection;

/**
 * Created by dwelter on 06/11/14.
 */
public interface SingleNucleotidePolymorphismDAO {

    Collection<SingleNucleotidePolymorphism> retrieveAllSNPs();

}
