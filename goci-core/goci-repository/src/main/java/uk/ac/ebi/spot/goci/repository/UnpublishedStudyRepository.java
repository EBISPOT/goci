package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.UnpublishedStudy;

@RepositoryRestResource(exported = false)
public interface UnpublishedStudyRepository extends JpaRepository<UnpublishedStudy, Long> {
    public UnpublishedStudy findByAccession(String accessionID);
}
