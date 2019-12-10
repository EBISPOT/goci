package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.AncestryExtension;
import uk.ac.ebi.spot.goci.model.AssociationExtension;

@RepositoryRestResource(exported = false)
public interface AssociationExtensionRepository extends JpaRepository<AssociationExtension, Long> {}
