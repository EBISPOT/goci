package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.WeeklyProgressView;

//import uk.ac.ebi.spot.goci.model.PublicationToStudies;

import java.util.List;
import java.util.Date;
import java.util.Map;

/**
 * Created by emma on 08/06/2016.
 *
 * @author emma
 *         <p>
 *         Repository accessing weekly curator progress
 */
@RepositoryRestResource(exported = false)
public interface WeeklyProgressViewRepository extends JpaRepository<WeeklyProgressView, Long> {
    @Query("SELECT DISTINCT weekStartDay FROM WeeklyProgressView ORDER BY weekStartDay ASC") List<Date> getAllWeekStartDates();

    // Works with String[]
    @Query(value = "SELECT DISTINCT P.PUBMED_ID, listagg(S.ID, ',') WITHIN GROUP (ORDER BY S.ID) STUDY_IDS " +
            "FROM STUDY S, PUBLICATION P WHERE P.ID=S.PUBLICATION_ID GROUP BY P.PUBMED_ID" ,
            nativeQuery = true)
    List<Map.Entry> getAllPublicationToStudyMappings();
}
