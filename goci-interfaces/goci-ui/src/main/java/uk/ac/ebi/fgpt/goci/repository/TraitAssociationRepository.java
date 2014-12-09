package uk.ac.ebi.fgpt.goci.repository;

import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.util.Collection;

/**
 * Created by Dani on 27/11/2014.
 */
public interface TraitAssociationRepository {
    TraitAssociation findBySnp(SingleNucleotidePolymorphism snp);

    Collection<? extends TraitAssociation> findByStudy(Study study);

    TraitAssociation findAll();
}
