package uk.ac.ebi.spot.goci.repository;

import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

/**
 * Created by Dani on 27/11/2014.
 */
public interface SnpRepository {
    SingleNucleotidePolymorphism findByRsId(String rsId);

    Object findAll();
}
