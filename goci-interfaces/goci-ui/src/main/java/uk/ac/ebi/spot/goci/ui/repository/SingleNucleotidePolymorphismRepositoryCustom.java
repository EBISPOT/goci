package uk.ac.ebi.spot.goci.ui.repository;

import uk.ac.ebi.spot.goci.ui.model.SingleNucleotidePolymorphism;

/**
 * Created by emma on 05/12/14.
 */
public interface SingleNucleotidePolymorphismRepositoryCustom {

    SingleNucleotidePolymorphism retrieveAllSNPDetails(String rsID);
}
