package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.MappingMetadata;

/**
 * Created by emma on 28/09/2015.
 *
 * @author emma
 *         <p>
 *         Repository for searching the mapping metadata table
 */
@RepositoryRestResource(exported = false)
public interface MappingMetadataRepository extends JpaRepository<MappingMetadata, Long> {

}
