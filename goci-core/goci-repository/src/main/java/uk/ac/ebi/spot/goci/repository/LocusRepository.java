package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.goci.model.Locus;

/**
 * Created by emma on 27/01/15.
 * @author emma
 *
 * Repository accessing Locus entity object
 */
public interface LocusRepository extends JpaRepository<Locus, Long> {
}
