package uk.ac.ebi.spot.goci.repository;

import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.TraitAssociation;

import java.util.Collection;

/**
 * Created by Dani on 27/11/2014.
 */
public interface TraitAssociationRepository {
    TraitAssociation findBySnp(SingleNucleotidePolymorphism snp);

    Collection<? extends TraitAssociation> findByStudy(Study study);

    TraitAssociation findAll();
}
