//package uk.ac.ebi.spot.goci.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//import uk.ac.ebi.spot.goci.model.CatalogSummaryView;
//
//import java.util.Collection;
//
///**
// * Created by emma on 17/02/15.
// *
// * @author emma
// *         <p>
// *         Repository accessing catalog summary view
// */
//@RepositoryRestResource
//public interface CatalogSummaryViewRepository extends JpaRepository<CatalogSummaryView, Long> {
//    Collection<CatalogSummaryView> findByCurationStatusOrderByStudyIdDesc(Long statusId);
//}
