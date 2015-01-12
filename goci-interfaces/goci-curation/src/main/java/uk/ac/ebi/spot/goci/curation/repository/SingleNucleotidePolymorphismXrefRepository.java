package uk.ac.ebi.spot.goci.curation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.curation.model.SingleNucleotidePolymorphismXref;

import java.util.Collection;

/**
 * Created by emma on 08/01/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing Single Nucloetide Polymorphism XREF entity object
 */
@RepositoryRestResource
public interface SingleNucleotidePolymorphismXrefRepository extends JpaRepository<SingleNucleotidePolymorphismXref, Long> {

    Collection<SingleNucleotidePolymorphismXref> findByAssociationID(Long associationID);

    Collection<SingleNucleotidePolymorphismXref> findBySnpID(Long snpID);

    SingleNucleotidePolymorphismXref findByAssociationIDAndSnpID(Long associationID, Long snpID);

}
