package uk.ac.ebi.spot.goci.repository;

import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.Association;

import java.util.Collection;

/**
 * Created by Dani on 27/11/2014.
 */
public interface TraitAssociationRepository {
    Association findBySnp(SingleNucleotidePolymorphism snp);

    Collection<? extends Association> findByStudy(Study study);

    Association findAll();
}
