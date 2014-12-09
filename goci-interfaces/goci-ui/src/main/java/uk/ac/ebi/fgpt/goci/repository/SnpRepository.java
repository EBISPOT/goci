package uk.ac.ebi.fgpt.goci.repository;

import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;

/**
 * Created by Dani on 27/11/2014.
 */
public interface SnpRepository {
    SingleNucleotidePolymorphism findByRsId(String rsId);
}
