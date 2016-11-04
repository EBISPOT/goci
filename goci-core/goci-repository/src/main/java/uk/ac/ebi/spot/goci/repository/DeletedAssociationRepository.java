package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.DeletedAssociation;

/**
 * Created by emma on 06/06/16.
 *
 * @author emma
 *         <p>
 *         Repository accessing Deleted Association entity object
 */
@RepositoryRestResource(exported = false)
public interface DeletedAssociationRepository extends JpaRepository<DeletedAssociation, Long> {
}

