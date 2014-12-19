//package uk.ac.ebi.spot.goci.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//import uk.ac.ebi.spot.goci.model.Snp;
//import uk.ac.ebi.spot.goci.model.Study;
//import uk.ac.ebi.spot.goci.model.TraitAssociation;
//
//import java.util.Collection;
//
///**
// * Created by Dani on 27/11/2014.
// */
//@RepositoryRestResource
//public interface TraitAssociationRepository  extends JpaRepository<TraitAssociation, Long> {
//    TraitAssociation findBySnp(Snp snp);
//
//    Collection<? extends TraitAssociation> findByStudy(Study study);
//
//    Collection<TraitAssociation> findByStudyID(String studyID);
//
//
//    //  TraitAssociation findAll();
//}
