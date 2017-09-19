package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.MappingMetadata;

import java.util.Collection;

/**
 * Created by emma on 28/09/2015.
 *
 * @author emma
 *         <p>
 *         Repository for searching the mapping metadata table
 */
@RepositoryRestResource(exported = false)
public interface MappingMetadataRepository extends JpaRepository<MappingMetadata, Long> {

    @Query(value = "select * from mapping_metadata " +
                   "where USAGE_START_DATE = (select max(USAGE_START_DATE) from mapping_metadata)",
           nativeQuery = true)
    Collection<MappingMetadata> getLatestMapping();

}
