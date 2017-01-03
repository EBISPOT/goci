package uk.ac.ebi.spot.goci.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.StudyTrackingView;

import java.util.List;

/**
 * Created by Cinzia on 8/11/16.
 *
 * @author Cinzia
 *         <p>
 *        Repository for Study Tracking view
 */
@RepositoryRestResource(exported = false)
public interface StudyTrackingViewRepository extends JpaRepository<StudyTrackingView, Long>{

}
