package uk.ac.ebi.spot.goci.curation.repository;

import uk.ac.ebi.spot.goci.curation.model.SingleNucleotidePolymorphism;

import java.util.Collection;

/**
 * Created by emma on 05/12/14.
 */
public interface SingleNucleotidePolymorphismRepositoryCustom {

    SingleNucleotidePolymorphism retrieveAllSNPDetails(String rsID);
}
