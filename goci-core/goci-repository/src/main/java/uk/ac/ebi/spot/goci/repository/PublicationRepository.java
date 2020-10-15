package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.spot.goci.model.Publication;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    @RestResource(exported = false)
    Publication findByPubmedId(String pubmedId);


    // THOR to change
    // Custom query to get list of study authors
    @RestResource(exported = false)
    @Query(value = "select distinct a.fullname_standard from Publication p, Author a where a.id=p.first_author_id " +
            "order by a.fullname_standard asc",
            nativeQuery = true)
    List<String> findAllStudyAuthors();


    @RestResource(exported = false)
    List<Publication> findByFirstAuthorIsNull();

    @Query(value = "select distinct pubmed_id from Publication", nativeQuery = true)
    List<String> findAllPubmedIds();
}
