package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.goci.model.UnpublishedAncestry;

public interface UnpublishedAncestryRepository extends JpaRepository<UnpublishedAncestry, Long> {}
